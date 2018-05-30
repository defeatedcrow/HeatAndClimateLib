package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import mezz.jei.api.IModRegistry;

public final class ReactorRecipeMaker {

	private ReactorRecipeMaker() {}

	public static void register(IModRegistry registry) {
		List<ReactorRecipe> list = new ArrayList<ReactorRecipe>();
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.ABSOLUTE));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.CRYOGENIC));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.FROSTBITE));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.COLD));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.COOL));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.NORMAL));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.WARM));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.HOT));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.BOIL));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.OVEN));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.KILN));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.SMELTING));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.UHT));
		list.addAll((List<ReactorRecipe>) RecipeAPI.registerReactorRecipes.getRecipeList(DCHeatTier.INFERNO));
		registry.addRecipes(list, DCsJEIPlugin.REACTOR_UID);
	}

}
