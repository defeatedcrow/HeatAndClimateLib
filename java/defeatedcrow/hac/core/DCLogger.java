package defeatedcrow.hac.core;

import org.apache.logging.log4j.Logger;

public class DCLogger {

	public static Logger logger() {
		return ClimateCore.LOGGER;
	}

	public static void debugLog(String s) {
		if (ClimateCore.isDebug) {
			ClimateCore.LOGGER.info(s);
		}
	}

	public static void debugLog(String id, String s) {
		if (ClimateCore.isDebug) {
			ClimateCore.LOGGER.info(id + ": " + s);
		}
	}

	public static void infoLog(String id, String s) {
		ClimateCore.LOGGER.info(id + ": " + s);
	}

	public static void infoLog(String s) {
		ClimateCore.LOGGER.info(s);
	}

	public static void warnLog(String s) {
		ClimateCore.LOGGER.warn(s);
	}

}
