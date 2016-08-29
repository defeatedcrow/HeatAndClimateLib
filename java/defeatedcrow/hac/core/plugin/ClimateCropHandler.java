package defeatedcrow.hac.core.plugin;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import defeatedcrow.hac.api.cultivate.IClimateCrop;

public class ClimateCropHandler implements IRecipeHandler<IClimateCrop> {

	@Override
	public Class<IClimateCrop> getRecipeClass() {
		return IClimateCrop.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "dcs_climate.crop";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(IClimateCrop recipe) {
		return new ClimateCropWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(IClimateCrop recipe) {
		if (recipe instanceof Block) {
			IBlockState state = recipe.getGrownState();
			if (state != null) {
				if (!recipe.getCropItems(state, 0).isEmpty() && recipe.getSeedItem(state) != null) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

}
