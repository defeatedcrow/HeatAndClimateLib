package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class VanillaRecipeRegister {

	public static void load() {
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 2), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 1));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 4), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 3));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 6), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.stone, 1, 5));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.stone, 1, 0), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.cobblestone, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.hardened_clay, 1, 0), DCHeatTier.KILN, null, null, false, new ItemStack(
				Blocks.clay, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.mossy_cobblestone, 1, 0), DCHeatTier.HOT, DCHumidity.WET, null, false,
				new ItemStack(Blocks.cobblestone, 1, 0));
	}

}
