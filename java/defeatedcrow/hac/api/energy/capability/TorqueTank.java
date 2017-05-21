package defeatedcrow.hac.api.energy.capability;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.energy.ITorqueReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * CapabilityHandler登録時のCallable登録のための仮タンク<br>
 * HaC本体はwrapperを使用していて、これは未使用<br>
 * 方向特性なし
 */
public class TorqueTank implements ITorqueHandler {

	public float torque;
	public final float maxTorque;

	public TorqueTank(float max) {
		maxTorque = max;
	}

	public TorqueTank() {
		this(32F);
	}

	@Override
	public List<EnumFacing> getOutputSide() {
		List<EnumFacing> list = Lists.newArrayList();
		return list;
	}

	@Override
	public float getTorqueAmount() {
		return torque;
	}

	@Override
	public boolean canProvideTorque(World world, BlockPos outputPos, EnumFacing output) {
		TileEntity tile = world.getTileEntity(outputPos);
		float amo = torque;
		if (tile != null && tile instanceof ITorqueReceiver && amo > 0F)
			return ((ITorqueReceiver) tile).canReceiveTorque(amo, output.getOpposite());
		return false;
	}

	@Override
	public float provideTorque(World world, BlockPos outputPos, EnumFacing output, boolean sim) {
		float amo = torque;
		if (canProvideTorque(world, outputPos, output)) {
			ITorqueReceiver target = (ITorqueReceiver) world.getTileEntity(outputPos);
			float ret = target.receiveTorque(amo, output, sim);
			if (!sim) {
				torque -= ret;
				if (torque < 0) {
					torque = 0;
				}
			}
			return ret;
		}
		return 0;
	}

	@Override
	public boolean canProvideTorque(float amount, EnumFacing output) {
		return true;

	}

	@Override
	public float provideTorque(float amount, EnumFacing output, boolean sim) {
		if (!canProvideTorque(amount, output)) {
			return 0;
		}
		float f = Math.min(maxTorque, torque);
		if (!sim) {
			torque -= f;
			if (torque < 0) {
				torque = 0;
			}
		}
		return f;
	}

	@Override
	public boolean canReceiveTorque(float amount, EnumFacing side) {
		return true;
	}

	@Override
	public float receiveTorque(float amount, EnumFacing input, boolean sim) {
		if (!canReceiveTorque(amount, input)) {
			return 0;
		}
		float f = Math.min(amount, maxTorque - torque);
		if (!sim) {
			torque += f;
			if (torque > maxTorque) {
				torque = maxTorque;
			}
		}
		return f;
	}

	@Override
	public NBTTagCompound writeToNBTTag(NBTTagCompound tag) {
		tag.setFloat("torque_tank", torque);
		return null;
	}

	@Override
	public void readFromTag(NBTTagCompound tag) {
		torque = tag.getFloat("torque_tank");
	}

}
