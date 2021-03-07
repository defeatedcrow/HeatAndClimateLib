package defeatedcrow.hac.core.climate.recipe;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipeRegister;
import defeatedcrow.hac.api.recipe.ISpinningRecipe;
import defeatedcrow.hac.api.recipe.ISpinningRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SpinningRecipeRegister implements ISpinningRecipeRegister {

	public SpinningRecipeRegister() {
		this.list = new ArrayList<ISpinningRecipe>();
	}

	public IMillRecipeRegister instance() {
		return RecipeAPI.registerMills;
	}

	private static List<ISpinningRecipe> list;

	@Override
	public List<ISpinningRecipe> getRecipeList() {
		return this.list;
	}

	@Override
	public void addRecipe(ItemStack output, int count, Object input) {
		if (input != null && !DCUtil.isEmpty(output)) {
			if (input instanceof String && OreDictionary.getOres((String) input).isEmpty()) {
				DCLogger.infoLog("SpinningRecipe Accepted empty input: " + input);
				return;
			}
			if (input instanceof List && ((List) input).isEmpty()) {
				DCLogger.infoLog("SpinningRecipe Accepted empty input list");
				return;
			}
			list.add(new SpinningRecipe(output, count, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, Object input) {
		addRecipe(output, 1, input);
	}

	@Override
	public void addRecipe(ISpinningRecipe recipe) {
		Class clazz = recipe.getClass();
		if (!Modifier.isAbstract(clazz.getModifiers()))
			list.add(recipe);
	}

	@Override
	public ISpinningRecipe getRecipe(ItemStack item) {
		ISpinningRecipe ret = null;
		if (!list.isEmpty()) {
			for (ISpinningRecipe recipe : list) {
				if (recipe.matchInput(item)) {
					ret = recipe;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean removeRecipe(ItemStack input) {
		ISpinningRecipe recipe = getRecipe(input);
		if (recipe != null && list.remove(recipe)) {
			return true;
		}
		return false;
	}

}
