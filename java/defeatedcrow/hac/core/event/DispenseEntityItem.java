package defeatedcrow.hac.core.event;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.placeable.IEntityItem;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseEntityItem extends BehaviorDefaultDispenseItem {

	private static final DispenseEntityItem INSTANCE = new DispenseEntityItem();

	public final List<Item> dispenceList = Lists.newArrayList();

	public static DispenseEntityItem getInstance() {
		return INSTANCE;
	}

	private DispenseEntityItem() {}

	private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

	@Override
	@Nonnull
	public ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
		if (!DCUtil.isEmpty(stack) && stack.getItem() instanceof IEntityItem) {
			return spawnItemEntity(source, stack);
		} else {
			return super.dispenseStack(source, stack);
		}
	}

	@Nonnull
	private ItemStack spawnItemEntity(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
		World world = source.getWorld();
		EnumFacing face = source.getBlockState().getValue(BlockDispenser.FACING);
		BlockPos pos = source.getBlockPos().offset(face);
		IEntityItem item = (IEntityItem) stack.getItem();
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.25D, pos.getY() + 0.25D, pos.getZ() + 0.25D,
				pos.getX() + 0.75D, pos.getY() + 0.75D, pos.getZ() + 0.75D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);
		if (list.isEmpty()) {
			Entity entity = item.getPlacementEntity(world, null, pos.getX() + 0.5D, pos.getY() + 0.5D,
					pos.getZ() + 0.5D, stack);
			if (entity != null) {
				if (item.spawnPlacementEntity(world, entity)) {
					DCUtil.reduceStackSize(stack, 1);
				}
			}
		}
		return stack;
	}

}
