package defeatedcrow.hac.asm;

import java.io.File;
import java.util.Map;

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
		if (data.containsKey("coremodLocation")) {
			file = (File) data.get("coremodLocation");
		}

		if (file == null) {

			file = new File((File) data.get("mcLocation"), "../bin");

			file.mkdir();

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

}
