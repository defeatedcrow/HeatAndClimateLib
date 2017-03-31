package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.IMobHeatResistant;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class MobResistantRegister implements IMobHeatResistant {

	public static final Map<Class<? extends Entity>, Float> heatResistant = new HashMap<Class<? extends Entity>, Float>();
	public static final Map<Class<? extends Entity>, Float> coldResistant = new HashMap<Class<? extends Entity>, Float>();

	public MobResistantRegister() {}

	public static IMobHeatResistant instance() {
		return DamageAPI.resistantData;
	}

	@Override
	public float getHeatResistant(ResourceLocation name) {
		if (name != null) {
			String n = name.getResourceDomain() + "." + name.getResourcePath();
			DCLogger.debugLog("register target: " + n);
			if (heatResistant.containsKey(n)) {
				Class<? extends Entity> entity = EntityList.NAME_TO_CLASS.get(n);
				return heatResistant.get(entity);
			}
		}
		return 0.0F;
	}

	@Override
	public float getColdResistant(ResourceLocation name) {
		if (name != null) {
			String n = name.getResourceDomain() + "." + name.getResourcePath();
			DCLogger.debugLog("register target: " + n);
			if (coldResistant.containsKey(n)) {
				Class<? extends Entity> entity = EntityList.NAME_TO_CLASS.get(n);
				return coldResistant.get(entity);
			}
		}
		return 0.0F;
	}

	@Override
	public void registerEntityResistant(ResourceLocation name, float heat, float cold) {
		if (name != null) {
			String n = name.getResourceDomain() + "." + name.getResourcePath();
			DCLogger.debugLog("register target: " + n);
			if (EntityList.NAME_TO_CLASS.containsKey(n)) {
				Class<? extends Entity> entity = EntityList.NAME_TO_CLASS.get(n);
				registerEntityResistant(entity, heat, cold);
			}
		}
	}

	@Override
	public void registerEntityResistant(Class<? extends Entity> entityClass, float heat, float cold) {
		if (entityClass != null) {
			if (heat != 0 && !heatResistant.containsKey(entityClass)) {
				heatResistant.put(entityClass, heat);
			}
			if (cold != 0 && !coldResistant.containsKey(entityClass)) {
				coldResistant.put(entityClass, cold);
			}
			DCLogger.debugLog("success registering : " + entityClass.getSimpleName() + " " + heat + "/" + cold);
		}
	}

	@Override
	public float getHeatResistant(Entity entity) {
		if (entity != null && !heatResistant.isEmpty()) {
			if (heatResistant.containsKey(entity.getClass())) {
				return heatResistant.get(entity.getClass());
			}
			for (Class<? extends Entity> target : heatResistant.keySet()) {
				if (target.isInstance(entity)) {
					return heatResistant.get(target);
				}
			}
		}
		return 0;
	}

	@Override
	public float getColdResistant(Entity entity) {
		if (entity != null && !coldResistant.isEmpty()) {
			if (coldResistant.containsKey(entity.getClass())) {
				return coldResistant.get(entity.getClass());
			}
			for (Class<? extends Entity> target : coldResistant.keySet()) {
				if (target.isInstance(entity)) {
					return coldResistant.get(target);
				}
			}
		}
		return 0;
	}

}
