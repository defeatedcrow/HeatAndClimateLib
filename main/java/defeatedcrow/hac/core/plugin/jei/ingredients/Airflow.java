package defeatedcrow.hac.core.plugin.jei.ingredients;

import defeatedcrow.hac.api.climate.DCAirflow;
import mezz.jei.api.recipe.IIngredientType;

public class Airflow implements IIngredientType<DCAirflow>{

	@Override
	public Class<? extends DCAirflow> getIngredientClass() {
		return DCAirflow.class;
	}

}
