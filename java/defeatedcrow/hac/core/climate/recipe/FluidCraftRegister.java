package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IFluidRecipe;
import defeatedcrow.hac.api.recipe.IFluidRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidCraftRegister implements IFluidRecipeRegister {

	public FluidCraftRegister() {
		this.absList = new ArrayList<FluidCraftRecipe>();
		this.frostList = new ArrayList<FluidCraftRecipe>();
		this.coldList = new ArrayList<FluidCraftRecipe>();
		this.coolList = new ArrayList<FluidCraftRecipe>();
		this.normalList = new ArrayList<FluidCraftRecipe>();
		this.warmList = new ArrayList<FluidCraftRecipe>();
		this.hotList = new ArrayList<FluidCraftRecipe>();
		this.ovenList = new ArrayList<FluidCraftRecipe>();
		this.kilnList = new ArrayList<FluidCraftRecipe>();
		this.smeltList = new ArrayList<FluidCraftRecipe>();
		this.uhtList = new ArrayList<FluidCraftRecipe>();
		this.infList = new ArrayList<FluidCraftRecipe>();
	}

	public IFluidRecipeRegister instance() {
		return RecipeAPI.registerFluidRecipes;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<FluidCraftRecipe> absList;
	private static List<FluidCraftRecipe> frostList;
	private static List<FluidCraftRecipe> coldList;
	private static List<FluidCraftRecipe> coolList;
	private static List<FluidCraftRecipe> normalList;
	private static List<FluidCraftRecipe> warmList;
	private static List<FluidCraftRecipe> hotList;
	private static List<FluidCraftRecipe> ovenList;
	private static List<FluidCraftRecipe> kilnList;
	private static List<FluidCraftRecipe> smeltList;
	private static List<FluidCraftRecipe> uhtList;
	private static List<FluidCraftRecipe> infList;

	@Override
	public List<FluidCraftRecipe> getRecipeList(DCHeatTier tier) {
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
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid,
			DCHeatTier heat, DCHumidity hum, DCAirflow air, boolean needCooling, FluidStack inFluid, Object... input) {
		List<FluidCraftRecipe> list = getRecipeList(heat);
		boolean b1 = input == null && inFluid == null;
		boolean b2 = output == null && outFluid == null;
		if (!b1 && !b2) {
			list.add(new FluidCraftRecipe(output, secondary, outFluid, heat, hum, air, secondaryChance, needCooling,
					inFluid, input));
		}
	}

	@Override
	public void addRecipe(IFluidRecipe recipe, DCHeatTier heat) {
		List<FluidCraftRecipe> list = getRecipeList(heat);
		if (recipe instanceof FluidCraftRecipe)
			list.add((FluidCraftRecipe) recipe);
	}

	@Override
	public IFluidRecipe getRecipe(IClimate clm, List<ItemStack> items, FluidStack fluid) {
		List<FluidCraftRecipe> list = new ArrayList<FluidCraftRecipe>();
		list.addAll(getRecipeList(clm.getHeat()));
		/*
		 * 現在環境の1つ下の温度帯のレシピも条件にあてまはる
		 */
		if (clm.getHeat() == DCHeatTier.NORMAL) {
			list.addAll(getRecipeList(DCHeatTier.WARM));
			list.addAll(getRecipeList(DCHeatTier.COOL));
		} else {
			int i = clm.getHeat().getTier() < 0 ? 1 : -1;
			DCHeatTier next = clm.getHeat().addTier(i);
			list.addAll(getRecipeList(next));
		}
		IFluidRecipe ret = null;
		if (list.isEmpty()) {} else {
			// DCLogger.debugLog("### searching... ###");
			int c = 0;
			for (IFluidRecipe recipe : list) {
				if (recipe.matches(items, fluid) && recipe.matchClimate(clm)) {
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
	public IFluidRecipe getRecipe(int code, List<ItemStack> items, FluidStack fluid) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return getRecipe(clm, items, fluid);
	}

}
