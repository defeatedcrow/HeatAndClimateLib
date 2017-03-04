package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.core.climate.WeatherChecker;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MHandlerWeatherUpdate implements IMessageHandler<MessageWeatherUpdate, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageWeatherUpdate message, MessageContext ctx) {
		if (ctx != null && ctx.side == Side.CLIENT) {
			int dim = message.dim;
			int cR = message.rainCount;
			int cS = message.sunCount;
			float rain = message.rain;
			WeatherChecker.INSTANCE.setWeather(dim, rain, cR, cS);
		}
		return null;
	}
}
