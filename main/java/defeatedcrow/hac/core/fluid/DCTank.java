package defeatedcrow.hac.core.fluid;

import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DCTank implements IFluidTank, IFluidHandler {

	protected FluidStack fluid;
	protected final int capacity;
	protected IFluidTankProperties[] tankProperties;

	public DCTank(int cap) {
		capacity = cap;
	}

	public DCTank readFromNBT(NBTTagCompound nbt, String key) {
		NBTTagList list = nbt.getTagList(key, 10);
		NBTTagCompound nbt2 = list.getCompoundTagAt(0);
		if (!nbt2.hasKey("Empty")) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt2);
			setFluid(fluid);
		} else {
			setFluid(null);
		}
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt, String key) {
		NBTTagList list = new NBTTagList();
		NBTTagCompound nbt2 = new NBTTagCompound();
		if (fluid != null) {
			fluid.writeToNBT(nbt2);
		} else {
			nbt2.setString("Empty", "");
		}
		list.appendTag(nbt2);
		nbt.setTag(key, list);
		return nbt;
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

	public void setAmount(int par1) {
		if (fluid != null && fluid.getFluid() != null) {
			fluid.amount = par1;
		}
	}

	public void setFluidByIdName(String id) {
		FluidStack ret = null;
		if (FluidRegistry.isFluidRegistered(id)) {
			Fluid f = FluidRegistry.getFluid(id);
			ret = new FluidStack(f, getFluidAmount());
		}
		fluid = ret;
	}

	public String getFluidIdName() {
		Fluid fluid = getFluidType();
		if (fluid != null) {
			return fluid.getName();
		}
		return "Empty";
	}

	public void setFluidById(int id) {
		FluidStack ret = null;
		for (Entry<Fluid, Integer> e : FluidRegistry.getRegisteredFluidIDs().entrySet()) {
			if (e.getValue() != null) {
				ret = new FluidStack(e.getKey(), getFluidAmount());
			}
		}
		fluid = ret;
	}

	public int getFluidIdNum() {
		Fluid fluid = getFluidType();
		if (fluid != null) {
			int i = FluidRegistry.getRegisteredFluidIDs().get(fluid);
			return i;
		}
		return -1;
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
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
		if (!isEmpty()) {
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

	/* Handler */

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[] {
				new FluidTankProperties(getFluid(), capacity)
		};
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
