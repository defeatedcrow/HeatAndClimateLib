package defeatedcrow.hac.core.event;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

// 常時監視系
public class TickEventDC {

	private static final Map<Integer, Integer> prevTime = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> counter = new HashMap<Integer, Integer>();

	// Weather checker
	@SubscribeEvent
	public void onTickEvent(TickEvent.WorldTickEvent event) {
		if (event.world != null && !event.world.isRemote && event.side == Side.SERVER) {
			int dim = event.world.provider.getDimension();
			if (!counter.containsKey(dim) || !prevTime.containsKey(dim)) {
				counter.put(dim, 200);
				prevTime.put(dim, -1);
			} else {
				int prev = prevTime.get(dim);
				int c = counter.get(dim);
				if (c > 0) {
					c--;
					counter.put(dim, c);
				} else {
					counter.put(dim, 200);
					int time = DCTimeHelper.currentTime(event.world);
					if (prev != time) {
						prevTime.put(dim, time);
						WeatherChecker.INSTANCE.setWeather(event.world);
						WeatherChecker.INSTANCE.sendPacket(event.world);
					}
				}
			}
		}
	}
}
