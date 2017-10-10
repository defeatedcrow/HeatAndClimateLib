package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.climate.recipe.FluidCraftRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FluidRecipeHandler implements IRecipeHandler<FluidCraftRecipe> {

	@Override
	public Class<FluidCraftRecipe> getRecipeClass() {
		return FluidCraftRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.fluidcraft";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FluidCraftRecipe recipe) {
		return new FluidRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(FluidCraftRecipe recipe) {
		if (recipe.getProcessedInput() != null) {
			if (recipe.getProcessedInput().size() > 3) {
				return false;
			}
			boolean ret = true;
			for (Object obj : recipe.getProcessedInput()) {
				if (obj instanceof List) {
					if (((List) obj).isEmpty())
						ret = false;
				} else if (obj == null) {
					ret = false;
				}
			}
			return ret;
		} else if (recipe.getInputFluid() != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(FluidCraftRecipe recipe) {
		return getRecipeCategoryUid();
	}

}
