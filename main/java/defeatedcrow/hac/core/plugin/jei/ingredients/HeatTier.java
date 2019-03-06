package defeatedcrow.hac.core.plugin.jei.ingredients;

import defeatedcrow.hac.api.climate.DCHeatTier;
import mezz.jei.api.recipe.IIngredientType;

public class HeatTier implements IIngredientType<DCHeatTier> {

	@Override
	public Class<? extends DCHeatTier> getIngredientClass() {
		return DCHeatTier.class;
	}

}
