package defeatedcrow.hac.api.placeable;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 指定したツールで右クリックすると3x3x3の範囲採掘できるオブジェクトに実装するインターフェイス。<br>
 * 主にコンテナブロックなど、回収に時間を掛ける必要がない装飾系ブロックに実装する。<br>
 * また、右クリック収穫作物やEntityなどにも流用可能。
 */
public interface IRapidCollectables {

	boolean isCollectable(@Nullable ItemStack item);

	int getCollectArea(@Nullable ItemStack item);

	boolean doCollect(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack tool);

}
