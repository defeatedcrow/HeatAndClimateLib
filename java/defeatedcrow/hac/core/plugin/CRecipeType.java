package defeatedcrow.hac.core.plugin;

import defeatedcrow.hac.api.climate.IClimate;

public enum CRecipeType {
	FLEEZING, GROWING, STANDARD_STATE, DRYING, FERMENTATION, COOKING, STEAMING, COMBUSTION, PYROLYSIS, SMELTING;

	public static CRecipeType getType(IClimate clm) {
		int t = clm.getHeat().getID();
		int h = clm.getHumidity().getID();
		int a = clm.getAirflow().getID();
		switch (t) {
		case 0:
		case 1:
			return FLEEZING;
		case 2:
			return STANDARD_STATE;
		case 3:
			if (h > 1)
				return FERMENTATION;
			else
				return DRYING;
		case 4:
			if (h > 1)
				return STEAMING;
			else
				return COOKING;
		case 5:
			if (a < 1)
				return PYROLYSIS;
			else if (h > 1)
				return STEAMING;
			else
				return COMBUSTION;
		case 6:
		case 7:
			if (a < 1)
				return SMELTING;
			else
				return COMBUSTION;
		default:
			return STANDARD_STATE;
		}
	}
}
