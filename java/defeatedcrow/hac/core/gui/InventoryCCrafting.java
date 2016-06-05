package defeatedcrow.hac.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCCrafting implements IInventory {

	private final ItemStack[] items;
	private final Container cont;

	public InventoryCCrafting(Container c, int i) {
		this.items = new ItemStack[i];
		this.cont = c;
	}

	@Override
	public String getName() {
		return "dcs.climate.inv_craft";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.getName(), new Object[0]);
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index >= this.getSizeInventory())
			index = this.getSizeInventory() - 1;
		return items[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.items[index] != null) {
			if (this.items[index].stackSize <= count) {
				ItemStack itemstack1 = this.items[index];
				this.items[index] = null;
				this.cont.onCraftMatrixChanged(this);
				return itemstack1;
			} else {
				ItemStack itemstack = this.items[index].splitStack(count);

				if (this.items[index].stackSize == 0) {
					this.items[index] = null;
				}

				this.cont.onCraftMatrixChanged(this);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (this.items[index] != null) {
			ItemStack itemstack = this.items[index];
			this.items[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index >= this.getSizeInventory())
			index = this.getSizeInventory() - 1;
		this.items[index] = stack;
		this.cont.onCraftMatrixChanged(this);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

}
