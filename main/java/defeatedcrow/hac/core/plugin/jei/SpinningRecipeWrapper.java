package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.climate.recipe.SpinningRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class SpinningRecipeWrapper implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final SpinningRecipe rec;

	@SuppressWarnings("unchecked")
	public SpinningRecipeWrapper(SpinningRecipe recipe) {
		rec = recipe;
		input = recipe.getProcessedInput();
		output = new ArrayList<ItemStack>();
		output.add(recipe.getOutput());
	}

	@Override
	public void getIngredients(IIngredients ing) {
		ing.setInputs(VanillaTypes.ITEM, input);
		ing.setOutputs(VanillaTypes.ITEM, output);
	}

	public List<ItemStack> getInputs() {
		return input;
	}

	public List<ItemStack> getOutputs() {
		return output;
	}

	@Override
	public void drawInfo(Minecraft mc, int wid, int hei, int mouseX, int mouseY) {}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}

}
