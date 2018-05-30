package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ClimateCropWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final IClimateCrop rec;
	private final Block block;
	private final List<DCHeatTier> temps;
	private final List<DCHumidity> hums;
	private final List<DCAirflow> airs;

	@SuppressWarnings("unchecked")
	public ClimateCropWrapper(IClimateCrop recipe) {
		rec = recipe;
		input = new ArrayList<ItemStack>();
		output = new ArrayList<ItemStack>();
		if (recipe instanceof Block && recipe.getGrownState() != null) {
			block = (Block) recipe;
			IBlockState grown = recipe.getGrownState();
			output.addAll(recipe.getCropItems(grown, 0));
			input.add(new ItemStack(block, 1, block.getMetaFromState(grown)));
			input.add(rec.getSeedItem(grown));
		} else {
			block = null;
		}

		temps = new ArrayList<DCHeatTier>();
		temps.addAll(recipe.getSuitableTemp(block.getDefaultState()));
		if (temps.isEmpty()) {
			temps.addAll(DCHeatTier.createList());
		}

		hums = new ArrayList<DCHumidity>();
		hums.addAll(recipe.getSuitableHum(block.getDefaultState()));
		if (hums.isEmpty()) {
			hums.addAll(DCHumidity.createList());
		}

		airs = new ArrayList<DCAirflow>();
		airs.addAll(recipe.getSuitableAir(block.getDefaultState()));
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
		ing.setInputs(ItemStack.class, input);
		ing.setOutputs(ItemStack.class, output);
		ing.setInputs(DCHeatTier.class, temps);
		ing.setInputs(DCHumidity.class, hums);
		ing.setInputs(DCAirflow.class, airs);
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		List<DCHeatTier> heats = rec.getSuitableTemp(block.getDefaultState());
		List<DCHumidity> hums = rec.getSuitableHum(block.getDefaultState());
		List<DCAirflow> airs = rec.getSuitableAir(block.getDefaultState());
		DCHeatTier minT = DCHeatTier.INFERNO;
		DCHumidity maxH = DCHumidity.DRY;
		DCAirflow maxA = DCAirflow.TIGHT;
		int baseY = 75;

		ResourceLocation res = new ResourceLocation("dcs_climate", "textures/gui/c_crops_gui_jei.png");
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
			maxA = DCAirflow.NORMAL;
		} else {
			for (DCAirflow a : airs) {
				mc.currentScreen.drawTexturedModalRect(38 + a.getID() * 21, baseY + 20, a.getID() * 21, 178, 21, 3);
				if (maxA.getID() < a.getID())
					maxA = a;
			}
		}

		String name = "PLANT BLOCK";
		String crop = "CROPS";
		String seed = "SEEDS";
		mc.fontRenderer.drawString(name, 26, 20, 0x0099FF, false);
		mc.fontRenderer.drawString(crop, 80, 14, 0x0099FF, false);
		mc.fontRenderer.drawString(seed, 80, 42, 0x0099FF, false);
	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		int baseY = 72;
		List<String> s = new ArrayList<String>();
		if (y > baseY && y < baseY + 8) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 6;
				s.add(DCHeatTier.getTypeByID(i).name() + " " + DCHeatTier.getTypeByID(i).getTemp());
			}
		}
		if (y > baseY + 10 && y < baseY + 18) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCHumidity.getTypeByID(i).name());
			}
		}
		if (y > baseY + 20 && y < baseY + 28) {
			if (x > 38 && x < 122) {
				int i = (x - 38) / 21;
				s.add(DCAirflow.getTypeByID(i).name());
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
