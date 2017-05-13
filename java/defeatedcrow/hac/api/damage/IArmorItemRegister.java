package defeatedcrow.hac.api.damage;

import java.util.HashMap;

import defeatedcrow.hac.api.climate.ItemSet;
import net.minecraft.item.ItemStack;

/**
 * 気候ダメージへの耐性を防具Itemごとに登録
 */
public interface IArmorItemRegister {

	HashMap<ItemSet, Float> getHeatMap();

	HashMap<ItemSet, Float> getColdMap();

	void registerMaterial(ItemStack item, float heat, float cold);

	float getHeatPreventAmount(ItemStack item);

	float getColdPreventAmount(ItemStack item);

}
