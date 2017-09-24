package defeatedcrow.hac.api.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

/**
 * めっきツール<br>
 * 金床で使用すると、対象のアイテムにエンチャントを付与します。
 */
public interface IPlatingTool {

	Enchantment[] getEnchantments(int meta);

	boolean canEnchant(ItemStack target, ItemStack tool);

	ItemStack getEnchantedItem(ItemStack target, ItemStack tool);

}
