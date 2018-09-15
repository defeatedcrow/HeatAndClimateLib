package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import mezz.jei.api.IModRegistry;

public final class ClimateSmeltingMaker {

	private ClimateSmeltingMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(RecipeAPI.registerSmelting.getRecipeList(), DCsJEIPlugin.SMELTING_UID);
	}

}
