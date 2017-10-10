package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.core.ClimateCore;
import mezz.jei.api.ingredients.IIngredientHelper;

public class HeatTierHelper implements IIngredientHelper<DCHeatTier> {

	@Override
	public List<DCHeatTier> expandSubtypes(List<DCHeatTier> ingredients) {
		return ingredients;
	}

	@Override
	public DCHeatTier getMatch(Iterable<DCHeatTier> ingredients, DCHeatTier toMatch) {
		for (DCHeatTier tier : ingredients) {
			if (toMatch == tier) {
				return tier;
			}
		}
		return null;
	}

	@Override
	public String getDisplayName(DCHeatTier ingredient) {
		return ingredient.name();
	}

	@Override
	public String getUniqueId(DCHeatTier ingredient) {
		return "HeatTier:" + ingredient.name();
	}

	@Override
	public String getWildcardId(DCHeatTier ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(DCHeatTier ingredient) {
		return ClimateCore.MOD_ID;
	}

	@Override
	public Iterable<Color> getColors(DCHeatTier ingredient) {
		List<Color> colors = new ArrayList<Color>();
		int[] colorInt = ingredient.getColor();
		if (colorInt != null && colorInt.length >= 3) {
			colors.add(new Color(colorInt[0], colorInt[1], colorInt[2]));
		}
		return colors;
	}

	@Override
	public String getErrorInfo(DCHeatTier ingredient) {
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(DCHeatTier.class);
		if (ingredient != null) {
			toStringHelper.add("HeatTier", ingredient.name());
		} else {
			toStringHelper.add("HeatTier", "null");
		}
		return toStringHelper.toString();
	}

	@Override
	public String getResourceId(DCHeatTier ingredient) {
		return ingredient.name();
	}

	@Override
	public DCHeatTier copyIngredient(DCHeatTier ingredient) {
		int i = ingredient.getID();
		return DCHeatTier.getTypeByID(i);
	}

}
