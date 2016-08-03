package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class VanillaRecipeRegister {

	public static void load() {
		/* Smelting */
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

		// RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.GLASS, 1, 0), DCHeatTier.KILN,
		// null, null, false,
		// new ItemStack(Blocks.SAND, 1, 0));

		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.STONEBRICK, 1, 2), DCHeatTier.KILN, null, null,
				false, new ItemStack(Blocks.STONEBRICK, 1, 0));

		// SMELT

		/* Mill */

		RecipeAPI.registerMills.addRecipe(new ItemStack(Blocks.SAND), new ItemStack(Items.FLINT), new ItemStack(
				Blocks.GRAVEL));

		/* Fluid */

		// Add Alt Recipe
		addAltRecipes();
	}

	static void addAltRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.ARROW, 8, 0), new Object[] {
				"X",
				"Y",
				"Z",
				'X',
				"gemChalcedony",
				'Y',
				new ItemStack(Items.STICK),
				'Z',
				new ItemStack(Items.FEATHER) }));

		// コンパス
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.COMPASS, 1, 0), new Object[] {
				" X ",
				"XYX",
				" X ",
				'X',
				"ingotIron",
				'Y',
				"dustMagnetite" }));

		// 火打ち石のアナザー
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.FLINT_AND_STEEL, 1, 0), new Object[] {
				new ItemStack(Items.IRON_INGOT, 1, 0),
				"gemChalcedony" }));

		// 火薬
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 4, 0), new Object[] {
				"dustGraphite",
				"dustGraphite",
				"dustGraphite",
				"gemNiter",
				"gemNiter",
				"gemSulfur" }));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 4, 0), new Object[] {
				"dustGraphite",
				"dustGraphite",
				"dustGraphite",
				"dustNiter",
				"dustNiter",
				"dustSulfur" }));

		// 代替系
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.CAKE, 1, 0), new Object[] {
				"XXX",
				"YZY",
				"WWW",
				'X',
				"bucketMilk",
				'Y',
				"dustSugar",
				'Z',
				"egg",
				'W',
				"foodFlour" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.COOKIE, 8, 0), new Object[] {
				"YZY",
				'Y',
				"foodFlour",
				'Z',
				"cropCocoa" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BREAD, 1, 0), new Object[] {
				"YYY",
				'Y',
				"foodFlour" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BED, 1, 0), new Object[] {
				"XXX",
				"YYY",
				'X',
				"itemCloth",
				'Y',
				"plankWood" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.PAINTING, 1, 0), new Object[] {
				"XXX",
				"XYX",
				"XXX",
				'X',
				"stickWood",
				'Y',
				"itemCloth" }));
	}

}
