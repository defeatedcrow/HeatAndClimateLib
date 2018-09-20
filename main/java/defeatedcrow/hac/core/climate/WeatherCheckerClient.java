package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.config.CoreConfigDC;

public class WeatherCheckerClient {

	public static final WeatherCheckerClient INSTANCE = new WeatherCheckerClient();

	private WeatherCheckerClient() {}

	public static final Map<Integer, Float> rainPowerMap = new HashMap<Integer, Float>();

	public static final Map<Integer, Integer> rainCountMap = new HashMap<Integer, Integer>();

	public static final Map<Integer, Integer> sunCountMap = new HashMap<Integer, Integer>();

	private static final int drought = CoreConfigDC.droughtFrequency;

	public static void setWeather(int dim, float rain, int countR, int countS) {
		rainPowerMap.put(dim, rain);
		rainCountMap.put(dim, countR);
		sunCountMap.put(dim, countS);
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
		if (drought > 0 && sun > drought && !isHell) {
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
		} else if (rain > 0.85F) {
			return 1;
		}

		return 0;
	}

}
