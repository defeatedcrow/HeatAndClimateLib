package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.recipe.IMillRecipe;
import defeatedcrow.hac.api.recipe.IMillRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class MillRecipeRegister implements IMillRecipeRegister {

	public MillRecipeRegister() {
		this.list = new ArrayList<MillRecipe>();
	}

	public IMillRecipeRegister instance() {
		return RecipeAPI.registerMills;
	}

	private static List<MillRecipe> list;

	@Override
	public List<? extends IMillRecipe> getRecipeList() {
		return this.list;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, Object input) {
		if (input != null && output != null) {
			list.add(new MillRecipe(output, secondary, secondaryChance, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, Object input) {
		addRecipe(output, secondary, 1.0F, input);
	}

	@Override
	public void addRecipe(ItemStack output, Object input) {
		addRecipe(output, null, 1.0F, input);
	}

	@Override
	public void addRecipe(IMillRecipe recipe) {
		if (recipe instanceof MillRecipe) {
			list.add((MillRecipe) recipe);
		}
	}

	@Override
	public IMillRecipe getRecipe(ItemStack item) {
		IMillRecipe ret = null;
		if (list.isEmpty()) {
		} else {
			for (IMillRecipe recipe : list) {
				if (recipe.matchInput(item)) {
					ret = recipe;
				}
			}
		}
		return ret;
	}

}
