package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.core.ClimateCore;
import mezz.jei.api.ingredients.IIngredientHelper;

public class AirflowHelper implements IIngredientHelper<DCAirflow> {

	@Override
	public List<DCAirflow> expandSubtypes(List<DCAirflow> ingredients) {
		return ingredients;
	}

	@Override
	public DCAirflow getMatch(Iterable<DCAirflow> ingredients, DCAirflow toMatch) {
		for (DCAirflow tier : ingredients) {
			if (toMatch == tier) {
				return tier;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(DCAirflow ingredient) {
		return ingredient.localize();
	}

	@Override
	public String getUniqueId(DCAirflow ingredient) {
		return "Airflow:" + ingredient.name();
	}

	@Override
	public String getWildcardId(DCAirflow ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(DCAirflow ingredient) {
		return ClimateCore.MOD_ID;
	}

	@Override
	public Iterable<Color> getColors(DCAirflow ingredient) {
		List<Color> colors = new ArrayList<Color>();
		int[] colorInt = ingredient.getColor();
		if (colorInt != null && colorInt.length >= 3) {
			colors.add(new Color(colorInt[0], colorInt[1], colorInt[2]));
		}
		return colors;
	}

	@Override
	public String getErrorInfo(DCAirflow ingredient) {
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(DCAirflow.class);
		if (ingredient != null) {
			toStringHelper.add("Airflow", ingredient.name());
		} else {
			toStringHelper.add("Airflow", "null");
		}
		return toStringHelper.toString();
	}

	@Override
	public String getResourceId(DCAirflow ingredient) {
		String name = ingredient.name();
		return name;
	}

	@Override
	public DCAirflow copyIngredient(DCAirflow ingredient) {
		int i = ingredient.getID();
		return DCAirflow.getTypeByID(i);
	}

}
