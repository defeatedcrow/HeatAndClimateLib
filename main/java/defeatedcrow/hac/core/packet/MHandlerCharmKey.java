package defeatedcrow.hac.core.packet;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.plugin.baubles.DCPluginBaubles;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerCharmKey implements IMessageHandler<MessageCharmKey, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageCharmKey message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		if (player != null) {
			Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.KEY);

			// 発動するのは最も左の一つだけ
			for (Entry<Integer, ItemStack> entry : charms.entrySet()) {
				IJewelCharm charm = (IJewelCharm) entry.getValue().getItem();
				if (charm.onUsing(player, entry.getValue())) {
					if (DCUtil.isEmpty(charm.consumeCharmItem(entry.getValue()))) {
						player.inventory.setInventorySlotContents(entry.getKey(), ItemStack.EMPTY);
						player.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
						player.inventory.markDirty();
						return null;
					}
				}
			}
			if (Loader.isModLoaded("baubles")) {
				List<ItemStack> items = DCPluginBaubles.getBaublesCharm(player, CharmType.KEY);
				for (ItemStack item : items) {
					if (!DCUtil.isEmpty(item)) {
						IJewelCharm charm = (IJewelCharm) item.getItem();
						if (charm.onUsing(player, item)) {
							if (DCUtil.isEmpty(charm.consumeCharmItem(item))) {
								DCPluginBaubles.setBaublesCharmEmpty(player);
								player.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
