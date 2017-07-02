package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidStack;

public class ClimateBiomeWrapper implements IRecipeWrapper {

	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;
	private IClimate climate;
	private final Biome biome;
	private final BiomeDictionary.Type[] types;
	private final boolean hasSeason;

	@SuppressWarnings("unchecked")
	public ClimateBiomeWrapper(Biome recipe) {
		biome = recipe;
		int id = Biome.getIdForBiome(biome);
		IClimate clm = ClimateAPI.register.getClimateFromBiome(id);
		climate = clm;

		temps = new ArrayList<DCHeatTier>();
		temps.add(clm.getHeat());

		hums = new ArrayList<DCHumidity>();
		hums.add(clm.getHumidity());

		airs = new ArrayList<DCAirflow>();
		airs.add(clm.getAirflow());

		types = BiomeDictionary.getTypesForBiome(biome);

		hasSeason = !ClimateAPI.register.getNoSeasonList().isEmpty()
				&& ClimateAPI.register.getNoSeasonList().contains(id);
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(DCHeatTier.class, temps);
		ing.setInputs(DCHumidity.class, hums);
		ing.setInputs(DCAirflow.class, airs);
	}

	@Override
	public List getInputs() {
		return null;
	}

	@Override
	public List getOutputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return null;
	}

	public List<DCHeatTier> getTemps() {
		return temps;
	}

	public List<DCHumidity> getHums() {
		return hums;
	}

	public List<DCAirflow> getAirs() {
		return airs;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		DCHeatTier heat = climate.getHeat();
		DCHeatTier upper = heat.addTier(1);
		DCHeatTier under = heat.addTier(-1);
		if (upper.getTier() > DCHeatTier.HOT.getTier()) {
			upper = DCHeatTier.HOT;
		}
		if (under.getTier() < DCHeatTier.COLD.getTier()) {
			under = DCHeatTier.COLD;
		}
		DCHumidity hum = climate.getHumidity();
		DCAirflow air = climate.getAirflow();
		int baseY = 12;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_biome_gui.png");
		mc.getTextureManager().bindTexture(res);
		if (heat != null) {
			mc.currentScreen.drawTexturedModalRect(17, baseY + 26, heat.getID() * 20, 170, 20, 3);
			mc.currentScreen.drawTexturedModalRect(37, baseY + 26, heat.getID() * 20, 170, 20, 3);
		}
		if (hum != null) {
			mc.currentScreen.drawTexturedModalRect(17, baseY + 48, hum.getID() * 40, 174, 40, 3);
		}
		if (air != null) {
			mc.currentScreen.drawTexturedModalRect(17, baseY + 70, air.getID() * 40, 178, 40, 3);
		}

		String t = heat.name();
		int heatColor = heat.getTier() < 0 ? 0x5050FF : 0xFF5050;
		mc.fontRendererObj.drawString("TEMP : " + biome.getTemperature(), 17, baseY + 14, heatColor, false);
		mc.fontRendererObj.drawString(t, 70, baseY + 22, heatColor, false);

		String h = hum == null ? "  -" : hum.name();
		int humColor = hum.getID() > 0 ? 0x5050FF : 0xFF5050;
		mc.fontRendererObj.drawString("HUM : Rainfall " + biome.getRainfall(), 18, baseY + 35, humColor, false);
		mc.fontRendererObj.drawString(h, 70, baseY + 45, humColor, false);

		String a = air == null ? "  -" : air.name();
		int airColor = air.getID() > 0 ? 0x5050FF : 0xFF5050;
		String airName = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HILLS) ? "AIR : HILLS"
				: "AIR : PLAIN";
		mc.fontRendererObj.drawString(airName, 18, baseY + 58, airColor, false);
		mc.fontRendererObj.drawString(a, 70, baseY + 67, airColor, false);

		if (hasSeason) {
			String n = "no seasonal change";
			mc.fontRendererObj.drawString(n, 18, baseY + 80, 0x000000, false);
		}

		if (types != null) {
			String ty = "";
			for (BiomeDictionary.Type type : types) {
				ty += type.name() + " ";
			}
			mc.fontRendererObj.drawString(ty, 18, baseY - 8, 0x000000, false);
		}

		String b = biome.getBiomeName() + " Biome";
		mc.fontRendererObj.drawString(b, 18, baseY - 20, 0x000000, false);
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		List<String> s = new ArrayList<String>();
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
