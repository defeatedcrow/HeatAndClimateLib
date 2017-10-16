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

public class ReactorRecipeRegister implements IReactorRecipeRegister {

	public ReactorRecipeRegister() {
		this.absList = new ArrayList<ReactorRecipe>();
		this.frostList = new ArrayList<ReactorRecipe>();
		this.coldList = new ArrayList<ReactorRecipe>();
		this.coolList = new ArrayList<ReactorRecipe>();
		this.normalList = new ArrayList<ReactorRecipe>();
		this.warmList = new ArrayList<ReactorRecipe>();
		this.hotList = new ArrayList<ReactorRecipe>();
		this.ovenList = new ArrayList<ReactorRecipe>();
		this.kilnList = new ArrayList<ReactorRecipe>();
		this.smeltList = new ArrayList<ReactorRecipe>();
		this.uhtList = new ArrayList<ReactorRecipe>();
		this.infList = new ArrayList<ReactorRecipe>();
	}

	public IReactorRecipeRegister instance() {
		return RecipeAPI.registerReactorRecipes;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<ReactorRecipe> absList;
	private static List<ReactorRecipe> frostList;
	private static List<ReactorRecipe> coldList;
	private static List<ReactorRecipe> coolList;
	private static List<ReactorRecipe> normalList;
	private static List<ReactorRecipe> warmList;
	private static List<ReactorRecipe> hotList;
	private static List<ReactorRecipe> ovenList;
	private static List<ReactorRecipe> kilnList;
	private static List<ReactorRecipe> smeltList;
	private static List<ReactorRecipe> uhtList;
	private static List<ReactorRecipe> infList;

	@Override
	public List<ReactorRecipe> getRecipeList(DCHeatTier tier) {
		switch (tier) {
		case ABSOLUTE:
			return absList;
		case FROSTBITE:
			return frostList;
		case COLD:
			return coldList;
		case COOL:
			return coolList;
		case NORMAL:
			return normalList;
		case WARM:
			return warmList;
		case HOT:
			return hotList;
		case OVEN:
			return ovenList;
		case KILN:
			return kilnList;
		case SMELTING:
			return smeltList;
		case UHT:
			return uhtList;
		case INFERNO:
			return infList;
		default:
			return ovenList;
		}
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
		List<ReactorRecipe> list = getRecipeList(heat);
		boolean b1 = input == null && inFluid1 == null && inFluid2 == null;
		boolean b2 = DCUtil.isEmpty(output) && outFluid1 == null && outFluid2 == null;
		if (!b1 && !b2) {
			list.add(new ReactorRecipe(output, secondary, outFluid1, outFluid2, heat, secondaryChance, catalyst,
					inFluid1, inFluid2, input));
		}
	}

	@Override
	public void addRecipe(IReactorRecipe recipe, DCHeatTier heat) {
		List<ReactorRecipe> list = getRecipeList(heat);
		if (recipe instanceof ReactorRecipe)
			list.add((ReactorRecipe) recipe);
	}

	@Override
	public IReactorRecipe getRecipe(DCHeatTier tier, List<ItemStack> items, FluidStack fluid1, FluidStack fluid2) {
		List<ReactorRecipe> list = new ArrayList<ReactorRecipe>();
		list.addAll(getRecipeList(tier));
		/*
		 * 現在環境の1つ下の温度帯のレシピも条件にあてまはる
		 */
		if (tier == DCHeatTier.NORMAL) {
			list.addAll(getRecipeList(DCHeatTier.WARM));
			list.addAll(getRecipeList(DCHeatTier.COOL));
		} else {
			int i = tier.getTier() < 0 ? 1 : -1;
			DCHeatTier next = tier.addTier(i);
			list.addAll(getRecipeList(next));
		}
		IReactorRecipe ret = null;
		if (list.isEmpty()) {} else {
			// DCLogger.debugLog("### searching... ###");
			int c = 0;
			for (IReactorRecipe recipe : list) {
				if (recipe.matches(items, fluid1, fluid2) && recipe.matchHeatTier(tier)) {
					if (recipe.recipeCoincidence() >= c) {
						ret = recipe;
						c = recipe.recipeCoincidence();
					}
				}
			}
		}

		if (ret != null) {
			// DCLogger.debugLog("### fluid recipe found! ###");
			return ret;
		}
		return null;
	}

	@Override
	public IReactorRecipe getRecipe(int id, List<ItemStack> items, FluidStack fluid1, FluidStack fluid2) {
		return getRecipe(DCHeatTier.getTypeByID(id), items, fluid1, fluid2);
	}

}
