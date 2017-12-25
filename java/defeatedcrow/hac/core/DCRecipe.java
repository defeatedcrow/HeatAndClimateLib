package defeatedcrow.hac.core;

import javax.annotation.Nonnull;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import defeatedcrow.hac.core.recipe.ShapedNBTRecipe;
import defeatedcrow.hac.core.recipe.ShapelessNBTRecipe;
import defeatedcrow.hac.core.util.RecipeResources;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class DCRecipe {

	public static void load() {
		/* Smelting */
		// ABS
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(Blocks.PACKED_ICE, 1, 0), DCHeatTier.ABSOLUTE, null,
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

		// SMELT

		/* Mill */

		/* Fluid */

		// Add Alt Recipe
		addAltRecipes();

		if (CoreConfigDC.harderVanilla) {
			loadHarderRecipes();
		}
	}

	static void addAltRecipes() {
		// コンパス

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.COMPASS, 1, 0), new Object[] {
				" X ", "XYX", " X ", 'X', "ingotIron", 'Y', "dustMagnetite"
		});

		// 火打ち石のアナザー
		addShapelessRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.FLINT_AND_STEEL, 1, 0),
				new Object[] {
						new ItemStack(Items.IRON_INGOT, 1, 0), "gemChalcedony"
				});

		// 火薬
		addShapelessRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.GUNPOWDER, 4, 0), new Object[] {
				"dustGraphite", "dustGraphite", "dustGraphite", "gemNiter", "gemNiter", "gemSulfur"
		});

		addShapelessRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.GUNPOWDER, 4, 0), new Object[] {
				"dustGraphite", "dustGraphite", "dustGraphite", "dustNiter", "dustNiter", "dustSulfur"
		});

		// 代替系
		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.CAKE, 1, 0), new Object[] {
				"XXX", "YZY", "WWW", 'X', "bucketMilk", 'Y', "dustSugar", 'Z', "egg", 'W', "foodFlour"
		});

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.COOKIE, 8, 0), new Object[] {
				"YZY", 'Y', "foodFlour", 'Z', "cropCocoa"
		});

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.BREAD, 1, 0), new Object[] {
				"YYY", 'Y', "foodFlour"
		});

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.BED, 1, 0), new Object[] {
				"XXX", "YYY", 'X', "itemCloth", 'Y', "plankWood"
		});

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.PAINTING, 1, 0), new Object[] {
				"XXX", "XYX", "XXX", 'X', "stickWood", 'Y', "itemCloth"
		});

		addShapedRecipe(RecipeResources.CORE.getRecipeName(), new ItemStack(Items.SADDLE, 1, 0), new Object[] {
				"X", "Y", "Z", 'X', "itemLeather", 'Y', "itemCloth", 'Z', "ingotIron"
		});

	}

	static void loadHarderRecipes() {
		// test recipes

		// 紙魚抹殺
		ClimateSmelting silverfish5 = new ClimateSmelting(new ItemStack(Blocks.STONEBRICK, 1, 2), null, DCHeatTier.KILN,
				null, null, 0, false, new ItemStack(Blocks.MONSTER_EGG, 1, 4));
		RecipeAPI.registerSmelting.addRecipe(silverfish5, DCHeatTier.KILN);

		ClimateSmelting silverfish4 = new ClimateSmelting(new ItemStack(Blocks.STONEBRICK, 1, 1), null, DCHeatTier.KILN,
				null, null, 0, false, new ItemStack(Blocks.MONSTER_EGG, 1, 3));
		RecipeAPI.registerSmelting.addRecipe(silverfish4, DCHeatTier.KILN);

		ClimateSmelting silverfish3 = new ClimateSmelting(new ItemStack(Blocks.STONEBRICK, 1, 0), null, DCHeatTier.KILN,
				null, null, 0, false, new ItemStack(Blocks.MONSTER_EGG, 1, 2));
		RecipeAPI.registerSmelting.addRecipe(silverfish3, DCHeatTier.KILN);

		ClimateSmelting silverfish2 = new ClimateSmelting(new ItemStack(Blocks.COBBLESTONE, 1, 0), null,
				DCHeatTier.KILN, null, null, 0, false, new ItemStack(Blocks.MONSTER_EGG, 1, 1));
		RecipeAPI.registerSmelting.addRecipe(silverfish2, DCHeatTier.KILN);

		ClimateSmelting silverfish1 = new ClimateSmelting(new ItemStack(Blocks.STONE, 1, 1), null, DCHeatTier.KILN,
				null, null, 0, false, new ItemStack(Blocks.MONSTER_EGG, 1, 0));
		RecipeAPI.registerSmelting.addRecipe(silverfish1, DCHeatTier.KILN);

		// grassとsandの風化
		ClimateSmelting sand = new ClimateSmelting(new ItemStack(Blocks.SAND, 1, 0), null, DCHeatTier.OVEN,
				DCHumidity.DRY, null, 0, false, new ItemStack(Blocks.DIRT, 1, 0));
		sand.requiredHeat().add(DCHeatTier.SMELTING);
		sand.requiredHeat().add(DCHeatTier.UHT);
		sand.requiredHeat().add(DCHeatTier.INFERNO);
		RecipeAPI.registerSmelting.addRecipe(sand, DCHeatTier.OVEN);

		ClimateSmelting sand2 = new ClimateSmelting(new ItemStack(Blocks.DIRT, 1, 0), null, DCHeatTier.OVEN,
				DCHumidity.DRY, null, 0, false, new ItemStack(Blocks.GRASS, 1, 0)) {
			@Override
			public boolean additionalRequire(World world, BlockPos pos) {
				if (world.rand.nextInt(2) == 0)
					return true;
				return false;
			}
		};
		sand2.requiredHeat().add(DCHeatTier.SMELTING);
		sand2.requiredHeat().add(DCHeatTier.UHT);
		sand2.requiredHeat().add(DCHeatTier.INFERNO);
		RecipeAPI.registerSmelting.addRecipe(sand2, DCHeatTier.OVEN);

		ClimateSmelting dirt2 = new ClimateSmelting(new ItemStack(Blocks.DIRT, 1, 0), null, DCHeatTier.WARM,
				DCHumidity.WET, null, 0, false, new ItemStack(Blocks.SAND, 1, 0)) {
			@Override
			public boolean additionalRequire(World world, BlockPos pos) {
				if (world.rand.nextInt(2) == 0)
					return true;
				return false;
			}
		};
		dirt2.requiredHum().add(DCHumidity.UNDERWATER);
		dirt2.requiredHeat().remove(DCHeatTier.NORMAL);
		RecipeAPI.registerSmelting.addRecipe(dirt2, DCHeatTier.WARM);

		ClimateSmelting dirt3 = new ClimateSmelting(new ItemStack(Blocks.GRASS, 1, 0), null, DCHeatTier.WARM,
				DCHumidity.WET, null, 0, false, new ItemStack(Blocks.DIRT, 1, 0));
		dirt3.requiredHum().add(DCHumidity.UNDERWATER);
		dirt3.requiredHeat().remove(DCHeatTier.NORMAL);
		RecipeAPI.registerSmelting.addRecipe(dirt3, DCHeatTier.WARM);

		ClimateSmelting sap = new ClimateSmelting(new ItemStack(Blocks.SAPLING, 1, 0), null, DCHeatTier.WARM,
				DCHumidity.WET, null, 0, false, new ItemStack(Blocks.DEADBUSH, 1, 0)) {
			@Override
			public boolean additionalRequire(World world, BlockPos pos) {
				if (world.getBlockState(pos.down()).getBlock() instanceof BlockDirt
						|| world.getBlockState(pos.down()).getBlock() instanceof BlockGrass)
					return true;
				return false;
			}
		};
		RecipeAPI.registerSmelting.addRecipe(sap, DCHeatTier.WARM);

		ClimateSmelting sap2 = new ClimateSmelting(new ItemStack(Blocks.DEADBUSH, 1, 0), null, DCHeatTier.OVEN,
				DCHumidity.DRY, null, 0, false, new ItemStack(Blocks.SAPLING, 1, 32767)) {
			@Override
			public boolean additionalRequire(World world, BlockPos pos) {
				if (world.getBlockState(pos.down()).getBlock() instanceof BlockDirt
						|| world.getBlockState(pos.down()).getMaterial() == Material.SAND
						|| world.getBlockState(pos.down()).getMaterial() == Material.GRASS)
					return true;
				return false;
			}
		};
		RecipeAPI.registerSmelting.addRecipe(sap2, DCHeatTier.OVEN);
	}

	public static void addShapedRecipe(ResourceLocation name, @Nonnull ItemStack result, Object... recipe) {
		ShapedOreRecipe ret = new ShapedOreRecipe(name, result, recipe);
		ret.setRegistryName(name);
		for (Ingredient ing : ret.getIngredients()) {
			if (ing instanceof OreIngredient && ing.getMatchingStacks().length < 1) {
				return;
			}
		}
		ForgeRegistries.RECIPES.register(ret);
	}

	public static void addShapelessRecipe(ResourceLocation name, @Nonnull ItemStack result, Object... recipe) {
		ShapelessOreRecipe ret = new ShapelessOreRecipe(name, result, recipe);
		ret.setRegistryName(name);
		for (Ingredient ing : ret.getIngredients()) {
			if (ing instanceof OreIngredient && ing.getMatchingStacks().length < 1) {
				return;
			}
		}
		ForgeRegistries.RECIPES.register(ret);
	}

	public static void addShapedNBTRecipe(ResourceLocation name, @Nonnull ItemStack result, Object... recipe) {
		ShapedNBTRecipe ret = new ShapedNBTRecipe(name, result, recipe);
		ret.setRegistryName(name);
		for (Ingredient ing : ret.getIngredients()) {
			if (ing instanceof OreIngredient && ing.getMatchingStacks().length < 1) {
				return;
			}
		}
		ForgeRegistries.RECIPES.register(ret);
	}

	public static void addShapelessNBTRecipe(ResourceLocation name, @Nonnull ItemStack result, Object... recipe) {
		ShapelessNBTRecipe ret = new ShapelessNBTRecipe(name, result, recipe);
		ret.setRegistryName(name);
		for (Ingredient ing : ret.getIngredients()) {
			if (ing instanceof OreIngredient && ing.getMatchingStacks().length < 1) {
				return;
			}
		}
		ForgeRegistries.RECIPES.register(ret);
	}

}
