package defeatedcrow.hac.core.packet.command;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.WeatherChecker;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MHandlerComDrought implements IMessageHandler<MessageComDrought, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageComDrought message, MessageContext ctx) {
		if (ctx != null && ctx.side == Side.CLIENT) {
			int data = message.data;
			if (data == 0) {
				WeatherChecker.INSTANCE.sunCountMap.put(0, 0);
			} else {
				WeatherChecker.INSTANCE.sunCountMap.put(0, CoreConfigDC.droughtFrequency * 24 + 1);
			}
		}
		return null;
	}
}
