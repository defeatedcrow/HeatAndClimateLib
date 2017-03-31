package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.IClimateSmeltingRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class ClimateSmeltingRegister implements IClimateSmeltingRegister {

	public ClimateSmeltingRegister() {
		this.absList = new ArrayList<ClimateSmelting>();
		this.frostList = new ArrayList<ClimateSmelting>();
		this.coldList = new ArrayList<ClimateSmelting>();
		this.coolList = new ArrayList<ClimateSmelting>();
		this.normalList = new ArrayList<ClimateSmelting>();
		this.warmList = new ArrayList<ClimateSmelting>();
		this.hotList = new ArrayList<ClimateSmelting>();
		this.ovenList = new ArrayList<ClimateSmelting>();
		this.kilnList = new ArrayList<ClimateSmelting>();
		this.smeltList = new ArrayList<ClimateSmelting>();
		this.uhtList = new ArrayList<ClimateSmelting>();
		this.infList = new ArrayList<ClimateSmelting>();
	}

	public IClimateSmeltingRegister instance() {
		return RecipeAPI.registerSmelting;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<ClimateSmelting> absList;
	private static List<ClimateSmelting> frostList;
	private static List<ClimateSmelting> coldList;
	private static List<ClimateSmelting> coolList;
	private static List<ClimateSmelting> normalList;
	private static List<ClimateSmelting> warmList;
	private static List<ClimateSmelting> hotList;
	private static List<ClimateSmelting> ovenList;
	private static List<ClimateSmelting> kilnList;
	private static List<ClimateSmelting> smeltList;
	private static List<ClimateSmelting> uhtList;
	private static List<ClimateSmelting> infList;

	@Override
	public List<ClimateSmelting> getRecipeList(DCHeatTier tier) {
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
	public void addRecipe(ItemStack output, ItemStack secondary, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			float secondaryChance, boolean cooling, Object input) {
		if (input != null && !DCUtil.isEmpty(output) && heat != null) {
			List<ClimateSmelting> list = getRecipeList(heat);
			list.add(new ClimateSmelting(output, secondary, heat, hum, air, secondaryChance, cooling, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air, boolean needCooling,
			Object input) {
		if (input != null && !DCUtil.isEmpty(output) && heat != null) {
			List<ClimateSmelting> list = getRecipeList(heat);
			list.add(new ClimateSmelting(output, null, heat, hum, air, 1.0F, false, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, IClimate clm, float secondaryChance, Object input) {
		this.addRecipe(output, secondary, clm.getHeat(), clm.getHumidity(), clm.getAirflow(), secondaryChance, false,
				input);
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, Object input) {
		this.addRecipe(output, null, heat, null, null, 1.0F, false, input);
	}

	@Override
	public void addRecipe(IClimateSmelting recipe, DCHeatTier heat) {
		List<ClimateSmelting> list = getRecipeList(heat);
		if (recipe instanceof ClimateSmelting)
			list.add((ClimateSmelting) recipe);
	}

	@Override
	public IClimateSmelting getRecipe(IClimate clm, ItemStack item) {
		List<ClimateSmelting> list = getRecipeList(clm.getHeat());
		IClimateSmelting ret = null;
		if (list.isEmpty()) {} else {
			for (IClimateSmelting recipe : list) {
				if (recipe.matcheInput(item) && recipe.matchClimate(clm)) {
					ret = recipe;
				}
			}
		}
		/*
		 * Tier絶対値が1以上の場合、現在環境の1つ下の温度帯のレシピも条件にあてまはる
		 */
		if (ret == null) {
			if (clm.getHeat() == DCHeatTier.NORMAL) {
				List<ClimateSmelting> list2 = getRecipeList(DCHeatTier.WARM);
				if (list2.isEmpty()) {} else {
					for (IClimateSmelting recipe : list2) {
						if (recipe.matcheInput(item) && recipe.matchClimate(clm)) {
							ret = recipe;
						}
					}
				}
			} else {
				int i = clm.getHeat().getTier() < 0 ? 1 : -1;
				List<ClimateSmelting> list2 = getRecipeList(clm.getHeat().addTier(i));
				if (list2.isEmpty()) {} else {
					for (IClimateSmelting recipe : list2) {
						if (recipe.matcheInput(item) && recipe.matchClimate(clm)) {
							ret = recipe;
						}
					}
				}
			}
		}

		if (ret != null) {
			return ret;
		}
		return null;
	}

	@Override
	public IClimateSmelting getRecipe(int code, ItemStack item) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return getRecipe(clm, item);
	}

}
