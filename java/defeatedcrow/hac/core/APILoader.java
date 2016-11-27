package defeatedcrow.hac.core;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.cultivate.CropAPI;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.ArmorMaterialRegister;
import defeatedcrow.hac.core.climate.ClimateAltCalculator;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.ThermalInsulationUtil;
import defeatedcrow.hac.core.climate.recipe.ClimateCropRegister;
import defeatedcrow.hac.core.climate.recipe.ClimateRecipeRegister;
import defeatedcrow.hac.core.climate.recipe.ClimateSmeltingRegister;
import defeatedcrow.hac.core.climate.recipe.FluidCraftRegister;
import defeatedcrow.hac.core.climate.recipe.MillRecipeRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class APILoader {

	public static void loadAPI() {
		ClimateAPI.register = new ClimateRegister();
		ClimateAPI.calculator = new ClimateAltCalculator();
		ClimateAPI.registerBlock = new HeatBlockRegister();
		ClimateAPI.isLoaded = true;

		RecipeAPI.registerRecipes = new ClimateRecipeRegister();
		RecipeAPI.registerSmelting = new ClimateSmeltingRegister();
		RecipeAPI.registerFluidRecipes = new FluidCraftRegister();
		RecipeAPI.registerMills = new MillRecipeRegister();
		RecipeAPI.isLoaded = true;

		DamageAPI.armorRegister = new ArmorMaterialRegister();
		DamageAPI.isLoaded = true;

		CropAPI.register = new ClimateCropRegister();
		CropAPI.isLoaded = true;

		registerClimate();
		registerMaterial();
		ThermalInsulationUtil.load();
	}

	private static void registerMaterial() {
		DamageAPI.armorRegister.RegisterMaterial(ArmorMaterial.LEATHER, 2.0F);
		DamageAPI.armorRegister.RegisterMaterial(ArmorMaterial.DIAMOND, 0.5F);
	}

	public static void registerClimate() {
		// heat
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.LIT_PUMPKIN, 32767, DCHeatTier.WARM);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.TORCH, 32767, DCHeatTier.HOT);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.LIT_FURNACE, 32767, DCHeatTier.OVEN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.FIRE, 32767, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.FLOWING_LAVA, 32767, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.LAVA, 32767, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.field_189877_df, 0, DCHeatTier.KILN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.WATER, 32767, DCHeatTier.NORMAL);

		// cold
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.ICE, 32767, DCHeatTier.COLD);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.SNOW, 32767, DCHeatTier.COOL);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.PACKED_ICE, 32767, DCHeatTier.COLD);

		// hum
		ClimateAPI.registerBlock.registerHumBlock(Blocks.SPONGE, 0, DCHumidity.DRY);
		ClimateAPI.registerBlock.registerHumBlock(Blocks.SOUL_SAND, 0, DCHumidity.DRY);

		ClimateAPI.registerBlock.registerHumBlock(Blocks.SPONGE, 1, DCHumidity.WET);

		ClimateAPI.registerBlock.registerHumBlock(Blocks.FLOWING_WATER, 32767, DCHumidity.UNDERWATER);
		ClimateAPI.registerBlock.registerHumBlock(Blocks.WATER, 32767, DCHumidity.UNDERWATER);

		// air
		ClimateAPI.registerBlock.registerAirBlock(Blocks.AIR, 32767, DCAirflow.NORMAL);

		ClimateAPI.registerBlock.registerAirBlock(Blocks.LEAVES, 32767, DCAirflow.TIGHT);
		ClimateAPI.registerBlock.registerAirBlock(Blocks.LEAVES2, 32767, DCAirflow.TIGHT);

	}

}
