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
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipeJson {

	public static FluidRecipeJson INSTANCE = new FluidRecipeJson();

	private static final Map<String, Object> jsonMap = new LinkedHashMap<>();
	private static final Map<String, Object> dummyMap = new LinkedHashMap<>();

	private static void load() {
		if (!INSTANCE.jsonMap.isEmpty()) {
			for (Entry<String, Object> e : INSTANCE.jsonMap.entrySet()) {
				if (e == null || e.getValue() instanceof Map) {
					continue;
				}
				if (e.getKey() == null || e.getValue() == null || ((Map) e.getValue()).isEmpty()) {
					continue;
				}
				String recipeID = e.getKey();
				if (recipeID != null && !recipeID.contains("Sample")) {

					Map<String, Object> map = (Map<String, Object>) e.getValue();
					if (map != null && !map.isEmpty()) {

						EnumRecipeReg reg = EnumRecipeReg.ADD;
						Object[] list = null;
						ItemStack output = ItemStack.EMPTY;
						ItemStack secondary = ItemStack.EMPTY;
						float chance = 1F;
						FluidStack outputF = null;
						FluidStack inputF = null;
						DCHeatTier heat = DCHeatTier.NORMAL;
						DCHumidity hum = null;
						DCAirflow air = null;

						if (map.containsKey("recipe_type")) {
							Object o1 = map.get("recipe_type");
							if (o1 instanceof String) {
								String r2 = (String) o1;
								reg = EnumRecipeReg.getFromName(r2);
							}
						}

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

						if (map.containsKey("input")) {
							Object o2 = map.get("input");
							if (o2 instanceof List) {
								try {
									List<String> inputs = (List) o2;
									list = JsonUtilDC.getInputObjects(inputs);
								} catch (Error err) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (map.containsKey("output_primary")) {
							Object o2 = map.get("output_primary");
							if (o2 instanceof Map) {
								try {
									Map<String, Object> items = (Map) o2;
									output = JsonUtilDC.getOutput(items);
								} catch (Error err) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (map.containsKey("output_secondary")) {
							Object o3 = map.get("output_secondary");
							if (o3 instanceof Map) {
								try {
									Map<String, Object> items = (Map) o3;
									secondary = JsonUtilDC.getOutput(items);

									if (items.containsKey("chance")) {
										String c = items.get("chance").toString();
										chance = JsonUtilDC.parseFloat(c, chance);
									}

								} catch (Error err) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (map.containsKey("input_fluid")) {
							Object of = map.get("input_fluid");
							if (of instanceof Map) {
								try {
									Map<String, Object> items = (Map) of;
									inputF = JsonUtilDC.getFluid(items);
								} catch (Error err) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (map.containsKey("output_fluid")) {
							Object of = map.get("output_fluid");
							if (of instanceof Map) {
								try {
									Map<String, Object> items = (Map) of;
									outputF = JsonUtilDC.getFluid(items);
								} catch (Error err) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("FluidRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (reg == EnumRecipeReg.ADD && !DCUtil.isEmpty(output)) {
							if (addRecipe(output, secondary, chance, outputF, inputF, heat, hum, air, list))
								DCLogger.debugLog("FluidRecipeJson : Successfully added a fluid craft recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REPLACE && !DCUtil.isEmpty(output)) {
							if (changeRecipe(output, secondary, chance, outputF, inputF, heat, hum, air, list))
								DCLogger.debugLog("FluidRecipeJson : Successfully replaced a fluid craft recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REMOVE && list != null) {
							if (removeRecipe(inputF, heat, hum, air, list))
								DCLogger.debugLog("FluidRecipeJson : Successfully removed a fluid craft recipe. " + recipeID);
						}
					}
				}
			}
		}
	}

	private static boolean addRecipe(ItemStack output, ItemStack sec, float chance1, FluidStack outputF,
			FluidStack inputF, DCHeatTier heat, DCHumidity hum, DCAirflow air, Object[] input) {
		RecipeAPI.registerFluidRecipes.addRecipe(output, sec, chance1, outputF, heat, hum, air, false, inputF, input);
		return true;
	}

	private static boolean changeRecipe(ItemStack output, ItemStack sec, float chance1, FluidStack outputF,
			FluidStack inputF, DCHeatTier heat, DCHumidity hum, DCAirflow air, Object[] input) {
		if (removeRecipe(inputF, heat, hum, air, input)) {
			return addRecipe(output, sec, chance1, outputF, inputF, heat, hum, air, input);
		}
		return false;
	}

	private static boolean removeRecipe(FluidStack inputF, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			Object[] input) {
		List<ItemStack> check = JsonUtilDC.getInputLists(input);
		if (check.isEmpty())
			return false;
		IClimate clm = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return RecipeAPI.registerFluidRecipes.removeRecipe(clm, check, inputF);
	}

	public static void pre() {
		File file = new File(ClimateConfig.configDir + "/recipe/fluid_craft_recipe.json");

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

			File file = new File(ClimateConfig.configDir + "/recipe/fluid_craft_recipe.json");
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
				DCLogger.debugInfoLog("FluidRecipeJson : Failed to create json file: fluid_craft_recipe.json");
				e.printStackTrace();
			}

		} else {
			DCLogger.debugInfoLog("FluidRecipeJson : Recipe custom data json is already exists.");
			return;
		}

	}

	public static void registerDummy() {

		String id1 = "SampleRecipeID1 (Any string.)";
		Map<String, Object> recipe1 = new LinkedHashMap<>();
		recipe1.put("recipe_type", "ADD");

		Map<String, Object> output1 = new HashMap<>();
		output1.put("item", "samplemod:sample_output:metadata (removable)");
		recipe1.put("output_primary", output1);

		Map<String, Object> output2 = new HashMap<>();
		output2.put("item", "samplemod:sample_secondary:0 (removable)");
		output2.put("chance", 0.5);
		recipe1.put("output_secondary", output2);

		Map<String, Object> outputF = new HashMap<>();
		outputF.put("fluid", "sample_fluid_output (removable)");
		outputF.put("amount", 500);
		recipe1.put("output_fluid", outputF);

		recipe1.put("climate_temperature", "KILN (If this is removed, it will be forced to NORMAL.)");
		recipe1.put("climate_humidity", "DRY (If this is removed, it will be unspecified.)");
		recipe1.put("climate_airflow", "TIGHT (If this is removed, it will be unspecified.)");

		Map<String, Object> inputF = new HashMap<>();
		inputF.put("fluid", "sample_fluid_input (removable)");
		inputF.put("amount", 500);
		recipe1.put("input_fluid", inputF);

		List<String> input1 = Lists.newArrayList();
		input1.add("samplemod:sample_output:0");
		input1.add("ore_dict name is acceptable as input.");
		input1.add("Up to 6 inputs can be added.");
		recipe1.put("input", input1);
		INSTANCE.dummyMap.put(id1, recipe1);

		String id2 = "SampleRecipeID2";
		Map<String, Object> recipe2 = new LinkedHashMap<>();
		recipe2.put("recipe_type", "REMOVE");

		Map<String, Object> input2 = new HashMap<>();
		input2.put("item", "samplemod:remove_input:0");
		recipe2.put("input", input2);

		Map<String, Object> inputF2 = new HashMap<>();
		inputF2.put("fluid", "sample_fluid_input (removable)");
		inputF2.put("amount", 500);
		recipe2.put("input_fluid", inputF2);

		recipe2.put("climate_temperature", "KILN (If this is removed, it will be forced to NORMAL.)");
		recipe2.put("climate_humidity", "DRY (If this is removed, it will be forced to NORMAL.)");
		recipe2.put("climate_airflow", "TIGHT (If this is removed, it will be forced to NORMAL.)");

		INSTANCE.dummyMap.put(id2, recipe2);

	}

}
