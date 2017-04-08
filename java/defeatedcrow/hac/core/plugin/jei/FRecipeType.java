package defeatedcrow.hac.core.plugin.jei;

import net.minecraftforge.fluids.FluidStack;
import defeatedcrow.hac.api.climate.IClimate;

public enum FRecipeType {
	FLEEZING,
	STANDARD_STATE,
	DRYING,
	FERMENTATION,
	COOKING,
	BOILING,
	DISTILLING;

	public static FRecipeType getType(IClimate clm, boolean cooling, FluidStack fluid) {
		int t = clm.getHeat().getID();
		int h = clm.getHumidity().getID();
		int a = clm.getAirflow().getID();
		boolean f = fluid != null;
		switch (t) {
		case 0:
		case 1:
			return FLEEZING;
		case 2:
		case 3:
		case 4:
			if (h == 0)
				return DRYING;
			else
				return FERMENTATION;
		default:
			if (cooling)
				return DISTILLING;
			else
				return f ? BOILING : COOKING;
		}
	}
}
