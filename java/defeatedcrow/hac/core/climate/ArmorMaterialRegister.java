package defeatedcrow.hac.core.climate;

import java.util.HashMap;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import defeatedcrow.hac.api.damage.IArmorMaterialRegister;

public class ArmorMaterialRegister implements IArmorMaterialRegister {

	private HashMap<ArmorMaterial, Float> map;

	public ArmorMaterialRegister() {
		this.map = new HashMap<ArmorMaterial, Float>();
	}

	@Override
	public HashMap<ArmorMaterial, Float> getArmorMap() {
		return map;
	}

	@Override
	public void RegisterMaterial(ArmorMaterial material, float amount) {
		if (!map.containsKey(material)) {
			map.put(material, amount);
		}
	}

	@Override
	public float getPreventAmount(ArmorMaterial material) {
		if (map.containsKey(material)) {
			float ret = map.get(material);
			return ret;
		}
		return 0.5F;
	}

}
