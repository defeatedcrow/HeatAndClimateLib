package defeatedcrow.hac.api.energy.capability;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class TorqueCapabilityHandler {

	private TorqueCapabilityHandler() {}

	public static final TorqueCapabilityHandler INSTANCE = new TorqueCapabilityHandler();

	@CapabilityInject(ITorqueHandler.class)
	public static Capability<ITorqueHandler> TORQUE_HANDLER_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(ITorqueHandler.class, INSTANCE.new StorageDC(), INSTANCE.new CallableDC());
	}

	public class StorageDC implements Capability.IStorage<ITorqueHandler> {

		@Override
		public NBTBase writeNBT(Capability<ITorqueHandler> capability, ITorqueHandler instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			instance.writeToNBTTag(nbt);
			return nbt;
		}

		@Override
		public void readNBT(Capability<ITorqueHandler> capability, ITorqueHandler instance, EnumFacing side,
				NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			instance.readFromTag(tag);
		}

	}

	public class CallableDC implements Callable<ITorqueHandler> {

		@Override
		public ITorqueHandler call() throws Exception {
			return new TorqueTank();
		}

	}

}
