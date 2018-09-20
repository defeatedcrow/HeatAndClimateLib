package defeatedcrow.hac.api.climate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Reduce Tier of HeatTier if it's between target and heat source block.
 */
public interface IThermalInsulationBlock {

	/*
	 * -1: prevent all
	 * 0: no prevention
	 * 1~ : reduce amount
	 */
	int getReductionAmount(World world, BlockPos pos, IBlockState state);

}
