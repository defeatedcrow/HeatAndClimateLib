package defeatedcrow.hac.core.energy;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.energy.ITorqueProvider;
import defeatedcrow.hac.api.energy.ITorqueReceiver;
import defeatedcrow.hac.api.energy.capability.ITorqueHandler;
import defeatedcrow.hac.api.energy.capability.TorqueCapabilityHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class TorqueHandlerWrapper implements ITorqueHandler, ICapabilityProvider {

	private final TileTorqueBase tile;

	public TorqueHandlerWrapper(TileTorqueBase t) {
		tile = t;
	}

	@Override
	public List<EnumFacing> getOutputSide() {
		List<EnumFacing> list = Lists.newArrayList();
		if (tile instanceof ITorqueProvider) {
			list.addAll(((ITorqueProvider) tile).getOutputSide());
		}
		return list;
	}

	@Override
	public float getTorqueAmount() {
		return tile.getCurrentTorque();
	}

	@Override
	public boolean canProvideTorque(World world, BlockPos outputPos, EnumFacing output) {
		if (tile instanceof ITorqueProvider) {
			return ((ITorqueProvider) tile).canProvideTorque(world, outputPos, output);
		}
		return false;
	}

	@Override
	public float provideTorque(World world, BlockPos outputPos, EnumFacing output, boolean sim) {
		if (tile instanceof ITorqueProvider) {
			return ((ITorqueProvider) tile).provideTorque(world, outputPos, output, sim);
		}
		return 0;
	}

	@Override
	public boolean canProvideTorque(float amount, EnumFacing output) {
		if (tile instanceof ITorqueProvider) {
			return ((ITorqueProvider) tile).getOutputSide().contains(output);
		}
		return false;

	}

	@Override
	public float provideTorque(float amount, EnumFacing output, boolean sim) {
		if (!canProvideTorque(amount, output)) {
			return 0;
		}
		float f = Math.min(amount, tile.prevTorque);
		if (!sim) {
			tile.prevTorque -= f;
			if (tile.prevTorque < 0) {
				tile.prevTorque = 0;
			}
		}
		return f;
	}

	@Override
	public boolean canReceiveTorque(float amount, EnumFacing side) {
		if (tile instanceof ITorqueReceiver) {
			return ((ITorqueReceiver) tile).canReceiveTorque(amount, side);
		}
		return false;
	}

	@Override
	public float receiveTorque(float amount, EnumFacing side, boolean sim) {
		if (tile instanceof ITorqueReceiver) {
			return ((ITorqueReceiver) tile).receiveTorque(amount, side, sim);
		}
		return 0;
	}

	@Override
	public NBTTagCompound writeToNBTTag(NBTTagCompound tag) {
		tile.getNBT(tag);
		return tag;
	}

	@Override
	public void readFromTag(NBTTagCompound tag) {
		tile.setNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == TorqueCapabilityHandler.TORQUE_HANDLER_CAPABILITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == TorqueCapabilityHandler.TORQUE_HANDLER_CAPABILITY ? (T) this : null;
	}

}
