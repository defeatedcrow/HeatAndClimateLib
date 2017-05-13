package defeatedcrow.hac.core.plugin.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ClimateEffectiveHandler implements IRecipeHandler<ClimateEffectiveTile> {

	@Override
	public Class<ClimateEffectiveTile> getRecipeClass() {
		return ClimateEffectiveTile.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "dcs_climate.effective";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ClimateEffectiveTile recipe) {
		return new ClimateEffectiveWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(ClimateEffectiveTile recipe) {
		return recipe.getInputItem() != null;
	}

	@Override
	public String getRecipeCategoryUid(ClimateEffectiveTile recipe) {
		return getRecipeCategoryUid();
	}

}
