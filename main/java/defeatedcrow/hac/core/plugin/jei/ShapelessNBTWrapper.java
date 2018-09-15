package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.recipe.ShapelessNBTRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;

public class ShapelessNBTWrapper extends ShapelessRecipeWrapper<ShapelessNBTRecipe> {

	public ShapelessNBTWrapper(IJeiHelpers jeiHelpers, ShapelessNBTRecipe recipe) {
		super(jeiHelpers, recipe);
	}

}
