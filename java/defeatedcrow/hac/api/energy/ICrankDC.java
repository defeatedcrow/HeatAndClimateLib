package defeatedcrow.hac.api.energy;

import net.minecraft.util.EnumFacing;

public interface ICrankDC {

	/*
	 * + -> 押す
	 * - -> 引く
	 */
	float outputPower();

	// 動力を出力できるか
	boolean isOutputSide(EnumFacing side);
}
