package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.recipe.ShapedNBTRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;

public class ShapedNBTWrapper extends ShapelessRecipeWrapper<ShapedNBTRecipe> implements IShapedCraftingRecipeWrapper {

	public ShapedNBTWrapper(IJeiHelpers jeiHelpers, ShapedNBTRecipe recipe) {
		super(jeiHelpers, recipe);
	}

	@Override
	public int getWidth() {
		return recipe.getWidth();
	}

	@Override
	public int getHeight() {
		return recipe.getHeight();
	}
}
