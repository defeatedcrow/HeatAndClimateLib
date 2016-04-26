package defeatedcrow.hac.core.plugin;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.recipe.ClimateRecipe;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;

@JEIPlugin
public class DCsJEIPlugin implements IModPlugin {

	private IJeiHelpers helper;

	@Deprecated
	@Override
	public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
		helper = jeiHelpers;
	}

	@Deprecated
	@Override
	public void onItemRegistryAvailable(IItemRegistry itemRegistry) {
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeCategories(new ClimateSmeltingCategory(helper.getGuiHelper()), new ClimateRecipeCategory(helper.getGuiHelper()));
		registry.addRecipeHandlers(new ClimateSmeltingHandler(), new ClimateRecipeHandler());
		List<ClimateSmelting> list = new ArrayList<ClimateSmelting>();

		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.ABSOLUTE));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.COLD));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.NORMAL));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.HOT));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.OVEN));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.KILN));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.SMELTING));
		list.addAll((List<ClimateSmelting>) RecipeAPI.registerSmelting.getRecipeList(DCHeatTier.UHT));

		List<ClimateRecipe> list2 = new ArrayList<ClimateRecipe>();
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.ABSOLUTE));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.COLD));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.NORMAL));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.HOT));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.OVEN));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.KILN));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.SMELTING));
		list2.addAll((List<ClimateRecipe>) RecipeAPI.registerRecipes.getRecipeList(DCHeatTier.UHT));

		registry.addRecipes(list);
		registry.addRecipes(list2);
	}

	@Deprecated
	@Override
	public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {
	}

	@Deprecated
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

}
