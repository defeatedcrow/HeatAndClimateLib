package defeatedcrow.hac.api.magic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract interface IMagicCost {

	/**
	 * Called before cost-consumable check.
	 * If false, the use of magic will be cancelled.
	 */
	boolean canUseMagic(EntityPlayer player, ItemStack stack);

	/**
	 * Called before cost consumption.
	 * If false, cost consumption will be cancelled. (The activation of magic is not affected.)
	 */
	boolean beforeConsumption(EntityPlayer player, ItemStack stack);

	float getCost(ItemStack item);

}
