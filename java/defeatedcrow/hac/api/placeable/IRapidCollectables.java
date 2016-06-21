package defeatedcrow.hac.api.placeable;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 指定したツールをで右クリックすると3x3x3の範囲採掘できるBlockのインターフェイス。<br>
 * 主にコンテナブロックなど、
 */
public interface IRapidCollectables {

	boolean isCollectable(@Nullable ItemStack item);

	int getCollectArea(@Nullable ItemStack item);

	boolean doMining(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack tool);

}
