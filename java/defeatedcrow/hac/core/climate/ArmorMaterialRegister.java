package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.damage.IArmorMaterialRegister;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ArmorMaterialRegister implements IArmorMaterialRegister {

	private static HashMap<ArmorMaterial, Float> map;

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
			Map<String, Float> res = new HashMap<String, Float>();
			res.put("resistant", amount);
			DCLogger.debugLog("register armor material: " + material.getName() + " " + amount);
			ArmorResistantRegister.INSTANCE.floatMap.put(material.getName(), res);
		}
	}

	protected static void RegisterMaterialFromJson(ArmorMaterial material, float amount) {
		if (!map.containsKey(material)) {
			map.remove(material);
		}
		map.put(material, amount);
		Map<String, Float> res = new HashMap<String, Float>();
		res.put("resistant", amount);
		DCLogger.debugLog("register armor material: " + material.getName() + " " + amount);
		ArmorResistantRegister.INSTANCE.floatMap.put(material.getName(), res);
	}

	@Override
	public float getPreventAmount(ArmorMaterial material) {
		if (map.containsKey(material)) {
			float ret = map.get(material);
			return ret;
		}
		return 0.25F;
	}

}
