package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ReactorRecipeHandler implements IRecipeHandler<ReactorRecipe> {

	@Override
	public Class<ReactorRecipe> getRecipeClass() {
		return ReactorRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.reactor";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ReactorRecipe recipe) {
		return new ReactorRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(ReactorRecipe recipe) {
		return true;
	}

	@Override
	public String getRecipeCategoryUid(ReactorRecipe recipe) {
		return getRecipeCategoryUid();
	}

}
