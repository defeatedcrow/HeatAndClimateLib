package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class ClimateEffectiveWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final ClimateEffectiveTile rec;
	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;

	@SuppressWarnings("unchecked")
	public ClimateEffectiveWrapper(ClimateEffectiveTile recipe) {
		rec = recipe;
		input = new ArrayList<ItemStack>();
		int meta = recipe.getInputMeta();
		int m = meta == 32767 ? OreDictionary.WILDCARD_VALUE : meta;
		Item i = recipe.getInputItem();
		ItemStack item = new ItemStack(i, 1, m);
		input.add(new ItemStack(i, 1, m));

		temps = new ArrayList<DCHeatTier>();
		temps.add(recipe.getHeat());

		hums = new ArrayList<DCHumidity>();
		hums.add(recipe.getHumidity());

		airs = new ArrayList<DCAirflow>();
		airs.add(recipe.getAirflow());
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(VanillaTypes.ITEM, input);
		ing.setInputs(ClimateTypes.TEMP, temps);
		ing.setInputs(ClimateTypes.HUM, hums);
		ing.setInputs(ClimateTypes.AIR, airs);
	}

	public List<ItemStack> getInputs() {
		return input;
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
		DCHeatTier heat = rec.getHeat();
		DCHumidity hum = rec.getHumidity();
		DCAirflow air = rec.getAirflow();
		int baseY = 12;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_effective_gui.png");
		mc.getTextureManager().bindTexture(res);
		if (heat != null) {
			if (heat.getID() > 6) {
				mc.currentScreen.drawTexturedModalRect(73, baseY + 9, heat.getID() * 20 - 140, 174, 20, 3);
				mc.currentScreen.drawTexturedModalRect(93, baseY + 9, heat.getID() * 20 - 140, 174, 20, 3);
			} else {
				mc.currentScreen.drawTexturedModalRect(73, baseY + 9, heat.getID() * 20, 170, 20, 3);
				mc.currentScreen.drawTexturedModalRect(93, baseY + 9, heat.getID() * 20, 170, 20, 3);
			}
		}
		if (hum != null) {
			mc.currentScreen.drawTexturedModalRect(73, baseY + 23, hum.getID() * 40, 178, 40, 3);
		}
		if (air != null) {
			mc.currentScreen.drawTexturedModalRect(73, baseY + 37, air.getID() * 40, 182, 40, 3);
		}

		String t = heat == null ? " -" : heat.localize();
		mc.fontRenderer.drawString(DCHeatTier.basename2() + " " + t, 73, baseY, 0x993030, false);

		String h = hum == null ? "  -" : hum.localize();
		mc.fontRenderer.drawString(DCHumidity.basename2() + " " + h, 73, baseY + 14, 0x303099, false);

		String a = air == null ? "  -" : air.localize();
		mc.fontRenderer.drawString(DCAirflow.basename2() + " " + a, 73, baseY + 28, 0x309930, false);
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
