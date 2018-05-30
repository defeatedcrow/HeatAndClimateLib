package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.climate.IClimate;

public enum CRecipeType {
	FLEEZING,
	STANDARD_STATE,
	DRYING,
	CULTIVATION,
	FERMENTATION,
	COOKING,
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
			return FLEEZING;

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
			if (h > 2)
				return STEAMING;
			else
				return h == 0 ? DRYING : COOKING;
		case 10:
			if (a < 1)
				return PYROLYSIS;
			else if (h > 1)
				return STEAMING;
			else
				return COMBUSTION;

		case 11:
		case 12:
		case 13:
			if (a < 1)
				return SMELTING;
			else
				return COMBUSTION;
		default:
			return STANDARD_STATE;
		}
	}
}
