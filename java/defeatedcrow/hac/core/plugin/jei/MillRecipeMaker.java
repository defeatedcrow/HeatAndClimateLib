package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.recipe.RecipeAPI;
import mezz.jei.api.IModRegistry;

public final class MillRecipeMaker {

	private MillRecipeMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(RecipeAPI.registerMills.getRecipeList());
	}

}
