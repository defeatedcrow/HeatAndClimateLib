package defeatedcrow.hac.core.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class SidedEnergyWrapper implements IEnergyStorage {

	protected final SidedEnergyStorage tank;
	protected final boolean canExtract;
	protected final boolean canReceive;

	public SidedEnergyWrapper(SidedEnergyStorage storage, boolean ext, boolean rec) {
		tank = storage;
		canExtract = ext;
		canReceive = rec;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive)
			return 0;

		return tank.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract)
			return 0;

		return tank.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return tank.energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return tank.capacity;
	}

	@Override
	public boolean canExtract() {
		return tank.canExtract();
	}

	@Override
	public boolean canReceive() {
		return tank.canReceive();
	}
}
