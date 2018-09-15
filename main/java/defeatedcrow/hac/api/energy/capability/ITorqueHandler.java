package defeatedcrow.hac.api.energy.capability;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITorqueHandler {

	List<EnumFacing> getOutputSide();

	float getTorqueAmount();

	boolean canProvideTorque(World world, BlockPos outputPos, EnumFacing output);

	/**
	 * マシンの持つoutput方向への出力処理を呼ぶ
	 */
	float provideTorque(World world, BlockPos outputPos, EnumFacing output, boolean sim);

	boolean canProvideTorque(float amount, EnumFacing output);

	/**
	 * マシンからトルクを吸い出す処理
	 */
	float provideTorque(float amount, EnumFacing output, boolean sim);

	boolean canReceiveTorque(float amount, EnumFacing input);

	/**
	 * トルクを受け入れる処理
	 */
	float receiveTorque(float amount, EnumFacing input, boolean sim);

	NBTTagCompound writeToNBTTag(NBTTagCompound tag);

	void readFromTag(NBTTagCompound tag);

}
