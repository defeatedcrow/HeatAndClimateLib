package defeatedcrow.hac.core.plugin.tweaker;

import net.minecraftforge.fml.common.Loader;

public class DCsCTPlugin {

	public static void load() {
		if (Loader.isModLoaded("MineTweaker3")) {
			try {
				CTRecipeRegister.load();
			} catch (Exception e) {

			}
		}
	}
}
