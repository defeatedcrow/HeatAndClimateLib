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

import defeatedcrow.hac.core.DCLogger;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ArmorResistantRegister {

	public static final Map<ArmorMaterial, Float> resistant = new HashMap<ArmorMaterial, Float>();

	private ArmorResistantRegister() {}

	public static final ArmorResistantRegister INSTANCE = new ArmorResistantRegister();

	public void registerArmorResistant(String name, float heat) {
		if (name != null) {
			DCLogger.debugLog("register target from json: " + name);
			for (ArmorMaterial target : ArmorMaterial.values()) {
				if (target.getName().equalsIgnoreCase(name)) {
					ArmorMaterialRegister.RegisterMaterialFromJson(target, heat);
				}
			}
		}
	}

	/* json */
	private static Map<String, Object> jsonMap = new HashMap<String, Object>();
	protected static Map<String, Map<String, Float>> floatMap = new HashMap<String, Map<String, Float>>();

	private static File dir = null;

	public static void startMap() {
		if (!jsonMap.isEmpty()) {
			for (Entry<String, Object> ent : jsonMap.entrySet()) {
				if (ent != null) {
					String name = ent.getKey();
					Object value = ent.getValue();
					float heat = 0;
					if (value instanceof Map) {
						String h = ((Map) value).get("resistant").toString();
						heat = Float.parseFloat(h);
					}
					INSTANCE.registerArmorResistant(name, heat);
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
		final float resistant;

		private FloatSet(float f1) {
			resistant = f1;
		}
	}

	public static void setDir(File file) {
		dir = new File(file, "defeatedcrow/climate/armor_material_resistant.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}
}
