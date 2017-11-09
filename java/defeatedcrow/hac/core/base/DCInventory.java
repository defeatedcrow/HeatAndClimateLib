package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class DCInventory implements IInventory {

	private final int size;
	public ItemStack[] inv;

	public DCInventory(int i) {
		size = i;
		inv = new ItemStack[i];
	}

	public List<ItemStack> getInputs(int from, int to) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (!DCUtil.isEmpty(getStackInSlot(i)))
				ret.add(getStackInSlot(i));
		}
		return ret;
	}

	public List<ItemStack> getOutputs(int from, int to) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (!DCUtil.isEmpty(getStackInSlot(i)))
				ret.add(getStackInSlot(i));
		}
		return ret;
	}

	// スロット数
	@Override
	public int getSizeInventory() {
		return size;
	}

	// インベントリ内の任意のスロットにあるアイテムを取得
	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= 0 && i < getSizeInventory()) {
			return inv[i];
		} else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		if (i < 0 || i >= this.getSizeInventory())
			return null;
		if (!DCUtil.isEmpty(getStackInSlot(i))) {
			ItemStack itemstack;
			itemstack = getStackInSlot(i).splitStack(num);
			if (getStackInSlot(i).stackSize <= 0) {
				setInventorySlotContents(i, null);
			}
			return itemstack;
		} else
			return null;
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (i < 0 || i >= this.getSizeInventory()) {
			return;
		} else {
			if (stack == null) {
				stack = null;
			}

			inv[i] = stack;

			if (!DCUtil.isEmpty(stack) && stack.stackSize > this.getInventoryStackLimit()) {
				stack.stackSize = getInventoryStackLimit();
			}

			this.markDirty();
		}
	}

	// インベントリの名前
	@Override
	public String getName() {
		return "dcs.gui.device.normal_inv";
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
	public void markDirty() {}

	// par1EntityPlayerがTileEntityを使えるかどうか
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		// Tile側で定義する
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return true;
	}

	// 追加メソッド
	public static int isItemStackable(ItemStack target, ItemStack current) {
		if (DCUtil.isEmpty(target) || DCUtil.isEmpty(current))
			return 0;

		if (target.getItem() == current.getItem() && target.getMetadata() == current.getMetadata()
				&& ItemStack.areItemStackTagsEqual(target, current)) {
			int i = current.stackSize + target.stackSize;
			if (i > current.getMaxStackSize()) {
				i = current.getMaxStackSize() - current.stackSize;
				return i;
			}
			return target.stackSize;
		}

		return 0;
	}

	public void incrStackInSlot(int i, ItemStack input) {
		if (i < this.getSizeInventory() && !DCUtil.isEmpty(input)) {
			if (!DCUtil.isEmpty(getStackInSlot(i))) {
				ItemStack stack = getStackInSlot(i);
				if (stack.getItem() == input.getItem() && stack.getMetadata() == input.getMetadata()) {
					DCUtil.addStackSize(stack, input.stackSize);
					if (stack.stackSize > this.getInventoryStackLimit()) {
						stack.stackSize = getInventoryStackLimit();
					}
				}
			} else {
				this.setInventorySlotContents(i, input);
			}
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (i < 0 || i >= this.getSizeInventory())
			return null;
		else {
			if (!DCUtil.isEmpty(getStackInSlot(i))) {
				ItemStack itemstack = this.getStackInSlot(i);
				inv[i] = null;
				return itemstack;
			}
		}
		return null;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			inv[i] = null;
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	/* Packet,NBT */

	public void readFromNBT(NBTTagCompound tag) {

		NBTTagList nbttaglist = tag.getTagList("InvItems", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = tag1.getByte("Slot");

			if (b0 >= 0 && b0 < this.getSizeInventory()) {
				inv[b0] = ItemStack.loadItemStackFromNBT(tag1);
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		// アイテムの書き込み
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (!DCUtil.isEmpty(getStackInSlot(i))) {
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(tag1);
				nbttaglist.appendTag(tag1);
			}
		}
		tag.setTag("InvItems", nbttaglist);
		return tag;
	}

}
