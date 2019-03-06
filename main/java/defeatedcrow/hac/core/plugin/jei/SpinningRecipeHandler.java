package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;

import defeatedcrow.hac.core.climate.recipe.SpinningRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

@Deprecated
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
		ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
		if (recipe.getProcessedInput() != null) {
			inputs.addAll(recipe.getProcessedInput());
			return !inputs.isEmpty();
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(SpinningRecipe recipe) {
		return getRecipeCategoryUid();
	}
}
