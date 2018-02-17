package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.recipe.RecipeAPI;
import mezz.jei.api.IModRegistry;

public final class CrusherRecipeMaker {

	private CrusherRecipeMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(RecipeAPI.registerCrushers.getRecipeList(), DCsJEIPlugin.CRUSHER_UID);
	}

}
