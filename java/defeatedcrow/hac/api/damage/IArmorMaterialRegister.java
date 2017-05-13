package defeatedcrow.hac.api.damage;

import java.util.HashMap;

import net.minecraft.item.ItemArmor.ArmorMaterial;

/**
 * 気候ダメージへの耐性をArmorMaterialごとに登録するためのもの。
 */
public interface IArmorMaterialRegister {

	HashMap<ArmorMaterial, Float> getHeatMap();

	HashMap<ArmorMaterial, Float> getColdMap();

	void registerMaterial(ArmorMaterial material, float heat, float cold);

	void registerMaterial(ArmorMaterial material, float f);

	float getHeatPreventAmount(ArmorMaterial material);

	float getColdPreventAmount(ArmorMaterial material);

}
