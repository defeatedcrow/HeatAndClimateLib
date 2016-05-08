package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.climate.ArmorMaterialRegister;
import defeatedcrow.hac.core.climate.ClimateCalculator;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.recipe.ClimateRecipeRegister;
import defeatedcrow.hac.core.climate.recipe.ClimateSmeltingRegister;

public class APILoader {

	public static void loadAPI() {
		ClimateAPI.register = new ClimateRegister();
		ClimateAPI.calculator = new ClimateCalculator();
		ClimateAPI.registerBlock = new HeatBlockRegister();

		RecipeAPI.registerRecipes = new ClimateRecipeRegister();
		RecipeAPI.registerSmelting = new ClimateSmeltingRegister();

		DamageAPI.armorRegister = new ArmorMaterialRegister();

		registerClimate();
		registerMaterial();
	}

	private static void registerMaterial() {
		DamageAPI.armorRegister.RegisterMaterial(ArmorMaterial.LEATHER, 3.0F);
		DamageAPI.armorRegister.RegisterMaterial(ArmorMaterial.DIAMOND, 1.0F);
	}

	public static void registerClimate() {
		// heat
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_pumpkin, 32767, DCHeatTier.HOT);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.torch, 32767, DCHeatTier.HOT);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_furnace, 32767, DCHeatTier.OVEN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.fire, 32767, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.flowing_lava, 32767, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lava, 32767, DCHeatTier.KILN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.water, 32767, DCHeatTier.NORMAL);

		// cold
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.ice, 32767, DCHeatTier.COLD);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.snow, 32767, DCHeatTier.COLD);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.packed_ice, 32767, DCHeatTier.COLD);

		// hum
		ClimateAPI.registerBlock.registerHumBlock(Blocks.sponge, 0, DCHumidity.DRY);

		ClimateAPI.registerBlock.registerHumBlock(Blocks.sponge, 1, DCHumidity.WET);

		ClimateAPI.registerBlock.registerHumBlock(Blocks.flowing_water, 32767, DCHumidity.UNDERWATER);
		ClimateAPI.registerBlock.registerHumBlock(Blocks.water, 32767, DCHumidity.UNDERWATER);

		// air
		ClimateAPI.registerBlock.registerAirBlock(Blocks.air, 32767, DCAirflow.NORMAL);

		ClimateAPI.registerBlock.registerAirBlock(Blocks.leaves, 32767, DCAirflow.TIGHT);
		ClimateAPI.registerBlock.registerAirBlock(Blocks.leaves2, 32767, DCAirflow.TIGHT);

	}

}
