package defeatedcrow.hac.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import defeatedcrow.hac.core.item.ItemClimateChecker;

public class MaterialRegister {
	private MaterialRegister() {
	}

	public static void load() {
		registerBlock();
		registerItem();
		registerHarvestLevel();
	}

	static void registerBlock() {
	}

	static void registerItem() {
		DCInit.climate_checker = new ItemClimateChecker().setCreativeTab(ClimateCore.climate).setUnlocalizedName(
				ClimateCore.PACKAGE_BASE + "_checker");
		GameRegistry.registerItem(DCInit.climate_checker, ClimateCore.PACKAGE_BASE + "_checker");
	}

	private static void registerHarvestLevel() {
	}
}
