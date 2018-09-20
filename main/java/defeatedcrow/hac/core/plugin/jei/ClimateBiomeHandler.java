package defeatedcrow.hac.core.plugin.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.world.biome.Biome;

public class ClimateBiomeHandler implements IRecipeHandler<Biome> {

	@Override
	public Class<Biome> getRecipeClass() {
		return Biome.class;
	}

	@Override
	public String getRecipeCategoryUid(Biome recipe) {
		return "dcs_climate.biome";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(Biome recipe) {
		return new ClimateBiomeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(Biome recipe) {
		return recipe != null;
	}

}
