package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.core.climate.recipe.CrusherRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CrusherRecipeWrapper implements IRecipeWrapper {

	private final List<List<ItemStack>> input;
	private final List<ItemStack> output;
	private final List<ItemStack> catalyst;
	private final List<FluidStack> outF;
	private final CrusherRecipe rec;

	@SuppressWarnings("unchecked")
	public CrusherRecipeWrapper(CrusherRecipe recipe) {
		rec = recipe;

		input = Lists.newArrayList();
		input.add(recipe.getProcessedInput());

		output = new ArrayList<>();
		output.add(recipe.getOutput());
		if (!DCUtil.isEmpty(recipe.getSecondary())) {
			output.add(recipe.getSecondary());
		}
		if (!DCUtil.isEmpty(recipe.getTertialy())) {
			output.add(recipe.getTertialy());
		}

		catalyst = new ArrayList<>();
		catalyst.add(recipe.getCatalyst());
		input.add(catalyst);

		outF = new ArrayList<>();
		if (recipe.getOutputFluid() != null) {
			outF.add(recipe.getOutputFluid());
		}
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputLists(ItemStack.class, input);
		ing.setOutputs(ItemStack.class, output);
		ing.setOutputs(FluidStack.class, outF);
	}

	@Override
	public List<ItemStack> getInputs() {
		return input.get(0);
	}

	@Override
	public List<ItemStack> getOutputs() {
		return output;
	}

	public List<ItemStack> getCatalyst() {
		return catalyst;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return outF;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {
		int chance0 = DCUtil.isEmpty(rec.getOutput()) ? 0 : (int) (rec.getChance() * 100);
		if (chance0 > 0) {
			mc.fontRendererObj.drawString(chance0 + "%", 102, 8, 0x0099FF, false);
		}
		int chance1 = DCUtil.isEmpty(rec.getSecondary()) ? 0 : (int) (rec.getSecondaryChance() * 100);
		if (chance1 > 0) {
			mc.fontRendererObj.drawString(chance1 + "%", 102, 26, 0x0099FF, false);
		}
		int chance2 = DCUtil.isEmpty(rec.getTertialy()) ? 0 : (int) (rec.getTertialyChance() * 100);
		if (chance2 > 0) {
			mc.fontRendererObj.drawString(chance2 + "%", 102, 44, 0x0099FF, false);
		}
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
