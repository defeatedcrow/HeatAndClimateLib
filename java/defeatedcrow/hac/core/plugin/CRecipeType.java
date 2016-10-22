package defeatedcrow.hac.core.plugin;

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
			return FLEEZING;
		case 3:
		case 4:
			if (h > 1)
				return CULTIVATION;
			else
				return STANDARD_STATE;
		case 5:
		case 6:
			if (h > 1)
				return FERMENTATION;
			else
				return DRYING;
		case 7:
			if (h > 2)
				return STEAMING;
			else
				return COOKING;
		case 8:
			if (a < 1)
				return PYROLYSIS;
			else if (h > 1)
				return STEAMING;
			else
				return COMBUSTION;
		case 9:
		case 10:
		case 11:
			if (a < 1)
				return SMELTING;
			else
				return COMBUSTION;
		default:
			return STANDARD_STATE;
		}
	}
}
