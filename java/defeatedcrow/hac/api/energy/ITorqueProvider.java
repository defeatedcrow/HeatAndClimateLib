package defeatedcrow.hac.api.energy;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * トルクを発生するTileEntityに実装するもの<br>
 * トルクマシンは、発生側のTileEntityが能動的に動くため、<br>
 * 外部からこれを利用したトルク発生機を追加する場合は、隣接Tileへの伝達処理を自作して下さい。
 */
public interface ITorqueProvider extends ITorqueDC {

	List<EnumFacing> getOutputSide();

	float getAmount();

	boolean canProvideTorque(World world, BlockPos outputPos, EnumFacing output);

	float provideTorque(World world, BlockPos outputPos, EnumFacing output, boolean sim);

}
