package defeatedcrow.hac.core.event;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.recipe.DCBlockUpdateEvent;

public class BlockUpdateDC {

	@SubscribeEvent
	public void onUpdate(DCBlockUpdateEvent event) {
		if (!event.world.isRemote && event.state != null) {
			World world = event.world;
			BlockPos p = event.pos;
			IBlockState st = event.state;
			Block block = st.getBlock();
			boolean f = true;

			if (block == null || st.getMaterial() == Material.AIR)
				return;

			int meta = block.getMetaFromState(st);

			// 直接指定の仕様
			if (block == Blocks.FARMLAND) {
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, p, 4, true);
				// 耕地はWET以上の湿度では湿る
				if (hum.getID() > 1) {
					world.setBlockState(p, block.getStateFromMeta(7), 3);
					// DCLogger.debugLog("farmland update");
					event.setCanceled(true);
					return;
				}
			} else if (block instanceof IGrowable && block != Blocks.GRASS && block != Blocks.TALLGRASS) {
				IGrowable grow = (IGrowable) block;
				DCHeatTier temp = ClimateAPI.calculator.getHeatTier(world, p, 2, false);
				if (grow.canGrow(world, p, st, false) && world.rand.nextInt(3) == 0) {
					if (temp == DCHeatTier.HOT) {
						grow.grow(world, world.rand, p, st);
					} else if (temp.getTier() < 0) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

}
