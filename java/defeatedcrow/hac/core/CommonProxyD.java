package defeatedcrow.hac.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import defeatedcrow.hac.core.worldgen.WorldGenOres;

public class CommonProxyD {

	public void loadMaterial() {
		MaterialRegister.load();
	}

	public void loadTE() {
		TileRegister.load();
	}

	public void loadWorldGen() {
		// gen
		GameRegistry.registerWorldGenerator(new WorldGenOres(), 2);
	}

}
