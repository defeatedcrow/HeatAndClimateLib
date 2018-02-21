package defeatedcrow.hac.core.client.base;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerBaseDC extends Container {

	protected abstract int inputMinIndex();

	protected abstract int inputMaxIndex();

	protected abstract int slotIndex();

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < slotIndex()) {
				if (!this.mergeItemStack(itemstack1, slotIndex(), slotIndex() + 35, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(itemstack1, itemstack);
			} else {
				if (!this.mergeItemStack(itemstack1, inputMinIndex(), inputMaxIndex(), false))
					return ItemStack.EMPTY;
			}

			if (!DCUtil.isEmpty(itemstack1)) {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}

}
