package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.IReactorRecipe;
import defeatedcrow.hac.api.recipe.IReactorRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ReactorRecipeRegister implements IReactorRecipeRegister {

	public ReactorRecipeRegister() {}

	public IReactorRecipeRegister instance() {
		return RecipeAPI.registerReactorRecipes;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<ReactorRecipe> list = new ArrayList<>();

	@Override
	public List<ReactorRecipe> getRecipeList() {
		return list;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid1,
			FluidStack outFluid2, DCHeatTier heat, ItemStack catalyst, FluidStack inFluid1, FluidStack inFluid2,
			Object... input) {
		if (output == null) {
			output = ItemStack.EMPTY;
		}
		if (secondary == null) {
			secondary = ItemStack.EMPTY;
		}
		if (catalyst == null) {
			catalyst = ItemStack.EMPTY;
		}
		boolean b1 = input == null && inFluid1 == null && inFluid2 == null;
		boolean b2 = DCUtil.isEmpty(output) && outFluid1 == null && outFluid2 == null;
		boolean b3 = hasEmptyInput(input);
		if (!b1 && !b2 && !b3) {
			list.add(new ReactorRecipe(output, secondary, outFluid1, outFluid2, heat, secondaryChance, catalyst,
					inFluid1, inFluid2, input));
		}
	}

	@Override
	public void addRecipe(IReactorRecipe recipe, DCHeatTier heat) {
		if (recipe instanceof ReactorRecipe && !hasEmptyInput(recipe.getInput()))
			list.add((ReactorRecipe) recipe);
	}

	private boolean hasEmptyInput(Object... inputs) {
		if (inputs != null && inputs.length > 0) {
			for (Object in : inputs) {
				if (in instanceof String) {
					boolean ret = true;
					if (OreDictionary.doesOreNameExist((String) in)) {
						List l = OreDictionary.getOres((String) in);
						if (!l.isEmpty() && l.size() > 0) {
							ret = false;
						}
					}

					if (ret) {
						return true;
					}
				} else if (in == null) {
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public IReactorRecipe getRecipe(DCHeatTier tier, List<ItemStack> items, FluidStack fluid1, FluidStack fluid2) {
		IReactorRecipe ret = null;
		int c = 0;
		for (IReactorRecipe recipe : list) {
			if (recipe.matches(items, fluid1, fluid2) && recipe.matchHeatTier(tier)) {
				if (recipe.recipeCoincidence() >= c) {
					ret = recipe;
					c = recipe.recipeCoincidence();
				}
			}
		}

		if (ret != null) {
			return ret;
		}
		return null;
	}

	@Override
	public IReactorRecipe getRecipe(int id, List<ItemStack> items, FluidStack fluid1, FluidStack fluid2) {
		return getRecipe(DCHeatTier.getTypeByID(id), items, fluid1, fluid2);
	}

	@Override
	public void addRecipe(IReactorRecipe recipe) {
		this.addRecipe(recipe);
	}

}
