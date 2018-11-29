package defeatedcrow.hac.core.fluid;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fluids.Fluid;

public class FluidDic {

	public final String dicName;
	public final List<Fluid> fluids = Lists.newArrayList();

	public FluidDic(String name) {
		dicName = name;
	}

	public boolean match(Fluid fluid) {
		if (fluid == null)
			return false;

		if (!fluids.isEmpty() && fluids.contains(fluid)) {
			return true;
		} else if (fluid.getName().contains(dicName)) {
			if (!dicName.equalsIgnoreCase("milk"))
				return true;
		}

		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FluidDic) {
			FluidDic f = (FluidDic) obj;
			return f.dicName.hashCode() == dicName.hashCode();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return dicName == null ? 0 : dicName.hashCode();
	}

	@Override
	public String toString() {
		String name = dicName == null ? "null" : dicName;
		return "Dic: " + dicName;
	}

}
