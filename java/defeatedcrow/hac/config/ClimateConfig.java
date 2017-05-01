package defeatedcrow.hac.config;

import java.io.File;

import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.fluid.FluidIDRegisterDC;
import net.minecraftforge.common.config.Configuration;

public class ClimateConfig {

	private ClimateConfig() {}

	public static final ClimateConfig INSTANCE = new ClimateConfig();

	public void load(File file) {

		File cfgFile = new File(file, "defeatedcrow/climate/core.cfg");
		CoreConfigDC.INSTANCE.load(new Configuration(cfgFile));

		// fluid
		FluidIDRegisterDC.setDir(file);
		FluidIDRegisterDC.pre();

		// resistant data
		MobResistantRegister.setDir(file);
		ArmorResistantRegister.setDir(file);
	}

}
