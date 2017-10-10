package defeatedcrow.hac.core;

import defeatedcrow.hac.core.base.DCItemBlock;
import defeatedcrow.hac.core.item.ItemClimateChecker;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MaterialRegister {
	private MaterialRegister() {}

	public static void load() {
		registerBlock();
		registerItem();
		registerHarvestLevel();
	}

	static void registerBlock() {}

	static void registerItem() {
		DCInit.climate_checker = new ItemClimateChecker().setCreativeTab(ClimateCore.climate)
				.setUnlocalizedName(ClimateCore.PACKAGE_BASE + "_checker");
		ForgeRegistries.ITEMS.register(DCInit.climate_checker.setRegistryName(ClimateCore.PACKAGE_BASE + "_checker"));
	}

	private static void registerHarvestLevel() {}

	static void registerBlock(Block block, String name) {
		Block reg = block.setRegistryName(name);
		ForgeRegistries.BLOCKS.register(reg);
		ForgeRegistries.ITEMS.register(new DCItemBlock(reg));
	}
}
