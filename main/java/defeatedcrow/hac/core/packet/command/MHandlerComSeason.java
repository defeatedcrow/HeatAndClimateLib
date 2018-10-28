package defeatedcrow.hac.core.packet.command;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MHandlerComSeason implements IMessageHandler<MessageComSeason, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageComSeason message, MessageContext ctx) {
		if (ctx != null && ctx.side == Side.CLIENT) {
			int data = message.data;
			EnumSeason season = null;
			if (data == 0) {
				season = EnumSeason.SPRING;
			} else if (data == 1) {
				season = EnumSeason.SUMMER;
			} else if (data == 2) {
				season = EnumSeason.AUTUMN;
			} else if (data == 3) {
				season = EnumSeason.WINTER;
			}
			DCTimeHelper.forcedSeason = season;
		}
		return null;
	}
}
