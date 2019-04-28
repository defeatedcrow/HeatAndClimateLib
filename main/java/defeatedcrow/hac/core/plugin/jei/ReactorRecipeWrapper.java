package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IReactorRecipe;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class ReactorRecipeWrapper implements IRecipeWrapper {

	private final List<List<ItemStack>> input;
	private final List<List<ItemStack>> input2;
	private final List<ItemStack> output;
	private final IReactorRecipe rec;
	private final List<ItemStack> catalyst;
	private final List<FluidStack> inF;
	private final List<FluidStack> outF;
	private final List<DCHeatTier> temps;
	public final float chance;

	@SuppressWarnings("unchecked")
	public ReactorRecipeWrapper(IReactorRecipe recipe) {
		rec = recipe;
		input = Lists.newArrayList();
		input2 = Lists.newArrayList();
		if (!recipe.getProcessedInput().isEmpty()) {
			for (Object obj : recipe.getProcessedInput()) {
				if (obj instanceof ItemStack) {
					List<ItemStack> ret = new ArrayList<>();
					ret.add((ItemStack) obj);
					input.add(ret);
				} else if (obj instanceof List) {
					input.add((List<ItemStack>) obj);
				}
			}
		}
		catalyst = new ArrayList<>();
		catalyst.addAll(recipe.getCatalyst());
		input2.addAll(input);
		input2.add(catalyst);

		output = new ArrayList<>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}
		chance = recipe.getSecondaryChance();

		inF = new ArrayList<>();
		outF = new ArrayList<>();
		if (recipe.getInputFluid() != null) {
			inF.add(recipe.getInputFluid());
		}
		if (recipe.getSubInputFluid() != null) {
			inF.add(recipe.getSubInputFluid());
		}
		if (recipe.getOutputFluid() != null) {
			outF.add(recipe.getOutputFluid());
		}
		if (recipe.getSubOutputFluid() != null) {
			outF.add(recipe.getSubOutputFluid());
		}

		temps = new ArrayList<>();
		temps.addAll(recipe.requiredHeat());
		if (temps.isEmpty()) {
			temps.addAll(DCHeatTier.createList());
		}
	}

	public List<DCHeatTier> getTemps() {
		return temps;
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputLists(VanillaTypes.ITEM, input2);
		ing.setInputs(VanillaTypes.FLUID, inF);
		ing.setOutputs(VanillaTypes.ITEM, output);
		ing.setOutputs(VanillaTypes.FLUID, outF);
		ing.setInputs(ClimateTypes.TEMP, temps);
	}

	public List<List<ItemStack>> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
	}

	public List<ItemStack> getCatalyst() {
		return catalyst;
	}

	public List<FluidStack> getFluidInputs() {
		return inF;
	}

	public List<FluidStack> getFluidOutputs() {
		return outF;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		List<DCHeatTier> heats = rec.requiredHeat();
		DCHeatTier minT = DCHeatTier.INFERNO;
		DCHumidity maxH = DCHumidity.DRY;
		DCAirflow maxA = DCAirflow.TIGHT;
		int baseY = 104;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_reactor_gui_jei.png");
		mc.getTextureManager().bindTexture(res);
		if (heats.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(2, baseY, 0, 170, 84, 3);
			minT = DCHeatTier.NORMAL;
		} else {
			for (DCHeatTier h : heats) {
				mc.currentScreen.drawTexturedModalRect(2 + h.getID() * 6, baseY, h.getID() * 6, 170, 6, 3);
				if (h.getID() < minT.getID())
					minT = h;
			}
		}

		int i = (int) (rec.getSecondaryChance() * 100);
		if (!DCUtil.isEmpty(rec.getSecondary()) && i > 0) {
			mc.fontRenderer.drawString(i + "%", 110, 113, 0x0099FF, false);
		}

		IClimate clm = ClimateAPI.register.getClimateFromParam(minT, maxH, maxA);
	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		int baseY = 102;
		List<String> s = new ArrayList<String>();
		if (y > baseY && y < baseY + 8) {
			if (x > 2 && x < 86) {
				int i = (x - 3) / 6;
				s.add(DCHeatTier.getTypeByID(i).localize() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > 94 && y < 112) {
			if (x > 108 && x < 124) {
				int i = (int) (rec.getSecondaryChance() * 100);
				if (DCUtil.isEmpty(rec.getSecondary()) || i == 0) {
					s.add("NO SECONDARY OUTPUT");
				}
			}
		}
		if (y > 10 && y < 26) {
			if (x > 93 && x < 109) {
				if (rec.getCatalyst() == null || rec.getCatalyst().isEmpty()) {
					s.add("NO CATALYST REQUIRED");
				} else {
					s.add("CATALYST");
				}
			}
		}
		// if (s.isEmpty())
		// s.add(x + ", " + y);

		return s.isEmpty() ? null : s;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
