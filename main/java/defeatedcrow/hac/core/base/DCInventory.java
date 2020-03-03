package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class DCInventory implements IInventory {

	private final int size;
	public final NonNullList<ItemStack> inv;

	public DCInventory(int i) {
		size = i;
		inv = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	public List<ItemStack> getInputs(int from, int to) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (DCUtil.isEmpty(getStackInSlot(i))) {
				ret.add(ItemStack.EMPTY);
			} else {
				ret.add(getStackInSlot(i));
			}
		}
		return ret;
	}

	public List<ItemStack> getOutputs(int from, int to) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (DCUtil.isEmpty(getStackInSlot(i))) {
				ret.add(ItemStack.EMPTY);
			} else {
				ret.add(getStackInSlot(i));
			}
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
			return inv.get(i);
		} else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		if (i < 0 || i >= this.getSizeInventory())
			return ItemStack.EMPTY;
		if (!DCUtil.isEmpty(getStackInSlot(i))) {
			ItemStack item = getStackInSlot(i).splitStack(num);
			if (item.getCount() <= 0) {
				item = ItemStack.EMPTY;
			}
			return item;
		} else
			return ItemStack.EMPTY;
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (i < 0 || i >= this.getSizeInventory()) {
			return;
		} else {
			if (stack == null) {
				stack = ItemStack.EMPTY;
			}

			inv.set(i, stack);

			if (!DCUtil.isEmpty(stack) && stack.getCount() > this.getInventoryStackLimit()) {
				stack.setCount(this.getInventoryStackLimit());
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
		if (DCUtil.isSameItem(target, current, false)) {
			int i1 = DCUtil.getSize(current) + DCUtil.getSize(target);
			if (!DCUtil.isEmpty(current) && i1 >= current.getMaxStackSize()) {
				return current.getMaxStackSize() - DCUtil.getSize(current);
			} else {
				return DCUtil.getSize(target);
			}
		}
		return 0;
	}

	public int canIncr(int i, ItemStack get) {
		if (i < 0 || i >= this.getSizeInventory() || DCUtil.isEmpty(get))
			return 0;
		else if (DCUtil.isEmpty(getStackInSlot(i)))
			return DCUtil.getSize(get);
		else {
			return isItemStackable(get, getStackInSlot(i));
		}
	}

	public int incrStackInSlot(int i, ItemStack input) {
		if (i >= 0 || i < this.getSizeInventory() && !DCUtil.isEmpty(input)) {
			if (!DCUtil.isEmpty(getStackInSlot(i))) {
				int add = isItemStackable(input, getStackInSlot(i));
				DCUtil.addStackSize(getStackInSlot(i), add);
				return add;
			} else {
				this.setInventorySlotContents(i, input);
				return DCUtil.getSize(input);
			}
		}
		return 0;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (i < 0 || i >= this.getSizeInventory())
			return ItemStack.EMPTY;
		else {
			if (!DCUtil.isEmpty(getStackInSlot(i))) {
				ItemStack itemstack = this.getStackInSlot(i);
				inv.set(i, ItemStack.EMPTY);
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	public int canInsertResult(ItemStack item, int s1, int s2) {
		if (DCUtil.isEmpty(item))
			return -1;
		ItemStack ret = item.copy();
		int count = 0;
		for (int i = s1; i < s2; i++) {
			int l = 0;
			if (DCUtil.isEmpty(this.getStackInSlot(i))) {
				l += ret.getCount();
			} else {
				l += this.isItemStackable(item, this.getStackInSlot(i));
			}
			ret.splitStack(l);
			count += l;
			if (ret.isEmpty())
				break;
		}
		return count;
	}

	/** itemの減少数を返す */
	public int insertResult(ItemStack item, int s1, int s2) {
		if (DCUtil.isEmpty(item))
			return 0;
		ItemStack ret = item.copy();
		int count = 0;
		for (int i = s1; i < s2; i++) {
			int l = 0;
			if (DCUtil.isEmpty(this.getStackInSlot(i))) {
				this.incrStackInSlot(i, ret.copy());
				l += ret.getCount();
			} else {
				int size = this.isItemStackable(ret, this.getStackInSlot(i));
				if (size > 0) {
					DCUtil.addStackSize(this.getStackInSlot(i), size);
					l += size;
				}
			}
			ret.splitStack(l);
			count += l;
			if (ret.isEmpty())
				break;
		}
		return count;
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
			inv.set(i, ItemStack.EMPTY);
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
				inv.set(b0, new ItemStack(tag1));
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
