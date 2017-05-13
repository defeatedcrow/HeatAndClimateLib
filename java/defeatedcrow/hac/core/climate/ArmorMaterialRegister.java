package defeatedcrow.hac.core.climate;

import java.util.HashMap;

import defeatedcrow.hac.api.damage.IArmorMaterialRegister;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ArmorMaterialRegister implements IArmorMaterialRegister {

	private static HashMap<ArmorMaterial, Float> heatMap;
	private static HashMap<ArmorMaterial, Float> coldMap;

	public ArmorMaterialRegister() {
		this.heatMap = new HashMap<ArmorMaterial, Float>();
		this.coldMap = new HashMap<ArmorMaterial, Float>();
	}

	@Override
	public HashMap<ArmorMaterial, Float> getHeatMap() {
		return heatMap;
	}

	@Override
	public HashMap<ArmorMaterial, Float> getColdMap() {
		return coldMap;
	}

	@Override
	public void registerMaterial(ArmorMaterial material, float heat, float cold) {
		if (!heatMap.containsKey(material) && !coldMap.containsKey(material)) {
			heatMap.put(material, heat);
			coldMap.put(material, cold);
			DCLogger.debugLog("register armor material: " + material + " heat " + heat + "/cold " + cold);
		}
	}

	@Override
	public void registerMaterial(ArmorMaterial material, float f) {
		registerMaterial(material, f, f);
	}

	@Override
	public float getHeatPreventAmount(ArmorMaterial material) {
		if (heatMap.containsKey(material)) {
			float ret = heatMap.get(material);
			return ret;
		}
		return 0.25F;
	}

	@Override
	public float getColdPreventAmount(ArmorMaterial material) {
		if (coldMap.containsKey(material)) {
			float ret = coldMap.get(material);
			return ret;
		}
		return 0.25F;
	}

}
