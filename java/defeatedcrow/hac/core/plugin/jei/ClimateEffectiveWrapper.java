package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ClimateEffectiveWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final ClimateEffectiveTile rec;

	@SuppressWarnings("unchecked")
	public ClimateEffectiveWrapper(ClimateEffectiveTile recipe) {
		rec = recipe;
		input = new ArrayList<ItemStack>();
		int meta = recipe.getInputMeta();
		int m = meta == 32767 ? OreDictionary.WILDCARD_VALUE : meta;
		Item i = recipe.getInputItem();
		ItemStack item = new ItemStack(i, 1, m);
		input.add(new ItemStack(i, 1, m));
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(ItemStack.class, input);
	}

	@Override
	public List getInputs() {
		return input;
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

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		DCHeatTier heat = rec.getHeat();
		DCHumidity hum = rec.getHumidity();
		DCAirflow air = rec.getAirflow();
		int baseY = 12;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_effective_gui.png");
		mc.getTextureManager().bindTexture(res);
		if (heat != null) {
			mc.currentScreen.drawTexturedModalRect(73, baseY + 9, heat.getID() * 20, 170, 20, 3);
			mc.currentScreen.drawTexturedModalRect(93, baseY + 9, heat.getID() * 20, 170, 20, 3);
		}
		if (hum != null) {
			mc.currentScreen.drawTexturedModalRect(73, baseY + 23, hum.getID() * 40, 174, 40, 3);
		}
		if (air != null) {
			mc.currentScreen.drawTexturedModalRect(73, baseY + 37, air.getID() * 40, 178, 40, 3);
		}

		String t = heat == null ? "  -" : heat.name();
		mc.fontRendererObj.drawString("TEMP", 73, baseY, 0x993030, true);
		mc.fontRendererObj.drawString(t, 93, baseY, 0x993030, true);

		String h = hum == null ? "  -" : hum.name();
		mc.fontRendererObj.drawString("HUM", 73, baseY + 14, 0x303099, true);
		mc.fontRendererObj.drawString(h, 93, baseY + 14, 0x303099, true);

		String a = air == null ? "  -" : air.name();
		mc.fontRendererObj.drawString("AIR", 73, baseY + 28, 0x309930, true);
		mc.fontRendererObj.drawString(a, 93, baseY + 28, 0x309930, true);
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
