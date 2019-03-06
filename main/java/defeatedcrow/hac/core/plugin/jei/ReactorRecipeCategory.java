package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.fluid.FluidDic;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ReactorRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ReactorRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_reactor_gui_jei.png");
		background = guiHelper.createDrawable(location, 4, 2, 168, 120);
	}

	@Override
	public String getUid() {
		return "dcs_climate.reactor";
	}

	@Override
	public String getTitle() {
		return I18n.format(getUid());
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
		if (!(recipeWrapper instanceof ReactorRecipeWrapper))
			return;
		ReactorRecipeWrapper wrapper = ((ReactorRecipeWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<List<ItemStack>> inputs = wrapper.getInputs();
		List<ItemStack> outputs = wrapper.getOutputs();
		List<FluidStack> inF1 = wrapper.getFluidInputs();
		List<FluidStack> outF1 = wrapper.getFluidOutputs();
		List<ItemStack> catalyst = wrapper.getCatalyst();

		if (!inputs.isEmpty()) {
			for (int i = 0; i < 4; i++) {
				int l = i;
				if (l < inputs.size()) {
					recipeLayout.getItemStacks().init(l, true, 7 + i * 18, 64);
					if (inputs.get(l) instanceof List) {
						recipeLayout.getItemStacks().set(l, inputs.get(l));
					}
				}
			}
		}

		if (!outputs.isEmpty()) {
			recipeLayout.getItemStacks().init(4, false, 89, 94);
			recipeLayout.getItemStacks().set(4, outputs.get(0));
			if (outputs.size() > 1) {
				recipeLayout.getItemStacks().init(5, false, 108, 95);
				recipeLayout.getItemStacks().set(5, outputs.get(1));
			}
		}

		if (!catalyst.isEmpty()) {
			recipeLayout.getItemStacks().init(6, false, 93, 10);
			recipeLayout.getItemStacks().set(6, catalyst);
		}

		if (!inF1.isEmpty() && inF1.get(0) != null) {
			FluidStack f1 = inF1.get(0);
			recipeLayout.getFluidStacks().init(0, false, 7, 18, 12, 40, 4000, false, null);
			FluidDic dic = FluidDictionaryDC.getDic(f1);
			if (dic != null && !dic.fluids.isEmpty()) {
				List<FluidStack> ret = Lists.newArrayList();
				for (Fluid f : dic.fluids) {
					ret.add(new FluidStack(f, f1.amount));
				}
				recipeLayout.getFluidStacks().set(0, ret);
			} else {
				recipeLayout.getFluidStacks().set(0, f1);
			}
			if (inF1.size() > 1) {
				FluidStack f2 = inF1.get(1);
				recipeLayout.getFluidStacks().init(1, false, 47, 18, 12, 40, 4000, false, null);
				FluidDic dic2 = FluidDictionaryDC.getDic(f2);
				if (dic2 != null && !dic2.fluids.isEmpty()) {
					List<FluidStack> ret = Lists.newArrayList();
					for (Fluid f : dic2.fluids) {
						ret.add(new FluidStack(f, f2.amount));
					}
					recipeLayout.getFluidStacks().set(1, ret);
				} else {
					recipeLayout.getFluidStacks().set(1, f2);
				}
			}
		}

		if (!outF1.isEmpty() && outF1.get(0) != null) {
			FluidStack f3 = outF1.get(0);
			recipeLayout.getFluidStacks().init(2, false, 89, 48, 12, 40, 4000, false, null);
			recipeLayout.getFluidStacks().set(2, f3);
			if (outF1.size() > 1) {
				FluidStack f4 = outF1.get(1);
				recipeLayout.getFluidStacks().init(3, false, 129, 48, 12, 40, 4000, false, null);
				recipeLayout.getFluidStacks().set(3, f4);
			}
		}

		List<DCHeatTier> temps = wrapper.getTemps();
		int i = 0;
		for (DCHeatTier temp : temps) {
			recipeLayout.getIngredientsGroup(ClimateTypes.TEMP).init(i, true, new HeatTierRenderer(),
					2 + temp.getID() * 6, 103, 6, 5, 0, 0);
			recipeLayout.getIngredientsGroup(ClimateTypes.TEMP).set(i, temp);
			i++;
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
