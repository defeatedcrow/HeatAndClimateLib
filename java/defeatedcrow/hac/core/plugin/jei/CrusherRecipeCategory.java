package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.ClimateCore;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;

public class CrusherRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public CrusherRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_crusher_gui_jei.png");
		background = guiHelper.createDrawable(location, 30, 10, 140, 60, 0, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.crusher";
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
	public void drawExtras(Minecraft mc) {}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof CrusherRecipeWrapper))
			return;
		CrusherRecipeWrapper wrapper = ((CrusherRecipeWrapper) recipeWrapper);

		List<ItemStack> inputs = wrapper.getInputs();
		List<ItemStack> outputs = wrapper.getOutputs();
		List<List<FluidStack>> outputF = ingredients.getOutputs(FluidStack.class);

		recipeLayout.getItemStacks().init(0, true, 15, 14);
		recipeLayout.getItemStacks().set(0, inputs);

		if (!wrapper.getCatalyst().isEmpty()) {
			recipeLayout.getItemStacks().init(1, true, 46, 14);
			recipeLayout.getItemStacks().set(1, wrapper.getCatalyst());
		}

		if (!outputs.isEmpty()) {
			recipeLayout.getItemStacks().init(2, false, 83, 5);
			recipeLayout.getItemStacks().set(2, outputs.get(0));
		}

		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(3, false, 83, 23);
			recipeLayout.getItemStacks().set(3, outputs.get(1));
		}

		if (outputs.size() > 2) {
			recipeLayout.getItemStacks().init(4, false, 83, 41);
			recipeLayout.getItemStacks().set(4, outputs.get(2));
		}

		if (!outputF.isEmpty()) {
			recipeLayout.getFluidStacks().init(5, false, 119, 8, 12, 50, 5000, false, null);
			recipeLayout.getFluidStacks().set(5, outputF.get(0).get(0));
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
