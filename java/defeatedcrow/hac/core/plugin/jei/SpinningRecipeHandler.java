package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.climate.recipe.SpinningRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SpinningRecipeHandler implements IRecipeHandler<SpinningRecipe> {

	@Override
	public Class<SpinningRecipe> getRecipeClass() {
		return SpinningRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.spinning";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(SpinningRecipe recipe) {
		return new SpinningRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(SpinningRecipe recipe) {
		return recipe.getProcessedInput() != null;
	}

	@Override
	public String getRecipeCategoryUid(SpinningRecipe recipe) {
		return getRecipeCategoryUid();
	}
}
