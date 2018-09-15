package defeatedcrow.hac.api.climate;

import net.minecraft.block.state.IBlockState;

/* 何らかの理由で真下の熱源を無効化したい場合に使用する
/* ほぼ熱交換器専用
 * */
public interface IHeatCanceler {

	boolean isActive(IBlockState state);

}
