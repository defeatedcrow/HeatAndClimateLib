package defeatedcrow.hac.api.energy;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITorqueProvider {

	EnumFacing getOutputSide();

	float getAmount();

	boolean canProvideTorque(World world, BlockPos outputPos, EnumFacing output);

	float provideTorque(World world, BlockPos outputPos, EnumFacing output, boolean sim);

}
