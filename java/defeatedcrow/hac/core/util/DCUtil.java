package defeatedcrow.hac.core.util;

import net.minecraft.util.BlockPos;

// 色々不足しているもの
public class DCUtil {

	public static double getDist(BlockPos p1, BlockPos p2) {
		double x = Math.abs(p1.getX() - p2.getX());
		double y = Math.abs(p1.getY() - p2.getY());
		double z = Math.abs(p1.getZ() - p2.getZ());
		return Math.sqrt(x * x + y * y + z * z);
	}

}
