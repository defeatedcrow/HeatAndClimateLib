package defeatedcrow.hac.core.plugin;

import defeatedcrow.hac.api.climate.IClimate;

public enum CRecipeType {
	FLEEZING, STANDARD_STATE, DRYING, CULTIVATION, FERMENTATION, COOKING, STEAMING, COMBUSTION, PYROLYSIS, SMELTING;

	public static CRecipeType getType(IClimate clm) {
		int t = clm.getHeat().getID();
		int h = clm.getHumidity().getID();
		int a = clm.getAirflow().getID();
		switch (t) {
		case 0:
		case 1:
			return FLEEZING;
		case 2:
		case 3:
			if (h > 1)
				return CULTIVATION;
			else
				return STANDARD_STATE;
		case 4:
			if (h > 1)
				return FERMENTATION;
			else
				return DRYING;
		case 5:
			if (h > 2)
				return STEAMING;
			else
				return COOKING;
		case 6:
			if (a < 1)
				return PYROLYSIS;
			else if (h > 1)
				return STEAMING;
			else
				return COMBUSTION;
		case 7:
		case 8:
		case 9:
			if (a < 1)
				return SMELTING;
			else
				return COMBUSTION;
		default:
			return STANDARD_STATE;
		}
	}
}
