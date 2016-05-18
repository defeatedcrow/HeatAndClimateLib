package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class VanillaRecipeRegister {

	public static void load() {
		// ABS
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.packed_ice, 1, 0), DCHeatTier.ABSOLUTE, null, DCAirflow.FLOW, false,
				new ItemStack(Blocks.ice, 1, 0));

		// COLD

		// NORMAL
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.mossy_cobblestone, 1, 0), DCHeatTier.NORMAL, DCHumidity.WET, null, false,
				new ItemStack(Blocks.cobblestone, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stonebrick, 1, 1), DCHeatTier.NORMAL, DCHumidity.WET, null, false,
				new ItemStack(Blocks.stonebrick, 1, 0));

		// HOT

		// OVEN

		// KILN
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 2), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 1));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 4), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 3));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 6), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 5));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 0), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.cobblestone, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.hardened_clay, 1, 0), DCHeatTier.KILN, DCHumidity.DRY, null, false,
				new ItemStack(Blocks.clay, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.glass, 1, 0), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.sand, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stonebrick, 1, 2), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stonebrick, 1, 0));

		// SMELT

	}

}
