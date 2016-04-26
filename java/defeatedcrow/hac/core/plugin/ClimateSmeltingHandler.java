package defeatedcrow.hac.core.plugin;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;

public class ClimateSmeltingHandler implements IRecipeHandler<ClimateSmelting> {

	@Override
	public Class<ClimateSmelting> getRecipeClass() {
		return ClimateSmelting.class;
	}

	@Override
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

}
