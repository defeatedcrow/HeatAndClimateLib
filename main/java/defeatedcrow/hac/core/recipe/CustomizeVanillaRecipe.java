package defeatedcrow.hac.core.recipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import defeatedcrow.hac.api.climate.ItemSet;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.DCRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * This code was referenced to OreDictionary.class.
 */
public class CustomizeVanillaRecipe {

	private CustomizeVanillaRecipe() {}

	private static Map<ItemSet, String> replaceTable = new HashMap<ItemSet, String>();

	private static ArrayList<ItemSet> exclusions = new ArrayList<ItemSet>();

	private static List<ItemSet> shapelessOnly = ImmutableList.of(new ItemSet(Items.WHEAT));

	static void initializeMap() {

		if (!ConvertTargetList.getExclusionList().isEmpty()) {
			DCLogger.debugLog("#HaCRecipeConverter# convert ex: " + ConvertTargetList.getExclusionList().size());
			exclusions.addAll(ConvertTargetList.getExclusionList());
		}

		if (!ConvertTargetList.getReplaceTable().isEmpty()) {
			DCLogger.debugLog("#HaCRecipeConverter# convert target: " + ConvertTargetList.getReplaceTable().size());
			replaceTable.putAll(ConvertTargetList.getReplaceTable());
		}
	}

	public static void initCustomize() {
		initializeMap();

		DCLogger.debugLog("#HaCRecipeConverter# Target recipe: " + CraftingManager.REGISTRY.getKeys().size());
		Iterator<IRecipe> targetRecipes = CraftingManager.REGISTRY.iterator();
		List<IRecipe> addRecipes = new ArrayList<IRecipe>();
		List<IRecipe> addOreRecipes = new ArrayList<IRecipe>();
		List<IRecipe> addShapelessRecipes = new ArrayList<IRecipe>();
		List<IRecipe> addShapelessOreRecipes = new ArrayList<IRecipe>();

		List<ItemSet> replaces = ImmutableList.copyOf(replaceTable.keySet());

		int count = 0;

		/*
		 * やってることはOreDictionaryの処理と同様。
		 * 違う点は、元のレシピはremoveせずに残すということ。
		 */
		while (targetRecipes.hasNext()) {
			IRecipe rec = targetRecipes.next();
			if (rec instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) rec;
				ItemStack output = recipe.getRecipeOutput();
				if (DCUtil.isEmpty(output) || containsMatch2(false, exclusions, output)) {
					continue;
				}

				if (!containsMatch(true, shapelessOnly, recipe.recipeItems) && containsMatch(true, replaces, recipe.recipeItems)) {
					addRecipes.add(recipe);
				}
			} else if (rec instanceof ShapelessRecipes) {
				ShapelessRecipes recipe = (ShapelessRecipes) rec;
				ItemStack output = recipe.getRecipeOutput();
				if (DCUtil.isEmpty(output) || containsMatch2(false, exclusions, output)) {
					continue;
				}

				if (containsMatch(true, replaces, recipe.recipeItems)) {
					addShapelessRecipes.add(recipe);
				}
			}
			if (rec instanceof ShapedOreRecipe) {
				ShapedOreRecipe recipe = (ShapedOreRecipe) rec;
				ItemStack output = recipe.getRecipeOutput();
				if (DCUtil.isEmpty(output) || containsMatch2(false, exclusions, output)) {
					continue;
				}

				if (!containsMatch(true, shapelessOnly, recipe.getIngredients()) && containsMatch(true, replaces, recipe
						.getIngredients())) {
					addOreRecipes.add(recipe);
				}

			} else if (rec instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe recipe = (ShapelessOreRecipe) rec;
				ItemStack output = recipe.getRecipeOutput();
				if (DCUtil.isEmpty(output) || containsMatch2(false, exclusions, output)) {
					continue;
				}

				if (containsMatch(true, replaces, recipe.getIngredients())) {
					addShapelessOreRecipes.add(recipe);
				}
			}
		}

		for (Object obj : addRecipes) {
			if (obj instanceof ShapedRecipes) {
				registerCustomShapedRecipe((ShapedRecipes) obj);
				count++;
			}
		}

		for (Object obj : addShapelessRecipes) {
			if (obj instanceof ShapelessRecipes) {
				registerCustomShapelessRecipe((ShapelessRecipes) obj);
				count++;
			}
		}

		for (Object obj : addOreRecipes) {
			if (obj instanceof ShapedOreRecipe) {
				registerCustomShapedOreRecipe((ShapedOreRecipe) obj);
				count++;
			}
		}

		for (Object obj : addShapelessOreRecipes) {
			if (obj instanceof ShapelessOreRecipe) {
				registerCustomShapelessOreRecipe((ShapelessOreRecipe) obj);
				count++;
			}
		}

		DCLogger.debugLog("#HaCRecipeConverter# Replaced " + count + " recipes.");
	}

	// Shaped
	private static void registerCustomShapedRecipe(ShapedRecipes recipe) {
		ItemStack output = recipe.getRecipeOutput();
		int x = recipe.recipeWidth;
		int y = recipe.recipeHeight;
		NonNullList<Ingredient> items = recipe.recipeItems;

		if (x * y == 0 || x > 3 || y > 3) {
			DCLogger.debugLog("Failed ShapedRecipe : " + output.toString());
			return;
		}

		ArrayList<Object> inputArray = new ArrayList<Object>();

		// 3x3より大きなレシピには全く対応していない
		String[] s = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		Character[] c = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

		String[] returnArray = new String[y];

		// クラフト欄指定用のString[]の生成
		for (int i = 0; i < y; i++) {
			String f = "";
			for (int k = 0; k < x; k++) {
				int num = k + (i * x);
				if (num < items.size()) {
					f += DCUtil.isEmptyIngredient(items.get(num)) ? " " : s[num];
				} else {
					f += " ";
				}
			}
			returnArray[i] = f;
		}

		for (int i = 0; i < returnArray.length; i++) {
			inputArray.add(returnArray[i]);
		}

		// ItemStack配列部分の追加
		// Character、ItemStack/String の順に追加していけば良い。空欄はnullのまま。
		for (int i = 0; i < x * y; i++) {
			if (!DCUtil.isEmptyIngredient(items.get(i))) {
				String sign = s[i];
				Ingredient item = items.get(i);
				boolean b = false;

				for (Entry<ItemSet, String> entry : replaceTable.entrySet()) {
					if (itemMatches(item.getMatchingStacks()[0], entry.getKey(), true)) {
						String oreName = entry.getValue();

						inputArray.add(c[i]);
						inputArray.add(entry.getValue());
						b = true;
					}
				}

				if (!b) {
					inputArray.add(c[i]);
					inputArray.add(item);
				}
			}
		}

		Object[] newInputs = inputArray.toArray();
		ResourceLocation name = new ResourceLocation(ClimateCore.MOD_ID, recipe.getRegistryName()
				.getResourcePath() + "_dcs");
		DCRecipe.addShapedRecipe(name, output, newInputs);
		DCLogger.debugLog("#HaCRecipeConverter# Customized ShapdRecipe : " + name.toString());
	}

	// Shapeless
	private static void registerCustomShapelessRecipe(ShapelessRecipes recipe) {
		ItemStack output = recipe.getRecipeOutput();
		NonNullList<Ingredient> items = recipe.recipeItems;
		ArrayList<Object> inputs = new ArrayList<Object>();

		if (DCUtil.isEmpty(output))
			return;

		if (items == null || items.isEmpty() || items.size() > 9) {
			DCLogger.debugLog("Failed ShapelessRecipe : " + output.toString());
			return;
		}

		for (Ingredient item : items) {
			boolean b = false;
			if (!DCUtil.isEmptyIngredient(item)) {
				for (Entry<ItemSet, String> entry : replaceTable.entrySet()) {
					if (itemMatches(item.getMatchingStacks()[0], entry.getKey(), true)) {
						String oreName = entry.getValue();
						inputs.add(oreName);
						b = true;
					}
				}

				if (!b) {
					inputs.add(item);
				}
			}
		}

		Object[] newInputs = inputs.toArray();
		ResourceLocation name = new ResourceLocation(ClimateCore.MOD_ID, recipe.getRegistryName()
				.getResourcePath() + "_dcs");
		DCRecipe.addShapelessRecipe(name, output, newInputs);
		DCLogger.debugLog("#HaCRecipeConverter# Customized ShapelessRecipe : " + name.toString());

	}

	// Shaped-Ore
	private static void registerCustomShapedOreRecipe(ShapedOreRecipe recipe) {
		ItemStack output = recipe.getRecipeOutput();
		NonNullList<Ingredient> objects = recipe.getIngredients();
		ArrayList<Object> inputs = new ArrayList<Object>();

		int x = 0;
		int y = 0;

		try {
			Field fieldW = recipe.getClass().getDeclaredField("width");
			fieldW.setAccessible(true);
			x = fieldW.getInt(recipe);

			Field fieldH = recipe.getClass().getDeclaredField("height");
			fieldH.setAccessible(true);
			y = fieldH.getInt(recipe);
		} catch (Exception e) {
			DCLogger.debugLog("Failed ShapedOreRecipe : " + objects.toString());
			return;
		}

		if (x * y == 0 || x > 3 || y > 3) {
			DCLogger.debugLog("Failed ShapedOreRecipe : " + output.toString());
			return;
		}

		// 3x3より大きなレシピには全く対応していない
		String[] s = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		Character[] c = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

		String[] returnArray = new String[y];

		// クラフト欄指定用のString[]の生成
		for (int i = 0; i < y; i++) {
			String f = "";
			for (int k = 0; k < x; k++) {
				int num = k + (i * x);
				if (num < objects.size()) {
					f += DCUtil.isEmptyIngredient(objects.get(num)) ? " " : s[num];
				} else {
					f += " ";
				}
			}
			returnArray[i] = f;
		}

		for (int i = 0; i < returnArray.length; i++) {
			inputs.add(returnArray[i]);
		}

		// item
		for (int i = 0; i < x * y; i++) {
			boolean b = false;
			ItemStack item = ItemStack.EMPTY;
			Ingredient obj = objects.get(i);
			if (DCUtil.isEmptyIngredient(obj)) {
				continue;
			}

			if (obj.getMatchingStacks().length == 1) {
				item = obj.getMatchingStacks()[0];
			} else {
				for (ItemStack oreItem : obj.getMatchingStacks()) {
					if (DCUtil.isEmpty(oreItem) || OreDictionary.getOreIDs(oreItem).length == 0)
						continue;
					int[] id = OreDictionary.getOreIDs(oreItem);
					if (id != null) {
						for (int j = 0; j < id.length; j++) {
							String str = OreDictionary.getOreName(id[j]);
							if (str != null) {
								inputs.add(c[i]);
								inputs.add(str);
								b = true;
								break;
							}
						}
					}
					if (b)
						break;
				}

				if (!b) {
					inputs.add(c[i]);
					inputs.add(obj);
					b = true;
				}
			}

			if (!DCUtil.isEmpty(item)) {
				for (Entry<ItemSet, String> entry : replaceTable.entrySet()) {
					if (itemMatches(item, entry.getKey(), true)) {
						String oreName = entry.getValue();
						inputs.add(c[i]);
						inputs.add(oreName);
						b = true;
					}
				}

				if (!b) {
					inputs.add(c[i]);
					inputs.add(obj);
				}
			}
		}

		Object[] newInputs = inputs.toArray();
		ResourceLocation name = new ResourceLocation(ClimateCore.MOD_ID, recipe.getRegistryName()
				.getResourcePath() + "_dcs");
		DCRecipe.addShapedRecipe(name, output, newInputs);
		DCLogger.debugLog("#HaCRecipeConverter# Customized ShapedOreRecipe : " + name.toString());

	}

	// Shapeless-Ore
	private static void registerCustomShapelessOreRecipe(ShapelessOreRecipe recipe) {
		ItemStack output = recipe.getRecipeOutput();
		NonNullList<Ingredient> objects = recipe.getIngredients();
		ArrayList<Object> inputs = new ArrayList<Object>();

		if (objects == null || objects.isEmpty() || objects.size() > 9) {
			DCLogger.debugLog("Failed ShapelessOreRecipe : " + output.toString());
			return;
		}

		for (Ingredient obj : objects) {
			boolean b = false;
			ItemStack item = ItemStack.EMPTY;

			if (obj.getMatchingStacks().length == 1) {
				item = obj.getMatchingStacks()[0];
			} else {
				for (ItemStack oreItem : obj.getMatchingStacks()) {
					if (DCUtil.isEmpty(oreItem) || OreDictionary.getOreIDs(oreItem).length == 0)
						continue;
					int[] id = OreDictionary.getOreIDs(oreItem);
					if (id != null) {
						for (int j = 0; j < id.length; j++) {
							String str = OreDictionary.getOreName(id[j]);
							if (str != null) {
								inputs.add(str);
								b = true;
								break;
							}
						}
					}
					if (b)
						break;
				}

				if (!b) {
					inputs.add(obj);
					b = true;
				}
			}

			if (!DCUtil.isEmpty(item)) {
				for (Entry<ItemSet, String> entry : replaceTable.entrySet()) {
					if (itemMatches(item, entry.getKey(), true)) {
						String oreName = entry.getValue();
						inputs.add(oreName);
						b = true;
					}
				}
			}

			if (!b) {
				inputs.add(obj);
			}
		}

		Object[] newInputs = inputs.toArray();
		ResourceLocation name = new ResourceLocation(ClimateCore.MOD_ID, recipe.getRegistryName()
				.getResourcePath() + "_dcs");
		DCRecipe.addShapelessRecipe(name, output, newInputs);
		DCLogger.debugLog("#HaCRecipeConverter# Customized ShapelessOreRecipe : " + name.toString());

	}

	private static boolean containsMatch(boolean strict, List<ItemSet> inputs, List<Ingredient> targets) {
		for (Ingredient target : targets) {
			if (DCUtil.isEmptyIngredient(target) || target.getMatchingStacks().length > 1) {
				continue;
			}
			if (target.getMatchingStacks() != null) {
				ItemStack stack = target.getMatchingStacks()[0];
				return inputs.contains(new ItemSet(stack));
			}
		}
		return false;
	}

	private static boolean containsMatch2(boolean strict, List<ItemSet> inputs, ItemStack... targets) {
		for (ItemSet input : inputs) {
			for (ItemStack target : targets) {
				return inputs.contains(new ItemSet(target));
			}
		}
		return false;
	}

	private static boolean containsMatch2(boolean strict, List<ItemSet> inputs, List<ItemStack> targets) {
		for (ItemSet input : inputs) {
			for (ItemStack target : targets) {
				return inputs.contains(new ItemSet(target));
			}
		}
		return false;
	}

	public static boolean itemMatches(ItemStack target, ItemSet input, boolean strict) {
		return itemMatches(new ItemSet(target), input, strict);
	}

	public static boolean itemMatches(ItemSet target, ItemSet input, boolean strict) {
		if (ItemSet.isEmpty(target) || ItemSet.isEmpty(input)) {
			return ItemSet.isEmpty(target) && ItemSet.isEmpty(input);
		}
		if (target.equals(input)) {
			if (strict) {
				return target.meta == input.meta || (target.meta == 0 && input.meta == 32767);
			} else {
				return true;
			}
		}
		return false;
	}

}
