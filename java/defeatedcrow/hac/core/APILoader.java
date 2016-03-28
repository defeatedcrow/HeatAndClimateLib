package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.DCsClimateAPI;
import defeatedcrow.hac.core.climate.ClimateCalculator;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;

public class APILoader {

	public static void loadAPI() {
		DCsClimateAPI.register = new ClimateRegister();
		DCsClimateAPI.calculator = new ClimateCalculator();
		DCsClimateAPI.registerBlock = new HeatBlockRegister();

		registerClimate();
	}

	public static void registerClimate() {
		// heat
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_pumpkin, DCHeatTier.HOT);
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.torch, DCHeatTier.HOT);

		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.lit_furnace, DCHeatTier.OVEN);

		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.fire, DCHeatTier.KILN);
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.flowing_lava, DCHeatTier.KILN);
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.lava, DCHeatTier.KILN);

		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.water, DCHeatTier.NORMAL);

		// cold
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.ice, DCHeatTier.COLD);
		DCsClimateAPI.registerBlock.registerHeatBlock(Blocks.packed_ice, DCHeatTier.COLD);

		// hum
		DCsClimateAPI.registerBlock.registerHumBlock(Blocks.flowing_water, DCHumidity.WET);
		DCsClimateAPI.registerBlock.registerHumBlock(Blocks.water, DCHumidity.WET);

		// air
		DCsClimateAPI.registerBlock.registerAirBlock(Blocks.air, DCAirflow.NORMAL);

		DCsClimateAPI.registerBlock.registerAirBlock(Blocks.leaves, DCAirflow.TIGHT);
		DCsClimateAPI.registerBlock.registerAirBlock(Blocks.leaves2, DCAirflow.TIGHT);

	}

}
