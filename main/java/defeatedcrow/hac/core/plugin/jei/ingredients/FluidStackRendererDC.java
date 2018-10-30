package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.List;

import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import mezz.jei.plugins.vanilla.ingredients.FluidStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackRendererDC extends FluidStackRenderer {

	@Override
	public List<String> getTooltip(Minecraft minecraft, FluidStack fluidStack, ITooltipFlag tooltipFlag) {
		List<String> list = super.getTooltip(minecraft, fluidStack, tooltipFlag);
		if (fluidStack != null) {
			String dic = FluidDictionaryDC.getFluidDicName(fluidStack.getFluid());
			if (dic != null)
				list.add("FluidDic: " + dic);
		}
		return list;
	}
}
