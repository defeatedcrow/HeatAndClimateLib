package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.climate.recipe.ClimateRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ClimateRecipeHandler implements IRecipeHandler<ClimateRecipe> {

	@Override
	public Class<ClimateRecipe> getRecipeClass() {
		return ClimateRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.recipe";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ClimateRecipe recipe) {
		return new ClimateRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(ClimateRecipe recipe) {
		if (recipe.getProcessedInput() != null && recipe.getProcessedInput().size() < 10) {
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
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(ClimateRecipe recipe) {
		return getRecipeCategoryUid();
	}

}
