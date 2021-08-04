package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.item.IAnimalFood;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.api.placeable.IRapidCollectables;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickEventDC {

	@SubscribeEvent
	public void onMining(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isRemote && event.getPlayer() != null) {
			IBlockState state = event.getState();
			BlockPos pos = event.getPos();
			ItemStack held = event.getPlayer().getHeldItemMainhand();

			// TOOLチャーム
			NonNullList<ItemStack> charms = DCUtil.getPlayerCharm(event.getPlayer(), CharmType.TOOL);
			for (ItemStack item : charms) {
				IJewelCharm charm = ((IJewelCharm) item.getItem());
				if (charm.onToolUsing(event.getPlayer(), event.getPos(), event.getState(), item)) {
					event.setCanceled(true);
				}
			}
		}
	}

	// 右クリック回収機構
	// Blockにターゲットした場合
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		BlockPos pos = event.getPos();
		ItemStack held = event.getItemStack();
		if (player != null) {
			IBlockState state = player.world.getBlockState(pos);
			if (state != null && state.getBlock() != null) {
				// IRapidCollectables
				if (state.getBlock() instanceof IRapidCollectables && ((IRapidCollectables) state.getBlock())
						.isCollectable(held)) {
					if (!player.world.isRemote) {
						IRapidCollectables current = (IRapidCollectables) state.getBlock();
						int area = current.getCollectArea(held);
						BlockPos min = new BlockPos(pos.add(-area, -area, -area));
						BlockPos max = new BlockPos(pos.add(area, area, area));
						Iterable<BlockPos> itr = pos.getAllInBox(min, max);
						boolean flag = false;
						for (BlockPos p : itr) {
							IBlockState block = event.getWorld().getBlockState(p);
							if (block.getBlock() == state.getBlock()) {
								IRapidCollectables target = (IRapidCollectables) block.getBlock();
								if (target.doCollect(event.getWorld(), p, block, player, held))
									flag = true;
							}
						}
					}

					event.setUseBlock(Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();
		ItemStack held = event.getItemStack();
		if (!DCUtil.isEmpty(held) && player != null && target instanceof EntityAnimal) {
			EntityAnimal living = (EntityAnimal) target;
			boolean b = held.getItem() instanceof IAnimalFood && ((IAnimalFood) held.getItem())
					.isTargetAnimal(living, held);
			boolean b2 = held.getItem() == Items.APPLE && ClimateCore.isDebug;
			if (b || b2) {
				boolean flag = false;
				if (living.getHealth() < living.getMaxHealth()) {
					living.heal(2.0F);
					flag = true;
				} else {
					if (living.isChild()) {
						living.ageUp((int) (-living.getGrowingAge() / 20 * 0.1F), true);
						flag = true;
					} else if (!living.isInLove()) {
						living.setInLove(player);
						flag = true;
					}
				}

				if (flag) {
					if (!player.capabilities.isCreativeMode) {
						held.shrink(1);
					}
					for (EntityAITaskEntry task : living.tasks.taskEntries) {
						if (task != null && task.action instanceof EntityAITempt) {
							EntityAITempt ai = (EntityAITempt) task.action;
							if (!ai.temptItem.contains(held.getItem())) {
								ai.temptItem.add(held.getItem());
							}
						}
					}
					event.setCancellationResult(EnumActionResult.SUCCESS);
				}
			}
		}
	}
}
