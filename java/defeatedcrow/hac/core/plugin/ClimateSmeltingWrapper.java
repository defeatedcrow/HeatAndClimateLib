package defeatedcrow.hac.core.plugin;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;

public class ClimateSmeltingWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final ClimateSmelting rec;

	@SuppressWarnings("unchecked")
	public ClimateSmeltingWrapper(ClimateSmelting recipe) {
		rec = recipe;
		input = recipe.getProcessedInput();
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (recipe.getSecondary() != null) {
			output.add(recipe.getSecondary());
		}
	}

	@Override
	public List getInputs() {
		return input;
	}

	@Override
	public List getOutputs() {
		return output;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return null;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		List<DCHeatTier> heats = rec.requiredHeat();
		List<DCHumidity> hums = rec.requiredHum();
		List<DCAirflow> airs = rec.requiredAir();
		DCHeatTier minT = DCHeatTier.UHT;
		DCHumidity maxH = DCHumidity.DRY;
		DCAirflow maxA = DCAirflow.TIGHT;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_smelting_gui.png");
		mc.getTextureManager().bindTexture(res);
		if (heats.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(48, 43, 0, 170, 64, 3);
			minT = DCHeatTier.NORMAL;
		} else {
			for (DCHeatTier h : heats) {
				mc.currentScreen.drawTexturedModalRect(48 + h.getID() * 8, 43, h.getID() * 8, 170, 8, 3);
				if (h.getID() < minT.getID())
					minT = h;
			}
		}
		if (hums.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(48, 53, 0, 174, 64, 3);
			maxH = DCHumidity.NORMAL;
		} else {
			for (DCHumidity h : hums) {
				mc.currentScreen.drawTexturedModalRect(48 + h.getID() * 16, 53, h.getID() * 16, 174, 16, 3);
				if (maxH.getID() < h.getID())
					maxH = h;
			}
		}
		if (airs.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(48, 63, 0, 178, 64, 3);
			maxA = DCAirflow.NORMAL;
		} else {
			for (DCAirflow a : airs) {
				mc.currentScreen.drawTexturedModalRect(48 + a.getID() * 16, 63, a.getID() * 16, 178, 16, 3);
				if (maxA.getID() < a.getID())
					maxA = a;
			}
		}

		IClimate clm = ClimateAPI.register.getClimateFromParam(minT, maxH, maxA);
		String s = CRecipeType.getType(clm).name();
		mc.fontRendererObj.drawString(s, 46, 2, 0x0099FF, true);

		String place = "Require the processing device.";
		if (rec.hasPlaceableOutput() > 0) {
			place = "Proceeds as placed object.";
		}
		mc.fontRendererObj.drawString(place, 32, 69, 0x0099FF, true);

		String flq = "Less Frequency Process";
		if (rec.recipeFrequency() == 0) {
			flq = "Rapid Process";
		} else if (rec.recipeFrequency() == 1) {
			flq = "Middle Frequency Process";
		}
		mc.fontRendererObj.drawString(flq, 32, 79, 0x0099FF, true);
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		List<String> s = new ArrayList<String>();
		if (y > 40 && y < 48) {
			if (x > 46 && x < 111) {
				int i = (x - 47) / 8;
				s.add(DCHeatTier.getTypeByID(i).name() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > 50 && y < 58) {
			if (x > 46 && x < 111) {
				int i = (x - 47) / 16;
				s.add(DCHumidity.getTypeByID(i).name());
			}
		}
		if (y > 60 && y < 68) {
			if (x > 46 && x < 111) {
				int i = (x - 47) / 16;
				s.add(DCAirflow.getTypeByID(i).name());
			}
		}
		if (y > 14 && y < 30) {
			if (x > 118 && x < 132) {
				int i = (int) (rec.getSecondaryChance() * 100);
				if (rec.getSecondary() == null || i == 0) {
					s.add("NO SECONDARY OUTPUT");
				} else {
					s.add("CHANCE: " + i + "%");
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
