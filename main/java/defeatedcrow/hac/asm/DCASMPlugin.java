package defeatedcrow.hac.asm;

import java.io.File;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.12.2")
public class DCASMPlugin implements IFMLLoadingPlugin, IFMLCallHook {

	static File file;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
				"defeatedcrow.hac.asm.DCMethodTransformer"
		};
	}

	@Override
	public String getModContainerClass() {
		return "defeatedcrow.hac.asm.DCASMCore";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		if (data.containsKey("mcLocation")) {
			file = (File) data.get("mcLocation");
		}

		if (file != null) {
			File config = new File(file, "config/defeatedcrow/climate/dcs_asm.cfg");
			loadConfig(config);
			// LogManager.getLogger("dcs_asm").debug("loaded config:" + config.toPath().toString());
		} else {
			// LogManager.getLogger("dcs_asm").debug("failed to load config");
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public Void call() throws Exception {
		return null;
	}

	private void loadConfig(File configFile) {
		ClimateAsmConfig.INSTANCE.load(new Configuration(configFile));
	}

}
