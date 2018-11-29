package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.fluid.FluidDic;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.ItemStackRendererDC;
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

public class FluidRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public FluidRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_fluidcraft_gui_jei.png");
		background = guiHelper.createDrawable(location, 8, 6, 160, 105, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.fluidcraft";
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
		if (!(recipeWrapper instanceof FluidRecipeWrapper))
			return;
		FluidRecipeWrapper wrapper = ((FluidRecipeWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<List<ItemStack>> inputs = wrapper.getInputs();
		List<ItemStack> outputs = wrapper.getOutputs();
		List<FluidStack> inF = wrapper.getFluidInputs();
		List<FluidStack> outF = wrapper.getFluidOutputs();

		if (!inputs.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				int l = i;
				if (l < inputs.size()) {
					recipeLayout.getItemStacks().init(l, true, 48, 13 + i * 18);
					if (inputs.get(l) instanceof List) {
						recipeLayout.getItemStacks().set(l, inputs.get(l));
					}
				}
			}
		}

		recipeLayout.getItemStacks().init(4, false, 93, 13);
		recipeLayout.getItemStacks().set(4, outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(5, false, new ItemStackRendererDC(wrapper.chance), 94, 32, 16, 16, 0, 0);
			recipeLayout.getItemStacks().set(5, outputs.get(1));
		}

		if (!inF.isEmpty() && inF.get(0) != null) {
			FluidStack f1 = inF.get(0);
			recipeLayout.getFluidStacks().init(0, false, 30, 16, 12, 50, 5000, false, null);
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

		}
		if (!outF.isEmpty() && outF.get(0) != null) {
			FluidStack f2 = outF.get(0);
			recipeLayout.getFluidStacks().init(1, false, 117, 16, 12, 50, 5000, false, null);
			recipeLayout.getFluidStacks().set(1, f2);
		}

		List<DCHeatTier> temps = wrapper.getTemps();
		int i = 0;
		for (DCHeatTier temp : temps) {
			recipeLayout.getIngredientsGroup(DCHeatTier.class).init(i, true, new HeatTierRenderer(),
					38 + temp.getID() * 6, 74, 6, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHeatTier.class).set(i, temp);
			i++;
		}

		List<DCHumidity> hums = wrapper.getHums();
		int j = 0;
		for (DCHumidity hum : hums) {
			recipeLayout.getIngredientsGroup(DCHumidity.class).init(j, true, new HumidityRenderer(),
					38 + hum.getID() * 21, 84, 21, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHumidity.class).set(j, hum);
			j++;
		}

		List<DCAirflow> airs = wrapper.getAirs();
		int k = 0;
		for (DCAirflow air : airs) {
			recipeLayout.getIngredientsGroup(DCAirflow.class).init(k, true, new AirflowRenderer(),
					38 + air.getID() * 21, 94, 21, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCAirflow.class).set(k, air);
			k++;
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
