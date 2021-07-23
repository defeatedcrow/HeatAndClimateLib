package defeatedcrow.hac.core.climate.recipe;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.recipe.ICrusherRecipe;
import defeatedcrow.hac.api.recipe.ICrusherRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class CrusherRecipeRegister implements ICrusherRecipeRegister {

	public CrusherRecipeRegister() {
		this.list = new ArrayList<ICrusherRecipe>();
	}

	public ICrusherRecipeRegister instance() {
		return RecipeAPI.registerCrushers;
	}

	private static List<ICrusherRecipe> list;

	@Override
	public List<ICrusherRecipe> getRecipeList() {
		return this.list;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack tertialy,
			float tertialyChance, FluidStack outputFluid, ItemStack catalyst, Object input) {
		if (output == null) {
			output = ItemStack.EMPTY;
		}
		if (secondary == null) {
			secondary = ItemStack.EMPTY;
		}
		if (tertialy == null) {
			tertialy = ItemStack.EMPTY;
		}
		if (catalyst == null || DCUtil.isEmpty(catalyst)) {
			return;
		}
		if (input != null && (!DCUtil.isEmpty(output) || outputFluid != null)) {
			if (input instanceof String && OreDictionary.getOres((String) input).isEmpty()) {
				DCLogger.infoLog("CrusherRecipe Accepted empty input: " + input);
				return;
			}
			if (input instanceof List && ((List) input).isEmpty()) {
				DCLogger.infoLog("CrusherRecipe Accepted empty input list");
				return;
			}
			list.add(new CrusherRecipe(output, 1F, secondary, secondaryChance, tertialy, tertialyChance, outputFluid,
					catalyst, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack tertialy,
			float tertialyChance, ItemStack catalyst, Object input) {
		addRecipe(output, secondary, secondaryChance, tertialy, tertialyChance, null, catalyst, input);
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outputFluid,
			ItemStack catalyst, Object input) {
		addRecipe(output, secondary, secondaryChance, ItemStack.EMPTY, 0.0F, outputFluid, catalyst, input);
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack catalyst,
			Object input) {
		addRecipe(output, secondary, secondaryChance, ItemStack.EMPTY, 0.0F, null, catalyst, input);
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack catalyst, Object input) {
		addRecipe(output, ItemStack.EMPTY, 0.0F, ItemStack.EMPTY, 0.0F, null, catalyst, input);
	}

	@Override
	public void addRecipe(ICrusherRecipe recipe) {
		Class clazz = recipe.getClass();
		if (!Modifier.isAbstract(clazz.getModifiers()))
			list.add(recipe);
	}

	@Override
	public ICrusherRecipe getRecipe(ItemStack item, ItemStack catalyst) {
		ICrusherRecipe ret = null;
		if (!list.isEmpty()) {
			for (ICrusherRecipe recipe : list) {
				if (recipe.matches(item) && recipe.matchCatalyst(catalyst)) {
					// DCLogger.debugLog("get recipe");
					ret = recipe;
				}
			}
		}
		return ret;
	}

	@Override
	public ICrusherRecipe getRecipe(ItemStack item) {
		return getRecipe(item, ItemStack.EMPTY);
	}

	@Override
	public boolean removeRecipe(ItemStack input, ItemStack catalyst) {
		ICrusherRecipe recipe = getRecipe(input, catalyst);
		if (recipe != null && list.remove(recipe)) {
			return true;
		}
		return false;
	}

}
