package defeatedcrow.hac.core.plugin.jei.ingredients;

import defeatedcrow.hac.api.climate.DCHumidity;
import mezz.jei.api.recipe.IIngredientType;

public class Humidity implements IIngredientType<DCHumidity> {

	@Override
	public Class<? extends DCHumidity> getIngredientClass() {
		return DCHumidity.class;
	}

}
