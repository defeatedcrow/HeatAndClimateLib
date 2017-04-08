package defeatedcrow.hac.core.plugin.tweaker;

import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.dcs_climate.ClimateSmelting")
public class CTClimateSmelting {

	@ZenMethod
	public void addRecipe(IItemStack output, int heatTier, int humTier, int airTier, IItemStack input) {
		if (input == null || output == null) {
			MineTweakerAPI.getLogger().logError("Climate Smelting: Detected invalid recipe including null item.");
			return;
		}
		DCHeatTier heat = DCHeatTier.getHeatEnum(heatTier);
		DCHumidity hum = DCHumidity.getTypeByID(humTier);
		DCAirflow air = DCAirflow.getTypeByID(airTier);
		IClimate clm = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		ItemStack out = CTHelper.getStack(output);
		ItemStack in = CTHelper.getStack(input);
		if (RecipeAPI.registerSmelting.getRecipe(clm, in) != null) {
			MineTweakerAPI.getLogger().logError("Climate Smelting: " + in.toString() + " already has a recipe.");
			return;
		}
		MineTweakerAPI.apply(new ClimateSmeltingWrapper(out, clm, in));
	}

	private static class ClimateSmeltingWrapper implements IUndoableAction {
		private boolean applied;
		private final IClimateSmelting recipe;
		private final IClimate climate;

		public ClimateSmeltingWrapper(ItemStack out, IClimate clm, Object in) {
			recipe = new ClimateSmelting(out, null, clm.getHeat(), clm.getHumidity(), clm.getAirflow(), 0, false, in);
			climate = clm;
		}

		@Override
		public void apply() {
			if (!applied) {
				RecipeAPI.registerSmelting.addRecipe(recipe, climate.getHeat());
				applied = true;
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			if (applied) {
				List<? extends IClimateSmelting> list = RecipeAPI.registerSmelting.getRecipeList(climate.getHeat());
				list.remove(recipe);
				applied = false;
			}
		}

		@Override
		public String describe() {
			return "Adding climate smelting recipe: " + recipe.getOutput().toString();
		}

		@Override
		public String describeUndo() {
			return "Removing climate smelting recipe: " + recipe.getOutput().toString();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	@ZenMethod
	public void removeRecipe(IItemStack input, int heatTier, int humTier, int airTier) {
		if (input == null) {
			MineTweakerAPI.getLogger().logError("Climate Smelting: Detected invalid recipe including null item.");
			return;
		}
		DCHeatTier heat = DCHeatTier.getHeatEnum(heatTier);
		DCHumidity hum = DCHumidity.getTypeByID(humTier);
		DCAirflow air = DCAirflow.getTypeByID(airTier);
		IClimate clm = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		ItemStack in = CTHelper.getStack(input);
		if (RecipeAPI.registerSmelting.getRecipe(clm, in) == null) {
			MineTweakerAPI.getLogger().logError("Climate Smelting: " + in.toString() + " has no recipe.");
			return;
		}
	}

	private static class ClimateSmeltingRemover implements IUndoableAction {
		private boolean applied;
		private IClimateSmelting recipe = null;
		private final IClimate climate;
		private final ItemStack input;

		public ClimateSmeltingRemover(ItemStack in, IClimate clm) {
			input = in;
			climate = clm;
		}

		@Override
		public void apply() {
			if (!applied) {
				recipe = RecipeAPI.registerSmelting.getRecipe(climate, input);
				if (recipe == null) {
					MineTweakerAPI.getLogger().logError("Climate Smelting: " + input.toString() + " has no recipe.");
					return;
				}
				RecipeAPI.registerSmelting.getRecipeList(climate.getHeat()).remove(recipe);
				applied = true;
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			if (applied && recipe != null) {
				RecipeAPI.registerSmelting.addRecipe(recipe, climate.getHeat());
				applied = false;
			}
		}

		@Override
		public String describe() {
			return "Removing climate smelting recipe: " + recipe.getOutput().toString();
		}

		@Override
		public String describeUndo() {
			return "Adding climate smelting recipe: " + recipe.getOutput().toString();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

}
