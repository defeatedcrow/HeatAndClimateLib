package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ClimateSmeltingWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final IClimateSmelting rec;
	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;

	@SuppressWarnings("unchecked")
	public ClimateSmeltingWrapper(IClimateSmelting recipe) {
		rec = recipe;
		input = recipe.getProcessedInput();
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}

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
		ing.setInputs(VanillaTypes.ITEM, input);
		ing.setOutputs(VanillaTypes.ITEM, output);
		ing.setInputs(ClimateTypes.TEMP, temps);
		ing.setInputs(ClimateTypes.HUM, hums);
		ing.setInputs(ClimateTypes.AIR, airs);
	}

	public List<ItemStack> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		List<DCHeatTier> heats = rec.requiredHeat();
		List<DCHumidity> hums = rec.requiredHum();
		List<DCAirflow> airs = rec.requiredAir();
		DCHeatTier minT = DCHeatTier.INFERNO;
		DCHumidity maxH = DCHumidity.DRY;
		DCAirflow maxA = DCAirflow.TIGHT;
		int baseY = 43;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_smelting_gui_jei.png");
		mc.getTextureManager().bindTexture(res);
		if (heats.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(38, baseY, 0, 170, 84, 3);
			minT = DCHeatTier.NORMAL;
		} else {
			for (DCHeatTier h : heats) {
				mc.currentScreen.drawTexturedModalRect(38 + h.getID() * 6, baseY, h.getID() * 6, 170, 6, 3);
				if (h.getID() < minT.getID())
					minT = h;
			}
		}
		if (hums.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(38, baseY + 10, 0, 174, 84, 3);
			maxH = DCHumidity.NORMAL;
		} else {
			for (DCHumidity h : hums) {
				mc.currentScreen.drawTexturedModalRect(38 + h.getID() * 21, baseY + 10, h.getID() * 21, 174, 21, 3);
				if (maxH.getID() < h.getID())
					maxH = h;
			}
		}
		if (airs.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(38, baseY + 20, 0, 178, 84, 3);
			maxA = DCAirflow.FLOW;
		} else {
			for (DCAirflow a : airs) {
				mc.currentScreen.drawTexturedModalRect(38 + a.getID() * 21, baseY + 20, a.getID() * 21, 178, 21, 3);
				if (maxA.getID() < a.getID())
					maxA = a;
			}
		}

		IClimate clm = ClimateAPI.register.getClimateFromParam(minT, maxH, maxA);
		String s = CRecipeType.getType(clm).name();
		mc.fontRenderer.drawString(s, 46, 2, 0x0099FF, false);

		String place = "Require the processing device.";
		if (rec.hasPlaceableOutput() > 0) {
			place = "Proceeds as a placed object.";
		}
		if (rec.canProceedAsDropItem()) {
			place = "Proceeds as a drop item entity.";
		}
		mc.fontRenderer.drawString(place, 32, 69, 0x0099FF, false);

		String flq = "Less Frequency Process";
		if (rec.recipeFrequency() == 0) {
			flq = "Rapid Process";
		} else if (rec.recipeFrequency() == 1) {
			flq = "Middle Frequency Process";
		}
		mc.fontRenderer.drawString(flq, 32, 79, 0x0099FF, false);
	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		List<String> s = new ArrayList<String>();
		if (y > 40 && y < 48) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 6;
				s.add(DCHeatTier.getTypeByID(i).localize() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > 50 && y < 58) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCHumidity.getTypeByID(i).localize());
			}
		}
		if (y > 60 && y < 68) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCAirflow.getTypeByID(i).localize());
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
