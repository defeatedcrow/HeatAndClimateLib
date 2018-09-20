package defeatedcrow.hac.api.placeable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * EntityとItemを紐付けるインターフェイス。
 */
public interface IEntityItem {

	boolean canSpawnHere(World world, BlockPos pos);

	boolean spawnPlacementEntity(World world, Entity entity);

	Entity getPlacementEntity(World world, EntityPlayer player, double x, double y, double z, ItemStack item);

}
