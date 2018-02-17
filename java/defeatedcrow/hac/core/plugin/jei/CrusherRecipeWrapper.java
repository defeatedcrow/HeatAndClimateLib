package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.climate.recipe.CrusherRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CrusherRecipeWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final List<ItemStack> catalyst;
	private final List<FluidStack> outF;
	private final CrusherRecipe rec;

	@SuppressWarnings("unchecked")
	public CrusherRecipeWrapper(CrusherRecipe recipe) {
		rec = recipe;

		input = recipe.getProcessedInput();

		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}
		if (!DCUtil.isEmpty(recipe.getTertialy())) {
			output.add(recipe.getTertialy());
		}

		catalyst = new ArrayList<>();
		catalyst.add(recipe.getCatalyst());

		outF = new ArrayList<>();
		if (recipe.getOutputFluid() != null) {
			outF.add(recipe.getOutputFluid());
		}
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(ItemStack.class, input);
		ing.setOutputs(ItemStack.class, output);
		ing.setOutputs(FluidStack.class, outF);
	}

	public List<ItemStack> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
	}

	public List<ItemStack> getCatalyst() {
		return catalyst;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		int chance1 = DCUtil.isEmpty(rec.getSecondary()) ? 0 : (int) (rec.getSecondaryChance() * 100);
		if (chance1 > 0) {
			mc.fontRenderer.drawString(chance1 + "%", 102, 26, 0x0099FF, false);
		}
		int chance2 = DCUtil.isEmpty(rec.getTertialy()) ? 0 : (int) (rec.getTertialyChance() * 100);
		if (chance2 > 0) {
			mc.fontRenderer.drawString(chance2 + "%", 102, 44, 0x0099FF, false);
		}
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
