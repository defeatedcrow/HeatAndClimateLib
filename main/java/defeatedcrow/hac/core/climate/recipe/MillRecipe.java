package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipe;
import defeatedcrow.hac.core.fluid.DCFluidUtil;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class MillRecipe implements IMillRecipe {

	private final Object input;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final float chance;

	public MillRecipe(ItemStack o, ItemStack s, float c, Object i) {
		input = i;
		output = o;
		secondary = s;
		chance = c;
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
		if (!DCUtil.isEmpty(secondary)) {
			return this.secondary.copy();
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public float getSecondaryChance() {
		return chance;
	}

	@Override
	public ItemStack getContainerItem(ItemStack item) {
		if (DCUtil.isEmpty(item)) {
			return ItemStack.EMPTY;
		} else if (!DCUtil.isEmpty(DCFluidUtil.getEmptyCont(item))) {
			return DCFluidUtil.getEmptyCont(item);
		} else {
			return item.getItem().getContainerItem(item);
		}
	}

	@Override
	public List<ItemStack> getProcessedInput() {
		return processedInput;
	}

	@Override
	public boolean matchInput(ItemStack item) {
		ArrayList<ItemStack> required = new ArrayList<>(DCUtil.getProcessedList(input));
		if (!DCUtil.isEmpty(item) && !required.isEmpty()) {
			// DCLogger.debugLog("in stonemill debug");
			Iterator<ItemStack> itr = required.iterator();
			while (itr.hasNext()) {
				ItemStack next = itr.next();
				if (DCUtil.isIntegratedItem(item, next, false)) {
					if (item.getCount() >= next.getCount()) {
						// DCLogger.debugLog("clear 1");
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean matchOutput(List<ItemStack> target, ItemStack in, int slotsize) {
		// DCLogger.debugLog("in stonemill debug 2");
		if (target != null && !target.isEmpty()) {
			int b2 = DCUtil.isEmpty(getOutput()) ? -1 : -2;
			int b3 = DCUtil.isEmpty(getSecondary()) ? -1 : -2;
			for (int i = 0; i < target.size(); i++) {
				ItemStack get = target.get(i);
				if (b2 < -1 && DCUtil.canInsert(getOutput(), get)) {
					b2 = i;
					continue;
				}
				if (b3 < -1 && DCUtil.canInsert(getSecondary(), get)) {
					b3 = i;
					continue;
				}
			}
			return b2 > -2 && b3 > -2 && b2 != b3;
		} else {
			if (slotsize > 1) {
				return true;
			} else if (slotsize > 0) {
				return DCUtil.isEmpty(getOutput()) || DCUtil.isEmpty(getSecondary());
			} else {
				return false;
			}
		}
	}
}
