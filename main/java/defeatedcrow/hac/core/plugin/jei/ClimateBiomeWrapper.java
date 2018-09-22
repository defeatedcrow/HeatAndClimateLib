package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.EnumSeason;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ClimateBiomeWrapper implements IRecipeWrapper {

	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;
	private final Biome biome;
	private final Set<Type> types;
	private final boolean hasSeason;

	@SuppressWarnings("unchecked")
	public ClimateBiomeWrapper(Biome recipe) {
		biome = recipe;
		int id = Biome.getIdForBiome(biome);
		DCHeatTier tem = ClimateAPI.register.getHeatTier(id);
		DCHumidity hum = ClimateAPI.register.getHumidity(id);
		DCAirflow air = ClimateAPI.register.getAirflow(id);

		hums = new ArrayList<DCHumidity>();
		hums.add(hum);

		airs = new ArrayList<DCAirflow>();
		airs.add(air);

		types = BiomeDictionary.getTypes(biome);

		hasSeason = !ClimateAPI.register.getNoSeasonList().isEmpty()
				&& ClimateAPI.register.getNoSeasonList().contains(id);

		temps = new ArrayList<DCHeatTier>();
		if (hasSeason) {
			temps.add(tem);
			temps.add(tem);
			temps.add(tem);
			temps.add(tem);
		} else {
			float spr = biome.getDefaultTemperature() + (float) EnumSeason.SPRING.temp;
			float smr = biome.getDefaultTemperature() + (float) EnumSeason.SUMMER.temp;
			float aut = biome.getDefaultTemperature() + (float) EnumSeason.AUTUMN.temp;
			float wtr = biome.getDefaultTemperature() + (float) EnumSeason.WINTER.temp;
			temps.add(DCHeatTier.getTypeByBiomeTemp(spr));
			temps.add(DCHeatTier.getTypeByBiomeTemp(smr));
			temps.add(DCHeatTier.getTypeByBiomeTemp(aut));
			temps.add(DCHeatTier.getTypeByBiomeTemp(wtr));
		}
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(DCHeatTier.class, temps);
		ing.setInputs(DCHumidity.class, hums);
		ing.setInputs(DCAirflow.class, airs);
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
		DCHumidity hum = hums.get(0);
		DCAirflow air = airs.get(0);
		int baseY = 25;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_biome_gui.png");
		mc.getTextureManager().bindTexture(res);

		for (int i = 0; i < 4; i++) {
			DCHeatTier heat = temps.get(i);
			if (heat != null) {
				if (heat.getID() > 6) {
					mc.currentScreen.drawTexturedModalRect(17 + 21 * i, baseY + 21, heat.getID() * 20 - 140, 174, 20,
							3);
				} else {
					mc.currentScreen.drawTexturedModalRect(17 + 21 * i, baseY + 21, heat.getID() * 20, 170, 20, 3);
				}
			}
		}

		if (hum != null) {
			mc.currentScreen.drawTexturedModalRect(17, baseY + 42, hum.getID() * 40, 178, 40, 3);
		}
		if (air != null) {
			mc.currentScreen.drawTexturedModalRect(17, baseY + 63, air.getID() * 40, 182, 40, 3);
		}

		String spr = "SPR";
		mc.fontRenderer.drawString(spr, 27 - mc.fontRenderer.getStringWidth(spr) / 2, baseY + 12, 0x000000, false);

		String smr = "SMR";
		mc.fontRenderer.drawString(smr, 48 - mc.fontRenderer.getStringWidth(smr) / 2, baseY + 12, 0x000000, false);

		String aut = "AUT";
		mc.fontRenderer.drawString(aut, 69 - mc.fontRenderer.getStringWidth(aut) / 2, baseY + 12, 0x000000, false);

		String wtr = "WTR";
		mc.fontRenderer.drawString(wtr, 90 - mc.fontRenderer.getStringWidth(wtr) / 2, baseY + 12, 0x000000, false);

		DCHeatTier heat = temps.get(2);
		String t = heat.name();
		int heatColor = heat.getTier() < 0 ? 0x5050FF : 0xFF5050;
		mc.fontRenderer.drawString("BASE TEMP : " + biome.getDefaultTemperature() + " (" + t + ")", 18, baseY + 25,
				heatColor, false);

		String h = hum == null ? "  -" : hum.name();
		int humColor = hum.getID() > 0 ? 0x5050FF : 0xFF5050;
		mc.fontRenderer.drawString("HUM : Rainfall " + biome.getRainfall() + " (" + h + ")", 18, baseY + 46, humColor,
				false);

		String a = air == null ? "  -" : air.name();
		int airColor = air.getID() > 0 ? 0x5050FF : 0xFF5050;
		String airName = BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS) ? "AIR : HILLS" : "AIR : PLAIN";
		mc.fontRenderer.drawString(airName + " (" + a + ")", 18, baseY + 67, airColor, false);

		if (hasSeason) {
			String n = "no seasonal change";
			mc.fontRenderer.drawString(n, 18, baseY + 80, 0x000000, false);
		}

		if (types != null) {
			String ty = "";
			for (BiomeDictionary.Type type : types) {
				ty += type.getName() + " ";
			}
			mc.fontRenderer.drawString(ty, 18, baseY - 8, 0x000000, false);
		}

		String b = biome.getBiomeName() + " Biome";
		mc.fontRenderer.drawString(b, 18, baseY - 20, 0x000000, false);
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
