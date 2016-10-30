package defeatedcrow.hac.core.plugin;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.core.climate.recipe.FluidCraftRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipeWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final FluidCraftRecipe rec;
	private final List<FluidStack> inF;
	private final List<FluidStack> outF;
	private final String type;
	private final boolean cooling;

	@SuppressWarnings("unchecked")
	public FluidRecipeWrapper(FluidCraftRecipe recipe) {
		type = recipe.additionalString();
		rec = recipe;
		input = recipe.getProcessedInput();
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (recipe.getSecondary() != null) {
			output.add(recipe.getSecondary());
		}

		inF = new ArrayList<FluidStack>();
		outF = new ArrayList<FluidStack>();
		if (recipe.getInputFluid() != null) {
			inF.add(recipe.getInputFluid());
		}
		if (recipe.getOutputFluid() != null) {
			outF.add(recipe.getOutputFluid());
		}
		cooling = recipe.isNeedCooling();
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
		return inF;
	}

	@Override
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
		int baseY = 75;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_fluidcraft_gui_jei.png");
		mc.getTextureManager().bindTexture(res);
		if (heats.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(44, baseY, 0, 170, 72, 3);
			minT = DCHeatTier.NORMAL;
		} else {
			for (DCHeatTier h : heats) {
				mc.currentScreen.drawTexturedModalRect(44 + h.getID() * 6, baseY, h.getID() * 6, 170, 6, 3);
				if (h.getID() < minT.getID())
					minT = h;
			}
		}
		if (hums.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(44, baseY + 10, 0, 174, 72, 3);
			maxH = DCHumidity.NORMAL;
		} else {
			for (DCHumidity h : hums) {
				mc.currentScreen.drawTexturedModalRect(44 + h.getID() * 18, baseY + 10, h.getID() * 18, 174, 18, 3);
				if (maxH.getID() < h.getID())
					maxH = h;
			}
		}
		if (airs.isEmpty()) {
			mc.currentScreen.drawTexturedModalRect(44, baseY + 20, 0, 178, 72, 3);
			maxA = DCAirflow.NORMAL;
		} else {
			for (DCAirflow a : airs) {
				mc.currentScreen.drawTexturedModalRect(44 + a.getID() * 18, baseY + 20, a.getID() * 18, 178, 18, 3);
				if (maxA.getID() < a.getID())
					maxA = a;
			}
		}

		IClimate clm = ClimateAPI.register.getClimateFromParam(minT, maxH, maxA);
		FluidStack fluid = inF.isEmpty() ? null : inF.get(0);
		String message = FRecipeType.getType(clm, cooling, fluid).name();
		mc.fontRendererObj.drawString(message + " " + type, 30, 0, 0x0099FF, true);
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		int baseY = 72;
		List<String> s = new ArrayList<String>();
		if (y > baseY && y < baseY + 8) {
			if (x > 44 && x < 116) {
				int i = (x - 44) / 6;
				s.add(DCHeatTier.getTypeByID(i).name() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > baseY + 10 && y < baseY + 18) {
			if (x > 44 && x < 116) {
				int i = (x - 44) / 18;
				s.add(DCHumidity.getTypeByID(i).name());
			}
		}
		if (y > baseY + 20 && y < baseY + 28) {
			if (x > 44 && x < 116) {
				int i = (x - 44) / 18;
				s.add(DCAirflow.getTypeByID(i).name());
			}
		}
		if (y > 31 && y < 49) {
			if (x > 94 && x < 112) {
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
