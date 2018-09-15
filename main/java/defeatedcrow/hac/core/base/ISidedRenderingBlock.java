package defeatedcrow.hac.core.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface ISidedRenderingBlock {

	boolean isRendered(EnumFacing face, IBlockState state);

}
