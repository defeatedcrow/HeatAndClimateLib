package defeatedcrow.hac.core.plugin.jei;

import mezz.jei.api.IGuiHelper;

public class FluidRecipeCategory_Pottery extends CrusherRecipeCategory {

	public FluidRecipeCategory_Pottery(IGuiHelper guiHelper) {
		super(guiHelper);
	}

	@Override
	public String getUid() {
		return "dcs_climate.fluidcraft_pottery";
	}

}
