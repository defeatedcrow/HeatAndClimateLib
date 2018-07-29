package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.recipe.RecipeAPI;
import mezz.jei.api.IModRegistry;

public final class ReactorRecipeMaker {

	private ReactorRecipeMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(RecipeAPI.registerReactorRecipes.getRecipeList(), DCsJEIPlugin.REACTOR_UID);
	}

}
