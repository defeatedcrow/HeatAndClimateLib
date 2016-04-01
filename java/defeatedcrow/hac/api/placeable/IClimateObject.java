package defeatedcrow.hac.api.placeable;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * アップデート処理内でClimateレシピを扱うオブジェクトに持たせる。
 * メソッド内で、Climate更新、レシピ処理などを全部行う。
 */
public interface IClimateObject {

	void onUpdateClimate(World world, BlockPos pos);

}
