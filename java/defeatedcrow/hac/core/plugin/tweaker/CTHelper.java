package defeatedcrow.hac.core.plugin.tweaker;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CTHelper {

	public static ItemStack getStack(IItemStack item) {
		if (item != null) {
			Object obj = item.getInternal();
			if (obj != null && obj instanceof ItemStack) {
				return (ItemStack) obj;
			}
		}
		return null;
	}

	public static Object getObject(IIngredient ing) {
		if (ing != null) {
			if (ing instanceof IOreDictEntry) {
				return ((IOreDictEntry) ing).getName();
			} else if (ing instanceof IItemStack) {
				return getStack((IItemStack) ing);
			}
		}
		return null;
	}

	public static FluidStack getFluid(ILiquidStack liq) {
		if (liq != null) {
			Fluid f = FluidRegistry.getFluid(liq.getName());
			if (liq != null) {
				return new FluidStack(f, liq.getAmount());
			}
		}
		return null;
	}

	public static ItemStack getStacks(IItemStack[] items) {
		if (items != null) {
			ItemStack[] ret = new ItemStack[items.length];
			for (int i = 0; i < items.length; i++) {
				ItemStack item = getStack(items[i]);
				ret[i] = item;
			}
		}
		return null;
	}

	public static ItemStack getStacks(IIngredient[] items) {
		if (items != null) {
			Object[] ret = new Object[items.length];
			for (int i = 0; i < items.length; i++) {
				Object item = getObject(items[i]);
				ret[i] = item;
			}
		}
		return null;
	}

	public static FluidStack[] getFluids(ILiquidStack[] fluids) {
		if (fluids != null) {
			FluidStack[] ret = new FluidStack[fluids.length];
			for (int i = 0; i < ret.length; i++)
				ret[i] = getFluid(fluids[i]);
			return ret;
		}
		return null;
	}

}
