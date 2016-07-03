package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class VanillaRecipeRegister {

	public static void load() {
		// FROST
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.PACKED_ICE, 1, 0), DCHeatTier.FROSTBITE, null,
				DCAirflow.FLOW, false, new ItemStack(Blocks.ICE, 1, 0));

		// COLD

		// NORMAL
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1, 0), DCHeatTier.NORMAL,
				DCHumidity.WET, null, false, new ItemStack(Blocks.COBBLESTONE, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONEBRICK, 1, 1), DCHeatTier.NORMAL, DCHumidity.WET,
				null, false, new ItemStack(Blocks.STONEBRICK, 1, 0));

		// HOT

		// OVEN

		// KILN
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONE, 1, 2), DCHeatTier.KILN, null, null, false,
				new ItemStack(Blocks.STONE, 1, 1));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONE, 1, 4), DCHeatTier.KILN, null, null, false,
				new ItemStack(Blocks.STONE, 1, 3));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONE, 1, 6), DCHeatTier.KILN, null, null, false,
				new ItemStack(Blocks.STONE, 1, 5));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONE, 1, 0), DCHeatTier.KILN, null, null, false,
				new ItemStack(Blocks.COBBLESTONE, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.HARDENED_CLAY, 1, 0), DCHeatTier.KILN,
				DCHumidity.DRY, null, false, new ItemStack(Blocks.CLAY, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.GLASS, 1, 0), DCHeatTier.KILN, null, null, false,
				new ItemStack(Blocks.SAND, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONEBRICK, 1, 2), DCHeatTier.KILN, null, null,
				false, new ItemStack(Blocks.STONEBRICK, 1, 0));

		// SMELT

	}

}
