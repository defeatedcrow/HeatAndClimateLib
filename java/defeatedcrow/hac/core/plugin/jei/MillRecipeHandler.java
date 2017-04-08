package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.climate.recipe.MillRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class MillRecipeHandler implements IRecipeHandler<MillRecipe> {

	@Override
	public Class<MillRecipe> getRecipeClass() {
		return MillRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "dcs_climate.mill";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(MillRecipe recipe) {
		return new MillRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(MillRecipe recipe) {
		return recipe.getProcessedInput() != null;
	}

	@Override
	public String getRecipeCategoryUid(MillRecipe recipe) {
		return getRecipeCategoryUid();
	}
}
