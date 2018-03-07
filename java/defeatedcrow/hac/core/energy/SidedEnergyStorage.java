package defeatedcrow.hac.core.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class SidedEnergyStorage implements ISidedEnergyStorage {
	protected int energy;
	protected int capacity;
	protected int flowrate;

	protected boolean[] extract = new boolean[] {
			true, true, true, true, true, true, true
	};
	protected boolean[] receive = new boolean[] {
			true, true, true, true, true, true, true
	};

	public SidedEnergyStorage(int cap) {
		this(cap, cap, false);
	}

	public SidedEnergyStorage(int cap, int rate) {
		this(cap, rate, false);
	}

	public SidedEnergyStorage(int cap, int rate, boolean forcedSide) {
		this.capacity = cap;
		this.flowrate = rate;
		extract[6] = !forcedSide;
		receive[6] = !forcedSide;
	}

	public SidedEnergyStorage setExtract(boolean[] bools) {
		extract = bools;
		return this;
	}

	public SidedEnergyStorage setReceive(boolean[] bools) {
		receive = bools;
		return this;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return receiveEnergy(maxReceive, simulate, null);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return extractEnergy(maxExtract, simulate, null);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate, EnumFacing face) {
		if (!canReceive(face)) {
			return 0;
		}
		int limit = Math.min(flowrate, capacity - energy);
		int ret = maxReceive;
		if (ret > limit) {
			ret = limit;
		}

		if (!simulate) {
			energy += ret;
		}
		return ret;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate, EnumFacing face) {
		if (!canExtract(face)) {
			return 0;
		}
		int limit = Math.min(flowrate, energy);
		int ret = maxExtract;
		if (ret > limit) {
			ret = limit;
		}

		if (!simulate) {
			energy += ret;
		}
		return ret;
	}

	@Override
	public int getEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return canExtract(null);
	}

	@Override
	public boolean canReceive() {
		return canReceive(null);
	}

	@Override
	public boolean canExtract(EnumFacing face) {
		if (face == null) {
			return flowrate > 0 && extract[6];
		}
		int id = face.getIndex();
		return flowrate > 0 && extract[id];
	}

	@Override
	public boolean canReceive(EnumFacing face) {
		if (face == null) {
			return receive[6];
		}
		int id = face.getIndex();
		return receive[id];
	}

	@Override
	public boolean[] extractSide() {
		return extract;
	}

	@Override
	public boolean[] receiveSide() {
		return receive;
	}

	public void setExtractSide(EnumFacing face, boolean b) {
		int id = face != null ? face.getIndex() : 6;
		extract[id] = b;
	}

	public void setReceiveSide(EnumFacing face, boolean b) {
		int id = face != null ? face.getIndex() : 6;
		receive[id] = b;
	}

	public void resetExtractSide() {
		extract = new boolean[] {
				true, true, true, true, true, true, true
		};
	}

	public void resetReceiveSide() {
		receive = new boolean[] {
				true, true, true, true, true, true, true
		};
	}

	public SidedEnergyStorage readFromNBT(NBTTagCompound nbt, String key) {
		if (nbt.hasKey("dc.energy_fu")) {
			int e = nbt.getInteger("dc.energy_fu");
			energy = e;
		}
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt, String key) {
		nbt.setInteger("dc.energy_fu", energy);
		return nbt;
	}
}
