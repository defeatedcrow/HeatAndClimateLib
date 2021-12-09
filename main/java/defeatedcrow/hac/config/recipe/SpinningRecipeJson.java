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

public class SpinningRecipeJson {

	public static SpinningRecipeJson INSTANCE = new SpinningRecipeJson();

	private static final Map<String, Object> jsonMap = new LinkedHashMap<>();
	private static final Map<String, Object> dummyMap = new LinkedHashMap<>();

	private static void load() {
		if (!INSTANCE.jsonMap.isEmpty()) {
			for (Entry<String, Object> e : INSTANCE.jsonMap.entrySet()) {
				if (e == null || !(e.getValue() instanceof Map)) {
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
						List<ItemStack> list = Lists.newArrayList();
						ItemStack output = ItemStack.EMPTY;
						int count = 1;

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

									if (map.containsKey("count")) {
										String i = map.get("count").toString();
										count = JsonUtilDC.parseInt(i, 1);
									}
								} catch (Error err) {
									DCLogger.infoLog("SpinningRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.infoLog("SpinningRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}
						if (map.containsKey("output")) {
							Object o2 = map.get("output");
							if (o2 instanceof Map) {
								try {
									Map<String, Object> items = (Map) o2;
									output = JsonUtilDC.getOutput(items);
								} catch (Error err) {
									DCLogger.infoLog("SpinningRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								} catch (Exception exp) {
									DCLogger.infoLog("SpinningRecipeJson : Error entry found. This entry is ignored. " + recipeID);
									continue;
								}
							}
						}

						if (reg == EnumRecipeReg.ADD && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (addRecipe(output, count, list))
								DCLogger.traceLog("SpinningRecipeJson : Successfully added a spinning recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REPLACE && !DCUtil.isEmpty(output) && !list.isEmpty()) {
							if (changeRecipe(output, count, list))
								DCLogger.traceLog("SpinningRecipeJson : Successfully replaced a spinning recipe. " + recipeID);
						} else if (reg == EnumRecipeReg.REMOVE && !list.isEmpty()) {
							if (removeRecipe(list))
								DCLogger.traceLog("SpinningRecipeJson : Successfully removed a spinning recipe. " + recipeID);
						}
					}
				}
			}
		}
	}

	private static boolean addRecipe(ItemStack output, int count, List<ItemStack> input) {
		RecipeAPI.registerSpinningRecipes.addRecipe(output, count, input);
		return true;
	}

	private static boolean changeRecipe(ItemStack output, int count, List<ItemStack> input) {
		if (removeRecipe(input)) {
			return addRecipe(output, count, input);
		}
		return false;
	}

	private static boolean removeRecipe(List<ItemStack> input) {
		return RecipeAPI.registerSpinningRecipes.removeRecipe(input.get(0));
	}

	public static void pre() {
		File file = new File(ClimateConfig.configDir + "/recipe/spinning_recipe.json");

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

			File file = new File(ClimateConfig.configDir + "/recipe/spinning_recipe.json");
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
				DCLogger.debugInfoLog("SpinningRecipeJson : Failed to create json file: spinning_recipe.json");
				e.printStackTrace();
			}

		} else {
			DCLogger.debugInfoLog("SpinningRecipeJson : Recipe custom data json is already exists.");
			return;
		}

	}

	public static void registerDummy() {

		String id1 = "SampleRecipeID1 (Any string.)";
		Map<String, Object> recipe1 = new LinkedHashMap<>();
		recipe1.put("recipe_type", "ADD");

		Map<String, Object> output1 = new HashMap<>();
		output1.put("item", "samplemod:sample_output:metadata");
		output1.put("count", 4);
		recipe1.put("output", output1);

		Map<String, Object> input1 = new HashMap<>();
		input1.put("ore_dict", "ore_dict name is acceptable as input.");
		input1.put("count", 2);
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
