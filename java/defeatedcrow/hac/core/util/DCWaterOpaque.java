package defeatedcrow.hac.core.util;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class DCWaterOpaque {

	private DCWaterOpaque() {
	}

	// waterの透過度をいじってみる
	public static void load() {
		if (CoreConfigDC.waterFix) {
			DCLogger.debugLog("#####");
			Block water = Blocks.WATER;
			Block flow = Blocks.FLOWING_WATER;

			water.setLightOpacity(1);
			int op = water.getLightOpacity(null);
			DCLogger.debugLog("stil water op: " + op);

			flow.setLightOpacity(1);
			int op2 = flow.getLightOpacity(null);
			DCLogger.debugLog("flowing water op: " + op2);

			DCLogger.debugLog("#####");
		}
	}

}
