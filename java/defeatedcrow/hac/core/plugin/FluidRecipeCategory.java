package defeatedcrow.hac.core.plugin;

import java.util.Collection;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public FluidRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_fluidcraft_gui.png");
		background = guiHelper.createDrawable(location, 8, 6, 160, 105, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.fluidcraft";
	}

	@Override
	public String getTitle() {
		return I18n.translateToLocal(getUid());
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft mc) {
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if (!(recipeWrapper instanceof FluidRecipeWrapper))
			return;
		FluidRecipeWrapper wrapper = ((FluidRecipeWrapper) recipeWrapper);

		List inputs = wrapper.getInputs();
		List outputs = wrapper.getOutputs();
		List<FluidStack> inF = wrapper.getFluidInputs();
		List<FluidStack> outF = wrapper.getFluidOutputs();

		if (!inputs.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				int l = i;
				if (l < inputs.size()) {
					recipeLayout.getItemStacks().init(l, true, 48, 13 + i * 18);
					if (inputs.get(l) instanceof ItemStack) {
						recipeLayout.getItemStacks().set(l, (ItemStack) inputs.get(l));
					} else if (inputs.get(l) instanceof Collection) {
						recipeLayout.getItemStacks().set(l, (Collection<ItemStack>) inputs.get(l));
					}
				}
			}
		}

		recipeLayout.getItemStacks().init(4, false, 93, 13);
		recipeLayout.getItemStacks().set(4, (ItemStack) outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(5, false, 93, 31);
			recipeLayout.getItemStacks().set(5, (ItemStack) outputs.get(1));
		}

		if (!inF.isEmpty() && inF.get(0) != null) {
			FluidStack f1 = inF.get(0);
			recipeLayout.getFluidStacks().init(0, false, 30, 16, 12, 50, 5000, false, null);
			recipeLayout.getFluidStacks().set(0, f1);
		}
		if (!outF.isEmpty() && outF.get(0) != null) {
			FluidStack f2 = outF.get(0);
			recipeLayout.getFluidStacks().init(1, false, 117, 16, 12, 50, 5000, false, null);
			recipeLayout.getFluidStacks().set(1, f2);
		}
	}

}
