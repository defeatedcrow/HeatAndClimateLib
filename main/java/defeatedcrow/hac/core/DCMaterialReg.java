package defeatedcrow.hac.core;

import defeatedcrow.hac.core.base.DCItemBlock;
import defeatedcrow.hac.core.item.ItemClimateChecker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DCMaterialReg {
	private DCMaterialReg() {}

	public static void load() {
		registerBlock();
		registerItem();
		registerHarvestLevel();
	}

	static void registerBlock() {}

	static void registerItem() {
		DCInit.climate_checker = new ItemClimateChecker().setCreativeTab(ClimateCore.climate)
				.setUnlocalizedName(ClimateCore.PACKAGE_BASE + "_checker");
		registerItem(DCInit.climate_checker, ClimateCore.PACKAGE_BASE + "_checker", ClimateCore.MOD_ID);
	}

	private static void registerHarvestLevel() {}

	public static void registerBlock(Block block, String name, String modid) {
		if (block == null)
			return;
		Block reg = block.setRegistryName(modid, name);
		ForgeRegistries.BLOCKS.register(reg);
		ForgeRegistries.ITEMS.register(new DCItemBlock(reg));
	}

	public static void registerItem(Item item, String name, String modid) {
		if (item == null)
			return;
		ForgeRegistries.ITEMS.register(item.setRegistryName(modid, name));
	}

	public static void registerBlock(String modid, Block block, String name) {
		if (block == null)
			return;
		Block reg = block.setRegistryName(modid, name);
		ForgeRegistries.BLOCKS.register(reg);
		ForgeRegistries.ITEMS.register(new DCItemBlock(reg));
	}

	public static void registerItem(String modid, Item item, String name) {
		if (item == null)
			return;
		ForgeRegistries.ITEMS.register(item.setRegistryName(modid, name));
	}
}
