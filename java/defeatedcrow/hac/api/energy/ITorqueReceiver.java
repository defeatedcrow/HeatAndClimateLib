package defeatedcrow.hac.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * トルクを受け取るTileEntityに実装するもの
 */
public interface ITorqueReceiver extends ITorqueDC {

	boolean canReceiveTorque(float amount, EnumFacing side);

	float receiveTorque(float amount, EnumFacing side, boolean sim);
}
