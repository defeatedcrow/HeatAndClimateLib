package defeatedcrow.hac.config.recipe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import defeatedcrow.hac.core.util.JsonUtilDC;
import net.minecraft.item.ItemStack;

public class ClimateSmeltingJson {

	public static ClimateSmeltingJson INSTANCE = new ClimateSmeltingJson();

	private static final Map<String, Map<String, Object>> jsonMap = new LinkedHashMap<>();
	private static final Map<String, Map<String, Object>> dummyMap = new LinkedHashMap<>();

	private static void load() {
		if (!INSTANCE.jsonMap.isEmpty()) {
			for (Entry<String, Map<String, Object>> e : INSTANCE.jsonMap.entrySet()) {
				if (e.getKey() == null || e.getValue() == null || e.getValue().isEmpty()) {
					continue;
				}
				String recipeID = e.getKey();
				if (recipeID != null && !recipeID.contains("Sample")) {

					Map<String, Object> map = e.getValue();
					if (map != null && !map.isEmpty()) {

						EnumRecipeReg reg = EnumRecipeReg.ADD;
						List<ItemStack> list = Lists.newArrayList();
						ItemStack output = ItemStack.EMPTY;

						DCHeatTier heat = DCHeatTier.NORMAL;
						DCHumidity hum = null;
						DCAirflow air = null;
						if (map.containsKey("climate_temperature")) {
							Object o1 = map.get("climate_temperature");
							if (o1 instanceof String) {
								String heat2 = (String) o1;
								heat = DCHeatTier.getFromName(heat2);
							}
						}
						if (map.containsKey("climate_humidity")) {
							Object o2 = map.get("climate_humidity");
							if (o2 instanceof String) {
								String hum2 = (String) o2;
								hum = DCHumidity.getFromName(hum2);
							}
						}
						if (map.containsKey("climate_airflow")) {
							Object o3 = map.get("climate_airflow");
							if (o3 instanceof String) {
								String air2 = (String) o3;
								air = DCAirflow.getFromName(air2);
							}
						}

						if (map.containsKey("recipe_type")) {
							Object o1 = map.get("recipe_type");
							if (o1 instanceof String) {
								String r2 = (String) o1;
								reg = EnumRecipeReg.getFromName(r2);
							}
						}
						if (map.containsKey("input")) {
							Object o2 = map.get("input");
							if (o2 instanceof Map) {
								try {
									Map<String, Object> inputs = (Map) o2;
									list = JsonUtilDC.getIngredient(inputs);
								} catch (Error err) {
									DCLogger.debugLog("ClimateSmeltingJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("ClimateSmeltingJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}
						if (map.containsKey("output")) {
							Object o2 = map.get("output");
							if (o2 instanceof Map) {
								try {
									Map<String, Object> iunputs = (Map) o2;
									output = JsonUtilDC.getOutput(iunputs);
								} catch (Error err) {
									DCLogger.debugLog("ClimateSmeltingJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("ClimateSmeltingJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (reg == EnumRecipeReg.ADD && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (addRecipe(output, heat, hum, air, list))
								DCLogger.debugLog("ClimateSmeltingJson : Successfully added a ClimateSmelting recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REPLACE && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (changeRecipe(output, heat, hum, air, list))
								DCLogger.debugLog("ClimateSmeltingJson : Successfully replaced a ClimateSmelting recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REMOVE && !DCUtil.isEmpty(output)) {
							if (removeRecipe(output, heat, hum, air))
								DCLogger.debugLog("ClimateSmeltingJson : Successfully removed a ClimateSmelting recipe. " + recipeID);
						}
					}
				}
			}
		}
	}

	private static boolean addRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			List<ItemStack> input) {
		RecipeAPI.registerSmelting.addRecipe(output, heat, hum, air, input);
		return true;
	}

	private static boolean changeRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			List<ItemStack> input) {
		if (removeRecipe(output, heat, hum, air)) {
			return addRecipe(output, heat, hum, air, input);
		}
		return false;
	}

	private static boolean removeRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air) {
		IClimate clm = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return RecipeAPI.registerSmelting.removeRecipe(clm, output);
	}

	public static void pre() {
		File file = new File(ClimateConfig.configDir + "/recipe/climate_smelting.json");

		if (file != null) {
			try {
				if (!file.exists()) {
					return;
				}

				if (file.canRead()) {
					FileInputStream fis = new FileInputStream(file.getPath());
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

		if (!INSTANCE.jsonMap.isEmpty()) {
			load();
		}
	}

	// 生成は初回のみ
	public static void post() {

		if (INSTANCE.jsonMap.isEmpty()) {
			registerDummy();

			File file = new File(ClimateConfig.configDir + "/recipe/climate_smelting.json");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			try {
				if (!file.exists() && !file.createNewFile()) {
					return;
				}

				if (file.canWrite()) {
					FileOutputStream fos = new FileOutputStream(file.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent("    ");
					Gson gson = new Gson();
					gson.toJson(INSTANCE.dummyMap, Map.class, jsw);

					osw.close();
					fos.close();
					jsw.close();
				}

			} catch (IOException e) {
				DCLogger.debugInfoLog("ClimateSmeltingJson : Failed to create json file: climate_smelting.json");
				e.printStackTrace();
			}

		} else {
			DCLogger.debugInfoLog("ClimateSmeltingJson : Recipe custom data json is already exists.");
			return;
		}

	}

	public static void registerDummy() {

		String id1 = "SampleRecipeID1 (Any string.)";
		Map<String, Object> recipe1 = new LinkedHashMap<>();
		recipe1.put("recipe_type", "ADD");

		Map<String, Object> output1 = new HashMap<>();
		output1.put("item", "samplemod:sample_output:metadata");
		recipe1.put("output", output1);

		recipe1.put("climate_temperature", "KILN (If this is removed, it will be forced to NORMAL.)");
		recipe1.put("climate_humidity", "DRY (If this is removed, it will be unspecified.)");
		recipe1.put("climate_airflow", "TIGHT (If this is removed, it will be unspecified.)");

		Map<String, Object> input1 = new HashMap<>();
		input1.put("ore_dict", "ore_dict name is acceptable as input.");
		recipe1.put("input", input1);
		INSTANCE.dummyMap.put(id1, recipe1);

		String id2 = "SampleRecipeID2";
		Map<String, Object> recipe2 = new LinkedHashMap<>();
		recipe2.put("recipe_type", "REMOVE");
		Map<String, Object> output2 = new HashMap<>();
		output2.put("item", "samplemod:remove_output:0");
		recipe2.put("output", output2);
		recipe2.put("climate_temperature", "COLD");
		recipe2.put("climate_humidity", "WET");
		INSTANCE.dummyMap.put(id2, recipe2);

	}

}
