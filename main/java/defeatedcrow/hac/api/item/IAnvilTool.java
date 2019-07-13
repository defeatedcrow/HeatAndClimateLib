package defeatedcrow.hac.api.item;

import net.minecraft.item.ItemStack;

/**
 * 金床ツール<br>
 * 金床でアイテムに対して使用できるツール。
 */
public interface IAnvilTool {

	boolean canEnhance(ItemStack target, ItemStack tool);

	ItemStack getEnhancedItem(ItemStack target, ItemStack tool);

	int getMaterialCost(int meta);

	int getCost(int meta);

}
