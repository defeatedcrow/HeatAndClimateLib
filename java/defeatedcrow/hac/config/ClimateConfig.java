package defeatedcrow.hac.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import defeatedcrow.hac.core.fluid.FluidIDRegisterDC;

public class ClimateConfig {

	private ClimateConfig() {
	}

	public static final ClimateConfig INSTANCE = new ClimateConfig();

	public void load(File file) {

		File cfgFile = new File(file, "defeatedcrow/climate/core.cfg");
		CoreConfigDC.INSTANCE.load(new Configuration(cfgFile));

		// fluid
		FluidIDRegisterDC.setDir(file);
		FluidIDRegisterDC.pre();
	}

}
