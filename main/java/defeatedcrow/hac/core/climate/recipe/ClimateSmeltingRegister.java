package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.IClimateSmeltingRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ClimateSmeltingRegister implements IClimateSmeltingRegister {
	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private List<IClimateSmelting> recipes = new ArrayList<>();

	public ClimateSmeltingRegister() {}

	public IClimateSmeltingRegister instance() {
		return RecipeAPI.registerSmelting;
	}

	@Override
	public List<IClimateSmelting> getRecipeList() {
		return recipes;
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			float secondaryChance, boolean cooling, Object input) {
		if (input != null && !DCUtil.isEmpty(output) && heat != null) {
			if (input instanceof String && OreDictionary.getOres((String) input).isEmpty()) {
				DCLogger.infoLog("ClimateSmelting Accepted empty input: " + input);
				return;
			}
			if (input instanceof List && ((List) input).isEmpty()) {
				DCLogger.infoLog("ClimateSmelting Accepted empty input list");
				return;
			}
			if (secondary == null) {
				secondary = ItemStack.EMPTY;
			}
			boolean drop = false;
			getRecipeList()
					.add(new ClimateSmelting(output, secondary, heat, hum, air, secondaryChance, cooling, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air, boolean needCooling,
			Object input) {
		if (input != null && !DCUtil.isEmpty(output) && heat != null) {
			getRecipeList().add(new ClimateSmelting(output, ItemStack.EMPTY, heat, hum, air, 1.0F, false, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, IClimate clm, float secondaryChance, Object input) {
		this.addRecipe(output, secondary, clm.getHeat(), clm.getHumidity(), clm.getAirflow(), secondaryChance, false,
				input);
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, Object input) {
		this.addRecipe(output, ItemStack.EMPTY, heat, null, null, 1.0F, false, input);
	}

	@Override
	public void addRecipe(IClimateSmelting recipe) {
		if (recipe instanceof ClimateSmelting)
			getRecipeList().add(recipe);
	}

	@Override
	public IClimateSmelting getRecipe(Supplier<IClimate> clm, ItemStack item) {
		for (IClimateSmelting recipe : recipes) {
			if (recipe.matcheInput(item) && recipe.matchClimate(clm.get())) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public IClimateSmelting getRecipe(IClimate clm, ItemStack item) {
		return getRecipe(Suppliers.ofInstance(clm), item);
	}

	@Override
	public IClimateSmelting getRecipe(int code, ItemStack item) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return getRecipe(clm, item);
	}

	@Override
	public void addRecipe(IClimateSmelting recipe, DCHeatTier heat) {
		this.addRecipe(recipe);
	}
}
