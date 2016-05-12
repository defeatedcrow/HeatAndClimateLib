package defeatedcrow.hac.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import defeatedcrow.hac.machine.common.StoveBase;

public class TileRegister {
	private TileRegister() {
	}

	public static void load() {
		GameRegistry.registerTileEntity(StoveBase.class, "dcs_te_core_stove");
	}

}
