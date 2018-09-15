package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.ClimateCore;
import mezz.jei.api.ingredients.IIngredientHelper;

public class HumidityHelper implements IIngredientHelper<DCHumidity> {

	@Override
	public List<DCHumidity> expandSubtypes(List<DCHumidity> ingredients) {
		return ingredients;
	}

	@Override
	public DCHumidity getMatch(Iterable<DCHumidity> ingredients, DCHumidity toMatch) {
		for (DCHumidity tier : ingredients) {
			if (toMatch == tier) {
				return tier;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(DCHumidity ingredient) {
		return ingredient.name();
	}

	@Override
	public String getUniqueId(DCHumidity ingredient) {
		return "Humidity:" + ingredient.name();
	}

	@Override
	public String getWildcardId(DCHumidity ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(DCHumidity ingredient) {
		return ClimateCore.MOD_ID;
	}

	@Override
	public Iterable<Color> getColors(DCHumidity ingredient) {
		List<Color> colors = new ArrayList<Color>();
		int[] colorInt = ingredient.getColor();
		if (colorInt != null && colorInt.length >= 3) {
			colors.add(new Color(colorInt[0], colorInt[1], colorInt[2]));
		}
		return colors;
	}

	@Override
	public String getErrorInfo(DCHumidity ingredient) {
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(DCHumidity.class);
		if (ingredient != null) {
			toStringHelper.add("Humidity", ingredient.name());
		} else {
			toStringHelper.add("Humidity", "null");
		}
		return toStringHelper.toString();
	}

	@Override
	public String getResourceId(DCHumidity ingredient) {
		return ingredient.name();
	}

	@Override
	public DCHumidity copyIngredient(DCHumidity ingredient) {
		int i = ingredient.getID();
		return DCHumidity.getTypeByID(i);
	}

}
