package defeatedcrow.hac.asm;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import defeatedcrow.hac.api.recipe.DCBlockUpdateEvent;

public class DCEventFactory {

	public static boolean onBlockUpdate(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		DCBlockUpdateEvent event = new DCBlockUpdateEvent(worldIn, pos, state, rand);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return true;
		}
		return false;
	}

}
