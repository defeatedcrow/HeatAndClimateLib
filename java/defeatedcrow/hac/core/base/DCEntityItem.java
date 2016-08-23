package defeatedcrow.hac.core.base;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.placeable.IEntityItem;

/* Entity設置Item */
public abstract class DCEntityItem extends DCItem implements IEntityItem {

	public DCEntityItem() {
		super();
	}

	/* 設置動作 */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) {
			if (!world.isRemote && pos.getY() > 0 && pos.getY() < 255 && player.canPlayerEdit(pos, facing, stack)) {
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (block != Blocks.TALLGRASS && block != Blocks.VINE && block != Blocks.DEADBUSH) {
					if (canSpawnHere(world, pos)) {
						double fX = facing.getFrontOffsetX() * 0.25D;
						double fY = facing.getFrontOffsetY() * 0.25D;
						double fZ = facing.getFrontOffsetZ() * 0.25D;
						Entity entity = this.getPlacementEntity(world, player, pos.getX() + hitX + fX, pos.getY()
								+ hitY + fY, pos.getZ() + hitZ + fZ, stack);
						if (entity != null) {
							if (this.spawnPlacementEntity(world, entity)) {
								--stack.stackSize;
								return EnumActionResult.SUCCESS;
							}
						}
					}
				}
			}
			return EnumActionResult.SUCCESS;
		} else {
			return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
		}
	}

	@Override
	public boolean canSpawnHere(World world, BlockPos pos) {
		return true;
	}

	@Override
	public abstract Entity getPlacementEntity(World world, EntityPlayer player, double x, double y, double z,
			ItemStack item);

	@Override
	public boolean spawnPlacementEntity(World world, Entity entity) {
		if (entity != null) {
			return world.spawnEntityInWorld(entity);
		}
		return false;
	}

}
