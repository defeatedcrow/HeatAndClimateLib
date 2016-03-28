package defeatedcrow.hac.api.heat;

import java.util.List;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * 熱利用BlockやTileEntity、Entityに持たせる伝熱機能。
 */
public interface IHeatReceiver {

	List<BlockPos> getSourceList();

	int getCurrentClimateCode();

	boolean canReceiveHeat(World world, BlockPos pos);

	boolean isActive(World world, BlockPos pos);

}
