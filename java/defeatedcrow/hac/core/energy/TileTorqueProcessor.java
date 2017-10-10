package defeatedcrow.hac.core.energy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * 加工機能とSidedInventory持ちTorqueTileのベースクラス
 */
public abstract class TileTorqueProcessor extends TileTorqueLockable implements ISidedInventory {

	// process
	public int currentBurnTime = 0;
	public int maxBurnTime = -1;

	private int lastTier = 0;
	private int lastBurn = 0;
	private int tickCount = 5;

	@Override
	protected void onServerUpdate() {
		super.onServerUpdate();
		if (tickCount <= 0) {
			tickCount = 5;
			// 完了処理
			if (this.maxBurnTime > 0) {
				if (this.currentBurnTime >= this.maxBurnTime) {
					// レシピ進行の再チェック
					if (this.isRecipeMaterial()) {
						if (this.onProcess()) {
							this.currentBurnTime = 0;
							this.maxBurnTime = 0;
							this.markDirty();
						}
					} else {
						// 一致しないためリセット
						this.currentBurnTime = 0;
						this.maxBurnTime = 0;
					}
				} else {
					// レシピ進行の再チェック
					if (this.isRecipeMaterial()) {
						this.currentBurnTime += this.prevTorque;
					} else {
						// 一致しないためリセット
						this.currentBurnTime = 0;
						this.maxBurnTime = 0;
					}
				}
			} else if (this.canStartProcess()) {
				// レシピ開始可能かどうか
				this.maxBurnTime = this.getProcessTime();
			}
		} else {
			tickCount--;
		}
	}

	public boolean isActive() {
		return this.currentBurnTime > 0;
	}

	public int getCurrentBurnTime() {
		return this.currentBurnTime;
	}

	public int getMaxBurnTime() {
		return this.maxBurnTime;
	}

	public void setCurrentBurnTime(int i) {
		this.currentBurnTime = i;
	}

	public void setMaxBurnTime(int i) {
		this.maxBurnTime = i;
	}

	/* === レシピ判定 === */

	public abstract int getProcessTime();

	public abstract boolean isRecipeMaterial();

	public abstract boolean canStartProcess();

	public abstract boolean onProcess();

	/** itemの減少数を返す */
	public int insertResult(ItemStack item) {
		if (DCUtil.isEmpty(item))
			return 0;
		for (int i = 1; i < this.getSizeInventory(); i++) {
			if (DCUtil.isEmpty(getStackInSlot(i))) {
				this.incrStackInSlot(i, item.copy());
				return item.getCount();
			} else {
				int size = this.isItemStackable(item, this.getStackInSlot(i));
				if (size > 0) {
					DCUtil.addStackSize(this.getStackInSlot(i), size);
					return size;
				}
			}
		}
		return 0;
	}

	/* ========== 以下、ISidedInventoryのメソッド ========== */

	protected int[] slotsTop() {
		return new int[] {
				0
		};
	};

	protected int[] slotsBottom() {
		return new int[] {
				1, 2
		};
	};

	protected int[] slotsSides() {
		return new int[] {
				0, 1, 2
		};
	};

	public ItemStack[] inv = new ItemStack[this.getSizeInventory()];

	public List<ItemStack> getInputs() {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		if (!DCUtil.isEmpty(inv[0]))
			ret.add(inv[0]);
		return ret;
	}

	public List<ItemStack> getOutputs() {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = 1; i < this.getSizeInventory(); i++) {
			if (!DCUtil.isEmpty(inv[i]))
				ret.add(inv[i]);
		}
		return ret;
	}

	// スロット数
	@Override
	public int getSizeInventory() {
		return 3;
	}

	// インベントリ内の任意のスロットにあるアイテムを取得
	@Override
	public ItemStack getStackInSlot(int i) {
		if (i < getSizeInventory())
			return this.inv[i];
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		if (i < 0 || i >= this.getSizeInventory())
			return null;
		if (!DCUtil.isEmpty(inv[i])) {
			ItemStack itemstack;

			if (this.inv[i].getCount() <= num) {
				itemstack = this.inv[i];
				this.inv[i] = null;
				return itemstack;
			} else {
				itemstack = this.inv[i].splitStack(num);
				return itemstack;
			}
		} else
			return null;
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (i < 0 || i >= this.getSizeInventory()) {
			return;
		} else {
			this.inv[i] = stack;

			if (!DCUtil.isEmpty(stack) && stack.getCount() > this.getInventoryStackLimit()) {
				stack.setCount(this.getInventoryStackLimit());
			}
		}
	}

	// インベントリの名前
	@Override
	public String getName() {
		return "dcs.gui.device.processor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	// インベントリ内のスタック限界値
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	// par1EntityPlayerがTileEntityを使えるかどうか
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (getWorld().getTileEntity(this.pos) != this || player == null)
			return false;
		else
			return Math.sqrt(player.getDistanceSq(pos)) < 256D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return i > 0 ? false : true;
	}

	// ホッパーにアイテムの受け渡しをする際の優先度
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? slotsBottom() : (side == EnumFacing.UP ? slotsTop() : slotsSides());
	}

	// ホッパーからアイテムを入れられるかどうか
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	// 隣接するホッパーにアイテムを送れるかどうか
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (index == 0) {
			return false;
		}

		return true;
	}

	// 追加メソッド
	public static int isItemStackable(ItemStack target, ItemStack current) {
		if (DCUtil.isEmpty(target) || DCUtil.isEmpty(current))
			return 0;

		if (target.getItem() == current.getItem() && target.getMetadata() == current.getMetadata()
				&& ItemStack.areItemStackTagsEqual(target, current)) {
			int i = current.getCount() + target.getCount();
			if (i > current.getMaxStackSize()) {
				i = current.getMaxStackSize() - current.getCount();
				return i;
			}
			return target.getCount();
		}

		return 0;
	}

	public void incrStackInSlot(int i, ItemStack input) {
		if (i < this.getSizeInventory() && !DCUtil.isEmpty(input)) {
			if (!DCUtil.isEmpty(inv[i])) {
				if (this.inv[i].getItem() == input.getItem() && this.inv[i].getMetadata() == input.getMetadata()) {
					DCUtil.addStackSize(inv[i], input.getCount());
					if (this.inv[i].getCount() > this.getInventoryStackLimit()) {
						this.inv[i].setCount(this.getInventoryStackLimit());
					}
				}
			} else {
				this.setInventorySlotContents(i, input);
			}
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		i = MathHelper.clamp(i, 0, this.getSizeInventory() - 1);
		if (i < inv.length) {
			if (!DCUtil.isEmpty(inv[i])) {
				ItemStack itemstack = this.inv[i];
				this.inv[i] = ItemStack.EMPTY;
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return this.currentBurnTime;
		case 1:
			return this.maxBurnTime;
		case 2:
			return this.current == null ? 0 : this.current.getClimateInt();
		default:
			return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.currentBurnTime = value;
			break;
		case 1:
			this.maxBurnTime = value;
			break;
		case 2:
			this.current = ClimateAPI.register.getClimateFromInt(value);
		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.inv.length; ++i) {
			this.inv[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
	IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	/* Packet,NBT */

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList nbttaglist = tag.getTagList("InvItems", 10);
		this.inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = tag1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inv.length) {
				this.inv[b0] = new ItemStack(tag1);
			}
		}

		this.currentBurnTime = tag.getInteger("BurnTime");
		this.maxBurnTime = tag.getInteger("MaxTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		// 燃焼時間や調理時間などの書き込み
		tag.setInteger("BurnTime", this.currentBurnTime);
		tag.setInteger("MaxTime", this.maxBurnTime);

		// アイテムの書き込み
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; ++i) {
			if (!DCUtil.isEmpty(inv[i])) {
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setByte("Slot", (byte) i);
				inv[i].writeToNBT(tag1);
				nbttaglist.appendTag(tag1);
			}
		}
		tag.setTag("InvItems", nbttaglist);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		// 燃焼時間や調理時間などの書き込み
		tag.setInteger("BurnTime", this.currentBurnTime);
		tag.setInteger("MaxTime", this.maxBurnTime);

		// アイテムの書き込み
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; ++i) {
			if (!DCUtil.isEmpty(inv[i])) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inv[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		tag.setTag("InvItems", nbttaglist);
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);

		NBTTagList nbttaglist = tag.getTagList("InvItems", 10);
		this.inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inv.length) {
				this.inv[b0] = new ItemStack(nbttagcompound1);
			}
		}

		this.currentBurnTime = tag.getInteger("BurnTime");
		this.maxBurnTime = tag.getInteger("MaxTime");
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new SPacketUpdateTileEntity(pos, -50, nbtTagCompound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public abstract Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn);

	@Override
	public abstract String getGuiID();

}
