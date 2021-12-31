package defeatedcrow.hac.core.util;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

public class DCWaterOpaque {

	private DCWaterOpaque() {}

	// waterの透過度をいじってみる
	public static void load() {
		if (CoreConfigDC.waterFix > 0D) {
			DCLogger.debugLog("#####");
			Block water = Blocks.WATER;
			Block flow = Blocks.FLOWING_WATER;

			int i = MathHelper.floor(CoreConfigDC.waterFix * 255D);
			if (i < 0 || i > 255) {
				i = 1;
			}

			if (water != null) {
				water.setLightOpacity(1);
				int op = Blocks.WATER.getDefaultState().getLightOpacity();
				DCLogger.debugLog("stil water op: " + op);
			}

			if (flow != null) {
				flow.setLightOpacity(1);
				int op2 = Blocks.FLOWING_WATER.getDefaultState().getLightOpacity();
				DCLogger.debugLog("flowing water op: " + op2);
			}

			DCLogger.debugLog("#####");
		}
	}

}
