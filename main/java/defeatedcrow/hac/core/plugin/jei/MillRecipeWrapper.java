package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.climate.recipe.MillRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class MillRecipeWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final MillRecipe rec;
	public final float chance;

	@SuppressWarnings("unchecked")
	public MillRecipeWrapper(MillRecipe recipe) {
		rec = recipe;
		input = recipe.getProcessedInput();
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}
		chance = recipe.getSecondaryChance();
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(ItemStack.class, input);
		ing.setOutputs(ItemStack.class, output);
	}

	public List<ItemStack> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		int chance = DCUtil.isEmpty(rec.getSecondary()) ? 0 : (int) (rec.getSecondaryChance() * 100);
		if (chance > 0) {
			mc.fontRenderer.drawString(chance + "%", 118, 2, 0x0099FF, false);
		}
	}

	@Override
	public List<String> getTooltipStrings(int x, int y) {
		List<String> s = new ArrayList<String>();

		if (x > 118 && x < 132) {
			if (y > 10 && y < 28) {
				int i = (int) (rec.getSecondaryChance() * 100);
				if (DCUtil.isEmpty(rec.getSecondary()) || i == 0) {
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
