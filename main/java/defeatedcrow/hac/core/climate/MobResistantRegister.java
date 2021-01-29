package defeatedcrow.hac.core.climate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.damage.IMobHeatResistant;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class MobResistantRegister implements IMobHeatResistant {

	public static final Map<Class<? extends Entity>, MobResistanceData> heatResistant = new HashMap<>();

	private MobResistantRegister() {}

	public static final MobResistantRegister INSTANCE = new MobResistantRegister();

	@Override
	public float getHeatResistant(ResourceLocation name, DCHeatTier temp) {
		if (name != null && temp != null) {
			MobResistanceData data = getEntityResistant(name);
			if (data != null) {
				float ret = data.resistanceValue_Temp;
				boolean cold = temp.isCold();
				float aj = Math.abs(data.suitableTemp.getTier());
				if (cold == data.suitableTemp.isCold()) {
					ret += aj;
				} else {
					ret -= aj;
				}
			}
		}
		return 2.0F;
	}

	@Override
	public float getHeatResistant(Entity entity, DCHeatTier temp) {
		float ret = 2.0F;
		if (entity != null && temp != null) {
			boolean cold = temp.isCold();
			if (heatResistant.containsKey(entity.getClass())) {
				MobResistanceData data = heatResistant.get(entity.getClass());
				if (data != null) {
					ret = data.resistanceValue_Temp;
					float aj = Math.abs(data.suitableTemp.getTier());
					if (cold == data.suitableTemp.isCold()) {
						ret += aj;
					} else {
						ret -= aj;
					}
				}
			}
			if (cold) {
				if (entity instanceof EntityLivingBase) {
					if (((EntityLivingBase) entity).isPotionActive(DCInit.prevFreeze)) {
						ret += 4.0F;
					}
					if (((EntityLivingBase) entity).isEntityUndead()) {
						ret += 2.0F;
					}
				}
				if (entity.isImmuneToFire()) {
					ret -= 2.0F;
				}

			} else {
				if (entity instanceof EntityLivingBase) {
					if (((EntityLivingBase) entity).isPotionActive(MobEffects.FIRE_RESISTANCE)) {
						ret += 4.0F;
					}
					if (temp.getTier() > DCHeatTier.OVEN.getTier() && ((EntityLivingBase) entity).isEntityUndead()) {
						ret -= 4.0F;
					}
				}
				if (entity.isImmuneToFire()) {
					ret += CoreConfigDC.infernalInferno ? 8.0F : 4.0F;
				}
			}

		}
		return ret;
	}

	@Override
	public float getHumResistant(ResourceLocation name, DCHumidity hum) {
		if (name != null && hum != null) {
			MobResistanceData data = getEntityResistant(name);
			if (data != null) {
				float ret = data.resistanceValue_Hum;
				int i = data.suitableHum.getID() - hum.getID();
				i = Math.abs(i);
				ret -= i;
				return ret;
			}
		}
		return 2.0F;
	}

	@Override
	public float getHumResistant(Entity entity, DCHumidity hum) {
		float ret = 2.0F;
		if (entity != null && hum != null) {
			if (heatResistant.containsKey(entity.getClass())) {
				MobResistanceData data = heatResistant.get(entity.getClass());
				if (data != null) {
					ret = data.resistanceValue_Hum;
					int i = data.suitableHum.getID() - hum.getID();
					i = Math.abs(i);
					ret -= i;
				}
			} else {
				int i = DCHumidity.NORMAL.getID() - hum.getID();
				i = Math.abs(i);
				ret -= i;
			}
			if (hum.getID() > 1) {
				if (entity instanceof EntityLivingBase) {
					if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
						ret += 2.0F;
					}
				}
				if (entity instanceof EntityWaterMob) {
					ret += 2.0F;
				}
			}
		}
		return ret;
	}

	@Override
	public float getAirResistant(ResourceLocation name, DCAirflow air) {
		if (name != null && air != null) {
			MobResistanceData data = getEntityResistant(name);
			if (data != null) {
				float ret = data.resistanceValue_Air;
				int i = data.suitableAir.getID() - air.getID();
				i = Math.abs(i);
				ret -= i;
				return ret;
			}
		}
		return 2.0F;
	}

	@Override
	public float getAirResistant(Entity entity, DCAirflow air) {
		float ret = 2.0F;
		if (entity != null && air != null) {
			if (heatResistant.containsKey(entity.getClass())) {
				MobResistanceData data = heatResistant.get(entity.getClass());
				if (data != null) {
					ret = data.resistanceValue_Hum;
					int i = data.suitableHum.getID() - air.getID();
					i = Math.abs(i);
					ret -= i;
				}
			} else {
				int i = DCAirflow.FLOW.getID() - air.getID();
				i = Math.abs(i);
				ret -= i;
			}
			if (air.getID() < 1) {
				if (entity instanceof EntityLivingBase) {
					if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
						ret += 1.0F;
					}
					if (((EntityLivingBase) entity).isEntityUndead()) {
						ret += 1.0F;
					}
				}
			} else {
				if (entity.isAirBorne) {
					ret -= 1.0F;
				}
			}
		}
		return ret;
	}

	public MobResistanceData getEntityResistant(ResourceLocation name) {
		if (name != null) {
			if (EntityList.getClass(name) != null) {
				Class<? extends Entity> entity = EntityList.getClass(name);
				if (entity != null && heatResistant.containsKey(entity)) {
					return heatResistant.get(entity);
				}
			}
		}
		return null;
	}

	@Override
	public void registerEntityResistant(ResourceLocation name, DCHeatTier temp, float v1, DCHumidity hum, float v2,
			DCAirflow air, float v3) {
		Class<? extends Entity> c = getEntityClass(name);
		registerEntityResistant(c, temp, v1, hum, v2, air, v3);
	}

	@Override
	public void registerEntityResistant(ResourceLocation name, DCHeatTier temp, float v1) {
		Class<? extends Entity> c = getEntityClass(name);
		registerEntityResistant(c, temp, v1);
	}

	@Override
	public void registerEntityResistant(Class<? extends Entity> entityClass, DCHeatTier temp, float v1, DCHumidity hum,
			float v2, DCAirflow air, float v3) {
		if (entityClass != null) {
			if (heatResistant.containsKey(entityClass)) {
				return;
			}
			DCLogger.infoLog("success registering : " + entityClass
					.getSimpleName() + " " + temp + "/" + v1 + " " + hum + "/" + v2 + " " + air + "/" + v3);
			if (EntityList.getKey(entityClass) != null) {
				MobResistanceData data = new MobResistanceData(temp, v1, hum, v2, air, v3);
				heatResistant.put(entityClass, data);
			}
		}
	}

	@Override
	public void registerEntityResistant(Class<? extends Entity> entityClass, DCHeatTier temp, float v1) {
		if (entityClass != null) {
			if (heatResistant.containsKey(entityClass)) {
				return;
			}
			DCLogger.infoLog("success registering : " + entityClass.getSimpleName() + " " + temp + "/" + v1);
			if (EntityList.getKey(entityClass) != null) {
				MobResistanceData data = new MobResistanceData(temp, v1);
				heatResistant.put(entityClass, data);
			}
		}
	}

	@Deprecated
	@Override
	public void registerEntityResistant(Class<? extends Entity> entityClass, float v1, float v2) {
		if (entityClass != null) {
			if (heatResistant.containsKey(entityClass)) {
				return;
			}
			int tier = MathHelper.floor(v1 - v2);
			DCHeatTier temp = DCHeatTier.getHeatEnum(tier);
			float res = v1 - tier;
			registerEntityResistant(entityClass, temp, res);
		}
	}

	@Deprecated
	@Override
	public void registerEntityResistant(ResourceLocation name, float v1, float v2) {
		Class<? extends Entity> c = getEntityClass(name);
		registerEntityResistant(c, v1, v2);
	}

	public Class<? extends Entity> getEntityClass(ResourceLocation name) {
		if (name != null) {
			if (EntityList.getClass(name) != null) {
				Class<? extends Entity> entity = EntityList.getClass(name);
				return entity;
			}
		}
		return null;
	}

	public Class<? extends Entity> getEntityClass(String s) {
		if (s != null) {
			ResourceLocation name = new ResourceLocation(s);
			DCLogger.debugLog("Trying register target from json: " + name.toString());
			if (EntityList.getClass(name) != null) {
				Class<? extends Entity> entity = EntityList.getClass(name);
				return entity;
			}
		}
		return null;
	}

	/* json */
	private static Map<String, Object> jsonMap = new HashMap<String, Object>();
	public static Map<String, MobResistanceData> floatMap = new HashMap<>();

	private static File dir = null;

	public static void startMap() {
		if (!INSTANCE.jsonMap.isEmpty()) {
			for (Entry<String, Object> ent : INSTANCE.jsonMap.entrySet()) {
				if (ent != null) {
					String name = ent.getKey();
					Class<? extends Entity> c1 = INSTANCE.getEntityClass(name);
					if (c1 == null)
						continue;

					Object value = ent.getValue();
					DCHeatTier temp = DCHeatTier.NORMAL;
					DCHumidity hum = DCHumidity.NORMAL;
					DCAirflow air = DCAirflow.FLOW;
					float value1 = 2.0F;
					float value2 = 2.0F;
					float value3 = 2.0F;
					if (value instanceof Map) {
						if (((Map) value).containsKey("suitableTemp")) {
							String v = ((Map) value).get("suitableTemp").toString();
							temp = DCHeatTier.getFromName(v);
						}
						if (((Map) value).containsKey("suitableHum")) {
							String v = ((Map) value).get("suitableHum").toString();
							hum = DCHumidity.getFromName(v);
						}
						if (((Map) value).containsKey("suitableAir")) {
							String v = ((Map) value).get("suitableAir").toString();
							air = DCAirflow.getFromName(v);
						}
						if (((Map) value).containsKey("resistanceValue_Temp")) {
							String v = ((Map) value).get("resistanceValue_Temp").toString();
							value1 = Float.parseFloat(v);
						}
						if (((Map) value).containsKey("resistanceValue_Hum")) {
							String v = ((Map) value).get("resistanceValue_Hum").toString();
							value2 = Float.parseFloat(v);
						}
						if (((Map) value).containsKey("resistanceValue_Air")) {
							String v = ((Map) value).get("resistanceValue_Air").toString();
							value3 = Float.parseFloat(v);
						}
					}
					INSTANCE.registerEntityResistant(c1, temp, value1, hum, value2, air, value3);
				}
			}
		} else {
			DCLogger.debugLog("no resistant json data.");
		}
	}

	public static void pre() {
		INSTANCE.jsonMap.clear();
		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				}

				if (dir.canRead()) {
					FileInputStream fis = new FileInputStream(dir.getPath());
					InputStreamReader isr = new InputStreamReader(fis);
					JsonReader jsr = new JsonReader(isr);
					Gson gson = new Gson();
					Map get = gson.fromJson(jsr, Map.class);

					isr.close();
					fis.close();
					jsr.close();

					if (get != null && !get.isEmpty()) {
						INSTANCE.jsonMap.putAll(get);

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		startMap();
	}

	// 生成は初回のみ
	public static void post() {

		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				} else if (!jsonMap.isEmpty()) {
					DCLogger.debugLog("resistant data json is already exists.");
					return;
				}

				if (dir.canWrite()) {
					INSTANCE.setDefaultMap();

					FileOutputStream fos = new FileOutputStream(dir.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(INSTANCE.floatMap, Map.class, jsw);

					osw.close();
					fos.close();
					jsw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class FloatSet {
		final float heat;
		final float cold;

		private FloatSet(float f1, float f2) {
			heat = f1;
			cold = f2;
		}
	}

	public static void setDir(File file) {
		dir = new File(file, "mob_climate_resistant.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}

	public void setDefaultMap() {
		putMap(EntityVillager.class, new MobResistanceData(DCHeatTier.NORMAL, 3.0F));
		putMap(EntitySnowman.class, new MobResistanceData(DCHeatTier.FROSTBITE, 4.0F));
		putMap(EntityIronGolem.class, new MobResistanceData(DCHeatTier.COLD, 6.0F));
		putMap(EntityWither.class, new MobResistanceData(DCHeatTier.OVEN, 4.0F));
		putMap(EntityDragon.class, new MobResistanceData(DCHeatTier.COLD, 4.0F));
		putMap(EntitySheep.class, new MobResistanceData(DCHeatTier.COOL, 3.0F));
		putMap(EntityPig.class, new MobResistanceData(DCHeatTier.WARM, 3.0F));
		putMap(EntityCow.class, new MobResistanceData(DCHeatTier.COOL, 3.0F));
		putMap(EntityHorse.class, new MobResistanceData(DCHeatTier.COOL, 3.0F));
		putMap(EntityChicken.class, new MobResistanceData(DCHeatTier.NORMAL, 3.0F));
		putMap(EntityOcelot.class, new MobResistanceData(DCHeatTier.WARM, 3.0F));
		putMap(EntityRabbit.class, new MobResistanceData(DCHeatTier.COOL, 2.0F));
		putMap(EntityWolf.class, new MobResistanceData(DCHeatTier.COOL, 3.0F));
		putMap(EntityPolarBear.class, new MobResistanceData(DCHeatTier.COLD, 3.0F));
		putMap(EntityLlama.class, new MobResistanceData(DCHeatTier.COOL, 4.0F));
		putMap(EntityEnderman.class, new MobResistanceData(DCHeatTier.COLD, 8.0F));
		putMap(EntityShulker.class, new MobResistanceData(DCHeatTier.COLD, 4.0F));
		putMap(EntityHusk.class, new MobResistanceData(DCHeatTier.NORMAL, 2.0F, DCHumidity.DRY, 2.0F, DCAirflow.NORMAL,
				2.0F));
	}

	private void putMap(Class<? extends Entity> key, MobResistanceData data) {
		floatMap.put(EntityList.getKey(key).toString(), data);
		heatResistant.put(key, data);
	}

	public class MobResistanceData {
		public final DCHeatTier suitableTemp;
		public final float resistanceValue_Temp;
		public final DCHumidity suitableHum;
		public final float resistanceValue_Hum;
		public final DCAirflow suitableAir;
		public final float resistanceValue_Air;

		public MobResistanceData(DCHeatTier temp, float v1, DCHumidity hum, float v2, DCAirflow air, float v3) {
			suitableTemp = temp;
			suitableHum = hum;
			suitableAir = air;
			resistanceValue_Temp = v1;
			resistanceValue_Hum = v2;
			resistanceValue_Air = v3;
		}

		public MobResistanceData(DCHeatTier temp, float v1) {
			suitableTemp = temp;
			suitableHum = DCHumidity.NORMAL;
			suitableAir = DCAirflow.FLOW;
			resistanceValue_Temp = v1;
			resistanceValue_Hum = 2.0F;
			resistanceValue_Air = 2.0F;
		}
	}

}
