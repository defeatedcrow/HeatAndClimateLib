package defeatedcrow.hac.core.plugin.main;

import java.io.File;

import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.main.config.MainConfig;

public class MainComHelper {

	public static boolean reloadMainConfig() {
		if (!ClimateCore.loadedMain)
			return false;

		File dir = new File(ClimateConfig.configDir, "main.cfg");
		if (dir.exists()) {
			MainConfig.INSTANCE.load(ClimateConfig.configDir);
			return true;
		}
		return false;
	}

}
