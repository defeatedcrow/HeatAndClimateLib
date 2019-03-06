package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;

import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

@Deprecated
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
		ArrayList<ItemStack> inputs = new ArrayList<ItemStack>();
		if (recipe.getProcessedInput() != null) {
			inputs.addAll(recipe.getProcessedInput());
			return !inputs.isEmpty();
		}
		return false;
	}

	@Override
	public String getRecipeCategoryUid(ClimateSmelting recipe) {
		return getRecipeCategoryUid();
	}

}
