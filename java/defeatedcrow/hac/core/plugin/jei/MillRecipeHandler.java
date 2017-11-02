package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;

import defeatedcrow.hac.core.climate.recipe.MillRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class MillRecipeHandler implements IRecipeHandler<MillRecipe> {

	@Override
	public Class<MillRecipe> getRecipeClass() {
		return MillRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.mill";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(MillRecipe recipe) {
		return new MillRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(MillRecipe recipe) {
		ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
		if (recipe.getProcessedInput() != null) {
			inputs.addAll(recipe.getProcessedInput());
			return !inputs.isEmpty();
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(MillRecipe recipe) {
		return getRecipeCategoryUid();
	}
}
