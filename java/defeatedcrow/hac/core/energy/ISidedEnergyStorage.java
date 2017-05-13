package defeatedcrow.hac.core.energy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * sided energy interface for FU
 */
public interface ISidedEnergyStorage extends IEnergyStorage {

	int receiveEnergy(int maxReceive, boolean simulate, EnumFacing face);

	int extractEnergy(int maxExtract, boolean simulate, EnumFacing face);

	boolean canExtract(EnumFacing face);

	boolean canReceive(EnumFacing face);

	boolean[] extractSide();

	boolean[] receiveSide();

}
