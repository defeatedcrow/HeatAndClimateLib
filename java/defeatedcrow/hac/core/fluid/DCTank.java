package defeatedcrow.hac.core.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DCTank implements IFluidTank, IFluidTankProperties, IFluidHandler {

	protected FluidStack fluid;
	protected final int capacity;

	public DCTank(int cap) {
		capacity = cap;
	}

	/* tank */

	public boolean isEmpty() {
		return fluid == null || fluid.getFluid() == null;
	}

	public boolean isFull() {
		return (fluid != null) && (fluid.amount == capacity);
	}

	public Fluid getFluidType() {
		return fluid != null ? fluid.getFluid() : null;
	}

	public String getFluidName() {
		return (fluid != null) && (fluid.getFluid() != null) ? fluid.getFluid().getLocalizedName(fluid) : "Empty";
	}

	public DCTank setFluid(FluidStack f) {
		fluid = f;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public void setAmount(int par1) {
		if (fluid != null && fluid.getFluid() != null) {
			fluid.amount = par1;
		}
	}

	@SideOnly(Side.CLIENT)
	public void setFluidById(int par1) {
		Fluid f = FluidRegistry.getFluid(par1);
		if (f != null) {
			fluid = new FluidStack(f, this.getFluidAmount());
		} else {
			fluid = null;
		}
	}

	@Override
	public FluidStack getFluid() {
		return fluid == null ? null : fluid;
	}

	@Override
	public int getFluidAmount() {
		return fluid == null ? 0 : fluid.amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack get, boolean doFill) {
		if (get != null && canFillTarget(get)) {
			int vac = capacity - this.getFluidAmount();
			int ret = Math.min(vac, get.amount);
			if (doFill) {
				if (isEmpty()) {
					fluid = get.copy();
				} else {
					fluid.amount += ret;
				}
			}
			return ret;
		}
		return 0;
	}

	@Override
	public FluidStack drain(int drain, boolean doDrain) {
		if (isEmpty()) {
			int ret = Math.min(drain, fluid.amount);
			if (ret > 0) {
				FluidStack f = new FluidStack(fluid.getFluid(), ret);
				if (doDrain) {
					fluid.amount -= ret;
					if (fluid.amount <= 0) {
						fluid = null;
					}
				}
				return f;
			}
		}
		return null;
	}

	/* property */

	@Override
	public FluidStack getContents() {
		return fluid;
	}

	@Override
	public boolean canFill() {
		return true;
	}

	@Override
	public boolean canDrain() {
		return false;
	}

	@Override
	public boolean canFillFluidType(FluidStack get) {
		return get != null;
	}

	@Override
	public boolean canDrainFluidType(FluidStack get) {
		return get != null;
	}

	/* Handler */

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[] { this };
	}

	@Override
	public FluidStack drain(FluidStack get, boolean doDrain) {
		if (canDrainTarget(get)) {
			return drain(get.amount, doDrain);
		}
		return null;
	}

	/* other method */

	public boolean canFillTarget(FluidStack get) {
		if (get != null) {
			if (isEmpty()) {
				return true;
			} else if (get.isFluidEqual(fluid)) {
				return true;
			}
		}
		return false;
	}

	public boolean canDrainTarget(FluidStack get) {
		if (get != null) {
			if (isEmpty()) {
				return false;
			} else if (get.isFluidEqual(fluid)) {
				return true;
			}
		}
		return false;
	}

}
