package defeatedcrow.hac.api.energy;

import net.minecraft.util.EnumFacing;

public interface ITorqueReceiver {

	boolean canReceiveTorque(float amount, EnumFacing side);

	float receiveTorque(float amount, EnumFacing side, boolean sim);
}
