package defeatedcrow.hac.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DCLogger {
	public static final Logger LOGGER = LogManager.getLogger("defeatedcrow");

	public static Logger logger() {
		return LOGGER;
	}

	public static void debugLog(String s) {
		if (ClimateCore.isDebug) {
			LOGGER.info(s);
		}
	}

	public static void debugLog(String id, String s) {
		if (ClimateCore.isDebug) {
			LOGGER.info(id + ": " + s);
		}
	}

	public static void infoLog(String id, String s) {
		LOGGER.info(id + ": " + s);
	}

}
