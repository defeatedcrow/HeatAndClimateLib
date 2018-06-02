package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.climate.IClimate;
import net.minecraftforge.fluids.FluidStack;

public enum FRecipeType {
	FREEZING,
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
		case 2:
		case 3:
			return FREEZING;

		case 4:
		case 5:
		case 6:
			if (h == 0)
				return DRYING;
			else
				return FERMENTATION;
		default:
			return f ? BOILING : COOKING;
		}
	}
}
