package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageWeatherUpdate;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.world.World;

public class WeatherChecker {

	public static final WeatherChecker INSTANCE = new WeatherChecker();

	private WeatherChecker() {}

	public static final Map<Integer, Float> rainPowerMap = new HashMap<Integer, Float>();

	public static final Map<Integer, Integer> rainCountMap = new HashMap<Integer, Integer>();

	public static final Map<Integer, Integer> sunCountMap = new HashMap<Integer, Integer>();

	public static void setWeather(World world) {
		if (world == null || world.isRemote) {
			return;
		}
		int dim = world.provider.getDimension();
		float rain = world.rainingStrength;
		int rainTime = world.getWorldInfo().getRainTime();
		int sunTime = world.getWorldInfo().getCleanWeatherTime();

		/*
		 * DCLogger.debugLog("=current weather info=");
		 * DCLogger.debugLog("rain:" + rain + " time:" + rainTime);
		 * DCLogger.debugLog("thunder:" + world.thunderingStrength + " time:" + world.getWorldInfo().getThunderTime());
		 * DCLogger.debugLog("sun time:" + sunTime);
		 */

		rainPowerMap.put(dim, rain);

		boolean r = false;
		if (sunTime > 100) {

		} else {
			if (rain > 0.25F && rainTime > 100) {
				r = true;
			}
		}

		if (r) {
			if (rainCountMap.containsKey(dim)) {
				int count = rainCountMap.get(dim);
				count++;
				rainCountMap.put(dim, count);
			} else {
				rainCountMap.put(dim, 1);
			}
			sunCountMap.put(dim, 0);
		} else {
			if (sunCountMap.containsKey(dim)) {
				int count = sunCountMap.get(dim);
				count++;
				if (count > 148) {
					count = DCUtil.rand.nextInt(100);
				}
				sunCountMap.put(dim, count);
			} else {
				sunCountMap.put(dim, 1);
			}
			rainCountMap.put(dim, 0);
		}
	}

	public static void setWeather(int dim, float rain, int countR, int countS) {
		rainPowerMap.put(dim, rain);
		rainCountMap.put(dim, countR);
		sunCountMap.put(dim, countS);
		DCLogger.debugLog("dim " + dim + " received data: " + rain + "/" + countR + ", " + countS);
	}

	public static int getTempOffset(int dim, boolean isHell) {
		int count = 0;
		int sun = 0;
		float rain = 0F;
		if (rainPowerMap.containsKey(dim)) {
			rain = rainPowerMap.get(dim);
		}
		if (rainCountMap.containsKey(dim)) {
			count = rainCountMap.get(dim);
		}
		if (sunCountMap.containsKey(dim)) {
			sun = sunCountMap.get(dim);
		}
		if (sun > 240 && !isHell) {
			// 日照り気味
			return 1;
		}
		if (count > 6 && rain > 0.25F) {
			return isHell ? 1 : -1;
		}
		if (rain > 0.85F) {
			return isHell ? 1 : -1;
		}

		return 0;
	}

	public static int getHumOffset(int dim, boolean isHell) {
		int count = 0;
		float rain = 0F;
		if (rainPowerMap.containsKey(dim)) {
			rain = rainPowerMap.get(dim);
		}
		if (rainCountMap.containsKey(dim)) {
			count = rainCountMap.get(dim);
		}

		if (count > 0 && rain > 0.0F) {
			return isHell ? 0 : 1;
		}

		return 0;
	}

	public static int getWindOffset(int dim, boolean isHell) {
		int count = 0;
		float rain = 0F;
		if (rainPowerMap.containsKey(dim)) {
			rain = rainPowerMap.get(dim);
		}
		if (rainCountMap.containsKey(dim)) {
			count = rainCountMap.get(dim);
		}

		if (count > 2 && rain > 0.25F) {
			return 1;
		}
		if (rain > 0.85F) {
			return 1;
		}

		return 0;
	}

	public static void sendPacket(World world) {
		if (world == null || world.isRemote) {
			return;
		}
		int dim = world.provider.getDimension();
		int count = 0;
		int sun = 0;
		float rain = 0F;
		if (rainPowerMap.containsKey(dim)) {
			rain = rainPowerMap.get(dim);
		}
		if (rainCountMap.containsKey(dim)) {
			count = rainCountMap.get(dim);
		}
		if (sunCountMap.containsKey(dim)) {
			sun = sunCountMap.get(dim);
		}
		HaCPacket.INSTANCE.sendToAll(new MessageWeatherUpdate(dim, rain, count, sun));
		// DCLogger.debugLog("send weather data : rain: " + rain + "/" + count + ", sun: " + sun);
	}

}
