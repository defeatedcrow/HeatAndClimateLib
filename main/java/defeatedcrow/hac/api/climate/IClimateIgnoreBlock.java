package defeatedcrow.hac.api.climate;

import net.minecraft.block.state.IBlockState;

/*
 * 気候計算に寄与しないブロック
 */
public interface IClimateIgnoreBlock {

	boolean isActive(IBlockState state);

}
