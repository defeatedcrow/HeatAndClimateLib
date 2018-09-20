package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.recipe.RecipeAPI;
import mezz.jei.api.IModRegistry;

public final class FluidRecipeMaker {

	private FluidRecipeMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(RecipeAPI.registerFluidRecipes.getRecipeList(), DCsJEIPlugin.FLUID_UID);
	}

}
