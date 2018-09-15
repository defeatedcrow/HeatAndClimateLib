package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.climate.IClimate;

public enum CRecipeType {
	FREEZING,
	STANDARD_STATE,
	DRYING,
	CULTIVATION,
	FERMENTATION,
	COOKING,
	BOILING,
	STEAMING,
	COMBUSTION,
	PYROLYSIS,
	SMELTING;

	public static CRecipeType getType(IClimate clm) {
		int t = clm.getHeat().getID();
		int h = clm.getHumidity().getID();
		int a = clm.getAirflow().getID();
		switch (t) {
		case 0:
		case 1:
		case 2:
		case 3:
			return FREEZING;
		case 4:
		case 5:
			if (h > 1)
				return CULTIVATION;
			if (h < 0)
				return DRYING;
			else
				return STANDARD_STATE;

		case 6:
		case 7:
			if (h > 1)
				return FERMENTATION;
			else
				return DRYING;
		case 8:
		case 9:
		case 10:
			if (h > 2)
				return BOILING;
			else if (a < 2)
				return PYROLYSIS;
			else
				return h == 0 ? DRYING : COOKING;
		case 11:
		case 12:
		case 13:
			if (a < 2)
				return SMELTING;
			else
				return COMBUSTION;
		default:
			return STANDARD_STATE;
		}
	}
}
