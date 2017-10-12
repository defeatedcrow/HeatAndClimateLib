package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class DCInventory implements IInventory {

	private final int size;

	public DCInventory(int i) {
		size = i;
	}

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
		if (size > 0)
			return size;
		else
			return 1;
	}

	// インベントリ内の任意のスロットにあるアイテムを取得
	@Override
	public ItemStack getStackInSlot(int i) {
		if (i < getSizeInventory())
			return this.inv[i];
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		if (i < 0 || i >= this.getSizeInventory())
			return ItemStack.EMPTY;
		if (!DCUtil.isEmpty(inv[i])) {
			ItemStack itemstack;
			itemstack = this.inv[i].splitStack(num);
			return itemstack;
		} else
			return ItemStack.EMPTY;
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
	public boolean isUsableByPlayer(EntityPlayer player) {
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
		for (int i = 0; i < this.inv.length; ++i) {
			this.inv[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	/* Packet,NBT */

	public void readFromNBT(NBTTagCompound tag) {

		NBTTagList nbttaglist = tag.getTagList("InvItems", 10);
		this.inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = tag1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inv.length) {
				this.inv[b0] = new ItemStack(tag1);
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

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
	public boolean isEmpty() {
		boolean flag = true;
		for (ItemStack item : inv) {
			if (!DCUtil.isEmpty(item))
				flag = false;
		}
		return flag;
	}

}
