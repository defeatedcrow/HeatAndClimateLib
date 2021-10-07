package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerCharmKey implements IMessageHandler<MessageCharmKey, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageCharmKey message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		if (player != null) {
			NonNullList<ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.KEY);

			// 発動するのは最も左の一つだけ
			for (ItemStack check : charms) {
				IJewelCharm charm = (IJewelCharm) check.getItem();
				if (DCUtil.playerCanUseCharm(player, check) && charm.onUsing(player, check)) {
					DCUtil.playerConsumeCharm(player, check);
					return null;
				}
			}
		}
		return null;
	}
}
