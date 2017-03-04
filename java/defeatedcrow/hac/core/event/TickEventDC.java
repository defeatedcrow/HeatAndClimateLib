package defeatedcrow.hac.core.event;

import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

// 常時監視系
public class TickEventDC {

	private int prevDate = 0;
	private int prevTime = 0;
	private static int count = 600;

	// Weather checker
	@SubscribeEvent
	public void onTickEvent(TickEvent.WorldTickEvent event) {
		if (event.world != null && !event.world.isRemote && event.side == Side.SERVER) {
			if (count > 0) {
				count--;
			} else {
				count = 600;
				int date = DCTimeHelper.getDay(event.world);
				int time = DCTimeHelper.currentTime(event.world);
				if (prevDate != date) {
					prevDate = date;
					prevTime = 0;
				}
				if (prevTime != time) {
					prevTime = time;
					WeatherChecker.INSTANCE.setWeather(event.world);
					WeatherChecker.INSTANCE.sendPacket(event.world);
				}
			}
		}
	}
}
