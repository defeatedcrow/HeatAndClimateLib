package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;

import defeatedcrow.hac.core.climate.recipe.CrusherRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

@Deprecated
public class CrusherRecipeHandler implements IRecipeHandler<CrusherRecipe> {

	@Override
	public Class<CrusherRecipe> getRecipeClass() {
		return CrusherRecipe.class;
	}

	public String getRecipeCategoryUid() {
		return "dcs_climate.crusher";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrusherRecipe recipe) {
		return new CrusherRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CrusherRecipe recipe) {
		ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
		if (recipe.getProcessedInput() != null) {
			inputs.addAll(recipe.getProcessedInput());
			return !inputs.isEmpty();
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(CrusherRecipe recipe) {
		return getRecipeCategoryUid();
	}
}
