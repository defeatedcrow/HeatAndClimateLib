package defeatedcrow.hac.core.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.base.DCTileBlock;

@SideOnly(Side.CLIENT)
public class JsonRegister {
	private JsonRegister() {
	}

	public static void load() {
		regSimpleBlock(DCInit.stove_fuel, "stovefuel", "machine", 0);

		regSimpleItem(DCInit.climate_checker, "checker", "tool");
	}

	static void regSimpleItem(Item item, String s, String p) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ClimateCore.PACKAGE_ID + ":" + p
				+ "/" + s, "inventory"));
	}

	// 汎用Tile使用メソッド
	static void regSimpleBlock(Block block, String s, String p, int maxMeta) {
		ModelLoader.setCustomStateMapper(block,
				(new StateMap.Builder()).ignore(((DCTileBlock) block).FACING, ((DCTileBlock) block).TYPE).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(ClimateCore.PACKAGE_ID
				+ ":" + "basetile"));
		if (maxMeta == 0) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
					ClimateCore.PACKAGE_ID + ":" + p + "/" + s, "inventory"));
		} else {
			for (int i = 0; i < maxMeta + 1; i++) {
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(
						ClimateCore.PACKAGE_ID + ":" + p + "/" + s + i, "inventory"));
			}
		}
	}

}
