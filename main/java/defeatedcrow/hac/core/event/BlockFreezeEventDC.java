package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.hook.DCBlockFreezeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockFreezeEventDC {

	@SubscribeEvent
	public void onFreeze(DCBlockFreezeEvent event) {
		if (!event.world.isRemote && event.pos != null) {
			World world = event.world;
			BlockPos pos = event.pos;
			IBlockState st = world.getBlockState(pos);

			if (st.getMaterial() == Material.WATER && world.isAirBlock(pos.up()) && canFreezePos(world, pos)) {
				world.setBlockState(pos, Blocks.ICE.getDefaultState(), 2);
				world.notifyNeighborsOfStateChange(pos, Blocks.ICE, false);
				event.setResult(Result.ALLOW);
				// DCLogger.debugLog("Success to freeze!!");
			}
		}
	}

	boolean canFreezePos(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!world.isRemote && pos != null && state != null) {
			Block block = state.getBlock();
			if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && block.getMetaFromState(state) == 0) {
				DCHeatTier clm = ClimateAPI.calculator.getAverageTemp(world, pos);
				return clm.getTier() < DCHeatTier.COLD.getTier();
			}
		}
		return false;
	}

}
