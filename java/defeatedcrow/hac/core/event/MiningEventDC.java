package defeatedcrow.hac.core.event;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.api.placeable.IRapidCollectables;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;

public class MiningEventDC {

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
					if (charm.consumeCharmItem(entry.getValue()) == null) {
						event.getPlayer().inventory.setInventorySlotContents(entry.getKey(), entry.getValue());
						event.getPlayer().inventory.markDirty();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		BlockPos pos = event.getPos();
		ItemStack held = event.getItemStack();
		if (player != null && !player.worldObj.isRemote) {
			IBlockState state = player.worldObj.getBlockState(pos);
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
							if (target.doMining(event.getWorld(), p, state, player, held))
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
}
