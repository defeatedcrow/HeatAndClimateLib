package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ReactorRecipeHandler implements IRecipeHandler<ReactorRecipe> {

	@Override
	public Class<ReactorRecipe> getRecipeClass() {
		return ReactorRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "dcs_climate.reactor";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ReactorRecipe recipe) {
		return new ReactorRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(ReactorRecipe recipe) {
		if (recipe.getProcessedInput() != null && !recipe.getProcessedInput().isEmpty()) {
			if (recipe.getProcessedInput().size() > 4) {
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
	public String getRecipeCategoryUid(ReactorRecipe recipe) {
		return getRecipeCategoryUid();
	}

}
