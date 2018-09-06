package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.api.placeable.IRapidCollectables;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.plugin.baubles.DCPluginBaubles;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
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
			Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(event.getPlayer(), CharmType.TOOL);
			for (Entry<Integer, ItemStack> entry : charms.entrySet()) {
				DCLogger.debugLog("mining charm");
				IJewelCharm charm = ((IJewelCharm) entry.getValue().getItem());
				if (charm.onToolUsing(event.getPlayer(), event.getPos(), event.getState(), entry.getValue())) {
					if (DCUtil.isEmpty(charm.consumeCharmItem(entry.getValue()))) {
						event.getPlayer().inventory.setInventorySlotContents(entry.getKey(), ItemStack.EMPTY);
						event.getPlayer().inventory.markDirty();
						event.getPlayer().playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
					}
				}
			}

			if (Loader.isModLoaded("baubles")) {
				ItemStack item = DCPluginBaubles.getBaublesCharm(event.getPlayer(), CharmType.DEFFENCE);
				if (!DCUtil.isEmpty(item)) {
					IJewelCharm charm = (IJewelCharm) item.getItem();
					if (charm.onToolUsing(event.getPlayer(), event.getPos(), event.getState(), item)) {
						if (DCUtil.isEmpty(charm.consumeCharmItem(item))) {
							DCPluginBaubles.setBaublesCharmEmpty(event.getPlayer());
						}
					}
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
		if (player != null && !player.world.isRemote) {
			IBlockState state = player.world.getBlockState(pos);
			if (state != null && state.getBlock() != null) {
				// IRapidCollectables
				if (state.getBlock() instanceof IRapidCollectables
						&& ((IRapidCollectables) state.getBlock()).isCollectable(held)) {
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

					if (flag) {
						event.setUseBlock(Result.ALLOW);
					}
				}
			}
		}
	}

	// 虚空を右クリックした場合
	@SubscribeEvent
	public void onRightClickAir(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack held = event.getItemStack();
		if (player != null && player.isSneaking() && !DCUtil.isEmpty(held) && !player.world.isRemote) {
			RayTraceResult ray = DCUtil.getRayTrace(player.world, player);
			BlockPos pos = null;
			if (ray == null) {
				pos = DCUtil.getRayTracePos(player.world, player);
			} else {
				pos = ray.getBlockPos();
			}
			if (pos != null) {
				AxisAlignedBB aabb = new AxisAlignedBB(pos.add(-2, -2, -2), pos.add(2, 2, 2));
				List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(null, aabb);
				List<IRapidCollectables> list2 = new ArrayList<IRapidCollectables>();
				boolean flag = false;
				for (Entity get : list) {
					if (!get.isDead && get instanceof IRapidCollectables) {
						list2.add((IRapidCollectables) get);
					}
				}

				for (IRapidCollectables get2 : list2) {
					if (get2.isCollectable(held)) {
						if (get2.doCollect(player.world, pos, player.world.getBlockState(pos), player, held))
							flag = true;
					}
				}
			}
		}
	}

}
