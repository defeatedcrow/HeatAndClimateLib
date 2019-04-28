package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IFluidRecipe;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipeWrapper implements IRecipeWrapper {

	private final List<List<ItemStack>> input;
	private final List<ItemStack> output;
	private final IFluidRecipe rec;
	private final List<FluidStack> inF;
	private final List<FluidStack> outF;
	private final String type;
	private final boolean cooling;
	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;
	public final float chance;

	@SuppressWarnings("unchecked")
	public FluidRecipeWrapper(IFluidRecipe recipe) {
		type = recipe.additionalString();
		rec = recipe;
		input = new ArrayList<List<ItemStack>>();
		if (!recipe.getProcessedInput().isEmpty()) {
			for (Object obj : recipe.getProcessedInput()) {
				if (obj instanceof ItemStack) {
					List<ItemStack> ret = new ArrayList<ItemStack>();
					ret.add((ItemStack) obj);
					input.add(ret);
				} else if (obj instanceof List) {
					input.add((List<ItemStack>) obj);
				}
			}
		}
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}
		chance = recipe.getSecondaryChance();

		inF = new ArrayList<FluidStack>();
		outF = new ArrayList<FluidStack>();
		if (recipe.getInputFluid() != null) {
			inF.add(recipe.getInputFluid());
		}
		if (recipe.getOutputFluid() != null) {
			outF.add(recipe.getOutputFluid());
		}
		cooling = recipe.isNeedCooling();

		temps = new ArrayList<DCHeatTier>();
		temps.addAll(recipe.requiredHeat());
		if (temps.isEmpty()) {
			temps.addAll(DCHeatTier.createList());
		}

		hums = new ArrayList<DCHumidity>();
		hums.addAll(recipe.requiredHum());
		if (hums.isEmpty()) {
			hums.addAll(DCHumidity.createList());
		}

		airs = new ArrayList<DCAirflow>();
		airs.addAll(recipe.requiredAir());
		if (airs.isEmpty()) {
			airs.addAll(DCAirflow.createList());
		}
	}

	public List<DCAirflow> getAirs() {
		return airs;
	}

	public List<DCHumidity> getHums() {
		return hums;
	}

	public List<DCHeatTier> getTemps() {
		return temps;
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputLists(VanillaTypes.ITEM, input);
		ing.setInputs(VanillaTypes.FLUID, inF);
		ing.setOutputs(VanillaTypes.ITEM, output);
		ing.setOutputs(VanillaTypes.FLUID, outF);
		ing.setInputs(ClimateTypes.TEMP, temps);
		ing.setInputs(ClimateTypes.HUM, hums);
		ing.setInputs(ClimateTypes.AIR, airs);
	}

	public List<List<ItemStack>> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
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
		List<DCHumidity> hums = rec.requiredHum();
		List<DCAirflow> airs = rec.requiredAir();
		DCHeatTier minT = DCHeatTier.INFERNO;
		DCHumidity maxH = DCHumidity.DRY;
		DCAirflow maxA = DCAirflow.TIGHT;
		int baseX = 38;
		int baseY = 75;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_fluidcraft_gui_jei.png");
		mc.getTextureManager().bindTexture(res);
		if (heats.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(baseX, baseY, 0, 170, 84, 3);
			minT = DCHeatTier.NORMAL;
		} else {
			for (DCHeatTier h : heats) {
				mc.currentScreen.drawTexturedModalRect(baseX + h.getID() * 6, baseY, h.getID() * 6, 170, 6, 3);
				if (h.getID() < minT.getID())
					minT = h;
			}
		}
		if (hums.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(baseX, baseY + 10, 0, 174, 84, 3);
			maxH = DCHumidity.NORMAL;
		} else {
			for (DCHumidity h : hums) {
				mc.currentScreen.drawTexturedModalRect(baseX + h.getID() * 21, baseY + 10, h.getID() * 21, 174, 21, 3);
				if (maxH.getID() < h.getID())
					maxH = h;
			}
		}
		if (airs.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(baseX, baseY + 20, 0, 178, 84, 3);
			maxA = DCAirflow.NORMAL;
		} else {
			for (DCAirflow a : airs) {
				mc.currentScreen.drawTexturedModalRect(baseX + a.getID() * 21, baseY + 20, a.getID() * 21, 178, 21, 3);
				if (maxA.getID() < a.getID())
					maxA = a;
			}
		}

		IClimate clm = ClimateAPI.register.getClimateFromParam(minT, maxH, maxA);
		FluidStack fluid = inF.isEmpty() ? null : inF.get(0);
		String message = FRecipeType.getType(clm, cooling, fluid).name();
		mc.fontRenderer.drawString(message + " " + type, 30, 0, 0x0099FF, false);
	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		int baseY = 72;
		List<String> s = new ArrayList<String>();
		if (y > baseY && y < baseY + 8) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 6;
				s.add(DCHeatTier.getTypeByID(i).localize() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > baseY + 10 && y < baseY + 18) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCHumidity.getTypeByID(i).localize());
			}
		}
		if (y > baseY + 20 && y < baseY + 28) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCAirflow.getTypeByID(i).localize());
			}
		}
		if (y > 31 && y < 49) {
			if (x > 94 && x < 112) {
				int i = (int) (rec.getSecondaryChance() * 100);
				if (rec.getSecondary() == null || i == 0) {
					s.add("NO SECONDARY OUTPUT");
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
