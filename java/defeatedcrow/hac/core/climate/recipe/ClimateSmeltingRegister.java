package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.IClimateSmeltingRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class ClimateSmeltingRegister implements IClimateSmeltingRegister {

	public ClimateSmeltingRegister() {
		this.absList = new ArrayList<ClimateSmelting>();
		this.coldList = new ArrayList<ClimateSmelting>();
		this.normalList = new ArrayList<ClimateSmelting>();
		this.hotList = new ArrayList<ClimateSmelting>();
		this.ovenList = new ArrayList<ClimateSmelting>();
		this.kilnList = new ArrayList<ClimateSmelting>();
		this.smeltList = new ArrayList<ClimateSmelting>();
		this.uhtList = new ArrayList<ClimateSmelting>();
	}

	public IClimateSmeltingRegister instance() {
		return RecipeAPI.registerSmelting;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<ClimateSmelting> absList;
	private static List<ClimateSmelting> coldList;
	private static List<ClimateSmelting> normalList;
	private static List<ClimateSmelting> hotList;
	private static List<ClimateSmelting> ovenList;
	private static List<ClimateSmelting> kilnList;
	private static List<ClimateSmelting> smeltList;
	private static List<ClimateSmelting> uhtList;

	@Override
	public List<? extends ClimateSmelting> getRecipeList(DCHeatTier tier) {
		switch (tier) {
		case ABSOLUTE:
			return absList;
		case COLD:
			return coldList;
		case NORMAL:
			return normalList;
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
		default:
			return ovenList;
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			float secondaryChance, boolean cooling, Object input) {
		List<IClimateSmelting> list = (List<IClimateSmelting>) getRecipeList(heat);
		if (input != null && output != null && heat != null) {
			list.add(new ClimateSmelting(output, secondary, heat, hum, air, secondaryChance, cooling, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, int code, float secondaryChance, Object input) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		this.addRecipe(output, secondary, clm.getHeat(), clm.getHumidity(), clm.getAirflow(), secondaryChance, false,
				input);
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, Object... input) {
		this.addRecipe(output, null, heat, null, null, 0.0F, false, input);
	}

	@Override
	public void addRecipe(IClimateSmelting recipe, DCHeatTier heat) {
		List<IClimateSmelting> list = (List<IClimateSmelting>) getRecipeList(heat);
		list.add(recipe);
	}

	@Override
	public IClimateSmelting getRecipe(IClimate clm, ItemStack item) {
		List<IClimateSmelting> list = (List<IClimateSmelting>) getRecipeList(clm.getHeat());
		if (list.isEmpty()) {
			return null;
		} else {
			for (IClimateSmelting recipe : list) {
				if (recipe.matcheInput(item) && recipe.matchClimate(clm)) {
					return recipe;
				}
			}
			return null;
		}
	}

	@Override
	public IClimateSmelting getRecipe(int code, ItemStack item) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		List<IClimateSmelting> list = (List<IClimateSmelting>) getRecipeList(clm.getHeat());
		if (list.isEmpty()) {
			return null;
		} else {
			for (IClimateSmelting recipe : list) {
				if (recipe.matcheInput(item) && recipe.matchClimate(clm)) {
					return recipe;
				}
			}
			return null;
		}
	}

}
