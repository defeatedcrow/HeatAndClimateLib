package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ClimateSmeltingHandler implements IRecipeHandler<ClimateSmelting> {

	@Override
	public Class<ClimateSmelting> getRecipeClass() {
		return ClimateSmelting.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.smelting";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ClimateSmelting recipe) {
		return new ClimateSmeltingWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(ClimateSmelting recipe) {
		return recipe.getProcessedInput() != null;
	}

	@Override
	public String getRecipeCategoryUid(ClimateSmelting recipe) {
		return getRecipeCategoryUid();
	}

}
