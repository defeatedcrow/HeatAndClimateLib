package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.ICrusherRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CrusherRecipe implements ICrusherRecipe {

	private final Object input;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final ItemStack tertialy;
	private final ItemStack catalyst;
	private final FluidStack outputF;
	private final float chance0;
	private final float chance1;
	private final float chance2;

	public CrusherRecipe(ItemStack o, float c0, ItemStack s, float c1, ItemStack t, float c2, FluidStack oF,
			ItemStack cat, Object i) {
		input = i;
		output = o;
		chance0 = c0;
		secondary = s;
		chance1 = c1;
		tertialy = t;
		chance2 = c2;
		outputF = oF;
		catalyst = cat;
		processedInput = DCUtil.getProcessedList(input);
	}

	@Override
	public Object getInput() {
		return input;
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
	}

	@Override
	public ItemStack getSecondary() {
		return secondary.copy();
	}

	@Override
	public ItemStack getTertialy() {
		return tertialy.copy();
	}

	@Override
	public ItemStack getCatalyst() {
		return catalyst.copy();
	}

	@Override
	public FluidStack getOutputFluid() {
		return outputF;
	}

	@Override
	public float getChance() {
		return chance0;
	}

	@Override
	public float getSecondaryChance() {
		return chance1;
	}

	@Override
	public float getTertialyChance() {
		return chance2;
	}

	@Override
	public List<ItemStack> getProcessedInput() {
		return processedInput;
	}

	@Override
	public boolean matches(ItemStack in) {
		ArrayList<ItemStack> required = new ArrayList<ItemStack>(this.processedInput);
		if (!DCUtil.isEmpty(in) && !required.isEmpty()) {
			Iterator<ItemStack> itr = required.iterator();
			while (itr.hasNext()) {
				ItemStack next = itr.next();
				if (DCUtil.isIntegratedItem(in, next, false)) {
					if (in.getCount() >= next.getCount()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean matchOutput(List<ItemStack> target, FluidStack fluid, int slotsize) {
		boolean b1 = false;
		if (this.outputF == null || fluid == null) {
			b1 = true;
		} else {
			b1 = outputF.getFluid() == fluid.getFluid();
		}

		if (b1) {
			if (target != null && !target.isEmpty()) {
				int b2 = DCUtil.isEmpty(getOutput()) ? -1 : -2;
				int b3 = DCUtil.isEmpty(getSecondary()) ? -1 : -2;
				int b4 = DCUtil.isEmpty(getTertialy()) ? -1 : -2;
				for (int i = 0; i < target.size(); i++) {
					ItemStack get = target.get(i);
					if (b2 < -1 && DCUtil.isStackable(getOutput(), get)) {
						b2 = i;
						continue;
					}
					if (b3 < -1 && DCUtil.isStackable(getSecondary(), get)) {
						b3 = i;
						continue;
					}
					if (b4 < -1 && DCUtil.isStackable(getTertialy(), get)) {
						b4 = i;
						continue;
					}
				}
				if (target.size() < slotsize - 2) {
					return true;
				} else if (target.size() == slotsize - 2) {
					return b2 > -2 || b3 > -2 || b4 > -2;
				} else {
					if (b2 > -2 || b3 > -2 || b4 > -2) {
						boolean b5 = b2 == -1 || (b2 != b3 && b2 != b4);
						boolean b6 = b3 == -1 || (b3 != b2 && b3 != b4);
						boolean b7 = b4 == -1 || (b4 != b3 && b4 != b2);
						return b5 && b6 && b7;
					}
				}
			} else {
				if (slotsize > 2) {
					return true;
				} else if (slotsize > 1) {
					return DCUtil.isEmpty(getOutput()) || DCUtil.isEmpty(getSecondary());
				} else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean matchCatalyst(ItemStack cat) {
		return DCUtil.isEmpty(catalyst) || DCUtil.isSameItem(cat, catalyst, false);
	}
}
