package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.ICrusherRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class CrusherRecipe implements ICrusherRecipe {

	private final Object input;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final ItemStack tertialy;
	private final ItemStack catalyst;
	private final FluidStack outputF;
	private final float chance1;
	private final float chance2;

	public CrusherRecipe(ItemStack o, ItemStack s, float c1, ItemStack t, float c2, FluidStack oF, ItemStack cat,
			Object i) {
		input = i;
		output = o;
		secondary = s;
		chance1 = c1;
		tertialy = t;
		chance2 = c2;
		outputF = oF;
		catalyst = cat;
		processedInput = new ArrayList<ItemStack>();
		if (input instanceof String) {
			List<ItemStack> ret = new ArrayList<ItemStack>();
			ret.addAll(OreDictionary.getOres((String) input));
			processedInput.addAll(ret);
		} else if (input instanceof List && !((List) input).isEmpty()) {
			List<ItemStack> ret = (List<ItemStack>) input;
			processedInput.addAll(ret);
		} else if (input instanceof ItemStack) {
			if (!DCUtil.isEmpty((ItemStack) input))
				processedInput.add(((ItemStack) input).copy());
		} else if (input instanceof Item) {
			processedInput.add(new ItemStack((Item) input, 1, 0));
		} else if (input instanceof Block) {
			processedInput.add(new ItemStack((Block) input, 1, 0));
		} else {
			throw new IllegalArgumentException("Unknown Object passed to recipe!");
		}
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
				int b2 = DCUtil.isEmpty(getOutput()) ? 0 : -1;
				int b3 = DCUtil.isEmpty(getSecondary()) ? 0 : -1;
				int b4 = DCUtil.isEmpty(getTertialy()) ? 0 : -1;
				for (int i = 0; i < target.size(); i++) {
					ItemStack get = target.get(i);
					if (DCUtil.isStackable(getOutput(), get)) {
						b2 = i;
						continue;
					}
					if (DCUtil.isStackable(getSecondary(), get)) {
						b3 = i;
						continue;
					}
					if (DCUtil.isStackable(getTertialy(), get)) {
						b3 = i;
						continue;
					}
				}
				if (target.size() < slotsize - 2) {
					return true;
				} else if (target.size() == slotsize - 2) {
					return b2 >= 0 || b3 >= 0 || b4 >= 0;
				} else {
					if (b2 >= 0 && b3 >= 0 && b4 >= 0) {
						boolean b5 = b2 == 0 || (b2 != b3 && b2 != b4);
						boolean b6 = b3 == 0 || (b3 != b2 && b3 != b4);
						boolean b7 = b4 == 0 || (b4 != b3 && b4 != b2);
						return b5 && b6 && b7;
					}
				}
			} else {
				if (slotsize > 2) {
					return true;
				} else if (slotsize > 0) {
					return DCUtil.isEmpty(getOutput()) || DCUtil.isEmpty(getSecondary());
				} else {
					return DCUtil.isEmpty(getOutput()) && DCUtil.isEmpty(getSecondary());
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
