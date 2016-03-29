package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.core.climate.ClimateCalculator;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;

public class APILoader {

	public static void loadAPI() {
		ClimateAPI.register = new ClimateRegister();
		ClimateAPI.calculator = new ClimateCalculator();
		ClimateAPI.registerBlock = new HeatBlockRegister();

		registerClimate();
	}

	public static void registerClimate() {
		// heat
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_pumpkin, DCHeatTier.HOT);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.torch, DCHeatTier.HOT);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_furnace, DCHeatTier.OVEN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.fire, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.flowing_lava, DCHeatTier.KILN);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.lava, DCHeatTier.KILN);

		ClimateAPI.registerBlock.registerHeatBlock(Blocks.water, DCHeatTier.NORMAL);

		// cold
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.ice, DCHeatTier.COLD);
		ClimateAPI.registerBlock.registerHeatBlock(Blocks.packed_ice, DCHeatTier.COLD);

		// hum
		ClimateAPI.registerBlock.registerHumBlock(Blocks.flowing_water, DCHumidity.WET);
		ClimateAPI.registerBlock.registerHumBlock(Blocks.water, DCHumidity.WET);

		// air
		ClimateAPI.registerBlock.registerAirBlock(Blocks.air, DCAirflow.NORMAL);

		ClimateAPI.registerBlock.registerAirBlock(Blocks.leaves, DCAirflow.TIGHT);
		ClimateAPI.registerBlock.registerAirBlock(Blocks.leaves2, DCAirflow.TIGHT);

	}

}
