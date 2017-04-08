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

import defeatedcrow.hac.api.damage.IMobHeatResistant;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class MobResistantRegister implements IMobHeatResistant {

	public static final Map<Class<? extends Entity>, Float> heatResistant = new HashMap<Class<? extends Entity>, Float>();
	public static final Map<Class<? extends Entity>, Float> coldResistant = new HashMap<Class<? extends Entity>, Float>();

	private MobResistantRegister() {}

	public static final MobResistantRegister INSTANCE = new MobResistantRegister();

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

	public void registerEntityResistant(String name, float heat, float cold) {
		if (name != null) {
			DCLogger.debugLog("register target from json: " + name);
			if (EntityList.NAME_TO_CLASS.containsKey(name)) {
				Class<? extends Entity> entity = EntityList.NAME_TO_CLASS.get(name);
				registerEntityResistant(entity, heat, cold);
			}
		}
	}

	@Override
	public void registerEntityResistant(Class<? extends Entity> entityClass, float heat, float cold) {
		if (entityClass != null) {
			if (heat != 0) {
				if (heatResistant.containsKey(entityClass)) {
					heatResistant.remove(entityClass);
				}
				heatResistant.put(entityClass, heat);
			}
			if (cold != 0) {
				if (coldResistant.containsKey(entityClass)) {
					coldResistant.remove(entityClass);
				}
				coldResistant.put(entityClass, cold);
			}
			DCLogger.debugLog("success registering : " + entityClass.getSimpleName() + " " + heat + "/" + cold);
			String name = EntityList.CLASS_TO_NAME.get(entityClass);
			if (name != null && !name.equalsIgnoreCase("Null")) {
				Map<String, Float> res = new HashMap<String, Float>();
				res.put("heat", heat);
				res.put("cold", cold);
				floatMap.put(name, res);
			}
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

	/* json */
	private static Map<String, Object> jsonMap = new HashMap<String, Object>();
	private static Map<String, Map<String, Float>> floatMap = new HashMap<String, Map<String, Float>>();

	private static File dir = null;

	public static void startMap() {
		if (!jsonMap.isEmpty()) {
			for (Entry<String, Object> ent : jsonMap.entrySet()) {
				if (ent != null) {
					String name = ent.getKey();
					Object value = ent.getValue();
					float heat = 0;
					float cold = 0;
					if (value instanceof Map) {
						String h = ((Map) value).get("heat").toString();
						String c = ((Map) value).get("cold").toString();
						heat = Float.parseFloat(h);
						cold = Float.parseFloat(c);
					}
					INSTANCE.registerEntityResistant(name, heat, cold);
				}
			}
		} else {
			DCLogger.debugLog("no resistant json data.");
		}
	}

	public static void pre() {
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
						jsonMap.putAll(get);

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
					FileOutputStream fos = new FileOutputStream(dir.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(floatMap, Map.class, jsw);

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
		dir = new File(file, "defeatedcrow/climate/mob_resistant.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}
}
