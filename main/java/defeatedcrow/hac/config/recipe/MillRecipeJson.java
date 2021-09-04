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

import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import defeatedcrow.hac.core.util.JsonUtilDC;
import net.minecraft.item.ItemStack;

public class MillRecipeJson {

	public static MillRecipeJson INSTANCE = new MillRecipeJson();

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
						ItemStack secondary = ItemStack.EMPTY;
						float chance = 1F;

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
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
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
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
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
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.debugLog("MillRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (reg == EnumRecipeReg.ADD && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (addRecipe(output, secondary, chance, list))
								DCLogger.debugLog("MillRecipeJson : Successfully added a mill recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REPLACE && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (changeRecipe(output, secondary, chance, list))
								DCLogger.debugLog("MillRecipeJson : Successfully replaced a mill recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REMOVE && !list.isEmpty()) {
							if (removeRecipe(list))
								DCLogger.debugLog("MillRecipeJson : Successfully removed a mill recipe. " + recipeID);
						}
					}
				}
			}
		}
	}

	private static boolean addRecipe(ItemStack output, ItemStack sec, float chance, List<ItemStack> input) {
		RecipeAPI.registerMills.addRecipe(output, sec, chance, input);
		return true;
	}

	private static boolean changeRecipe(ItemStack output, ItemStack sec, float chance, List<ItemStack> input) {
		if (removeRecipe(input)) {
			return addRecipe(output, sec, chance, input);
		}
		return false;
	}

	private static boolean removeRecipe(List<ItemStack> input) {
		return RecipeAPI.registerMills.removeRecipe(input.get(0));
	}

	public static void pre() {
		File file = new File(ClimateConfig.configDir + "/recipe/mill_recipe.json");

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

			File file = new File(ClimateConfig.configDir + "/recipe/mill_recipe.json");
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
				DCLogger.debugInfoLog("MillRecipeJson : Failed to create json file: mill_recipe.json");
				e.printStackTrace();
			}

		} else {
			DCLogger.debugInfoLog("MillRecipeJson : Recipe custom data json is already exists.");
			return;
		}

	}

	public static void registerDummy() {

		String id1 = "SampleRecipeID1 (Any string.)";
		Map<String, Object> recipe1 = new LinkedHashMap<>();
		recipe1.put("recipe_type", "ADD");

		Map<String, Object> output1 = new HashMap<>();
		output1.put("item", "samplemod:sample_output:metadata");
		recipe1.put("output_primary", output1);

		Map<String, Object> output2 = new HashMap<>();
		output2.put("item", "samplemod:sample_secondary:0 (removable)");
		output2.put("chance", 0.15);
		recipe1.put("output_secondary", output2);

		Map<String, Object> input1 = new HashMap<>();
		input1.put("ore_dict", "ore_dict name is acceptable as input.");
		recipe1.put("input", input1);
		INSTANCE.dummyMap.put(id1, recipe1);

		String id2 = "SampleRecipeID2";
		Map<String, Object> recipe2 = new LinkedHashMap<>();
		recipe2.put("recipe_type", "REMOVE");
		Map<String, Object> input2 = new HashMap<>();
		input2.put("item", "samplemod:remove_input:0");
		recipe2.put("input", input2);
		INSTANCE.dummyMap.put(id2, recipe2);

	}

}
