package defeatedcrow.hac.asm;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ClimateAsmConfig {

	private ClimateAsmConfig() {}

	public static final ClimateAsmConfig INSTANCE = new ClimateAsmConfig();

	public void load(Configuration cfg) {

		try {
			cfg.load();
			Property p_1 = cfg
					.get("asm setting", "Block Update Override", true, "Enable override the vanilla method: Block#updateTick.");
			Property p_2 = cfg
					.get("asm setting", "Block Freeze Override", true, "Enable override the vanilla method: World#canBlockFreezeBody.");
			Property p_3 = cfg
					.get("asm setting", "Entity In Air Override", true, "Enable override the vanilla method: Entity#isInsideOfMaterial.");
			Property p_4 = cfg
					.get("asm setting", "EntityItem Update Override", true, "Enable override the vanilla method: EntityItem#onEntityItemUpdate.");
			Property p_5 = cfg
					.get("asm setting", "Biome Temperature Override", true, "Enable override the vanilla method: Biome#getTemperature.");
			Property p_6 = cfg
					.get("asm setting", "Cave Gen Override", true, "Enable override the vanilla method: MapGenCave#digBlock.");
			Property p_7 = cfg
					.get("asm setting", "Ravine Gen Override", true, "Enable override the vanilla method: MapGenRavine#digBlock.");
			Property p_8 = cfg
					.get("asm setting", "ItemFood Override", true, "Enable override the vanilla method: ItemFood#onItemUseFinish.");

			DCMethodTransformer.enableBlockUpdate = p_1.getBoolean();
			DCMethodTransformer.enableBlockFreeze = p_2.getBoolean();
			DCMethodTransformer.enableEntityInAir = p_3.getBoolean();
			DCMethodTransformer.enableDropUpdate = p_4.getBoolean();
			DCMethodTransformer.enableBiomeTemp = p_5.getBoolean();
			DCMethodTransformer.enableCaveWater = p_6.getBoolean();
			DCMethodTransformer.enableRavineWater = p_7.getBoolean();
			DCMethodTransformer.enableEatFood = p_8.getBoolean();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}

}
