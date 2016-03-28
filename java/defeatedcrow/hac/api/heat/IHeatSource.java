package defeatedcrow.hac.api.heat;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.DCHeatTier;

/**
 * 熱源TileEntityに持たせる伝熱機能。<br>
 * instanceofチェックの対象がTikeEntityのみなので、他のオブジェクトに対応できない。
 */
public interface IHeatSource {

	DCHeatTier getTier();

	int getMaxTransfer();

	int getCurrentTransfer();

	int useHeatTransfer(World world, BlockPos pos, boolean sim);

	boolean isActive(World world, BlockPos pos);

}
