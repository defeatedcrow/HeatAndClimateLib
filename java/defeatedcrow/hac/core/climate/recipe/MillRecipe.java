package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipe;
import defeatedcrow.hac.core.fluid.DCFluidUtil;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
		processedInput = new ArrayList<ItemStack>();
		if (input instanceof String) {
			List<ItemStack> ret = new ArrayList<ItemStack>();
			ret.addAll(OreDictionary.getOres((String) input));
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
		ArrayList<ItemStack> required = new ArrayList<ItemStack>(this.processedInput);
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
	public boolean matchOutput(List<ItemStack> items, ItemStack in, int slotsize) {
		// DCLogger.debugLog("in stonemill debug 2");
		if (items != null && !items.isEmpty()) {
			int req = 3;
			for (ItemStack get : items) {
				if (DCUtil.isEmpty(getOutput()) || DCUtil.isStackable(getOutput(), get)) {
					req--;
				}
				if (DCUtil.isEmpty(getSecondary()) || DCUtil.isStackable(getSecondary(), get)) {
					req--;
				}
				if (DCUtil.isEmpty(getContainerItem(in)) || DCUtil.isStackable(getContainerItem(in), get)) {
					req--;
				}
			}
			if (items.size() <= slotsize - req) {
				// DCLogger.debugLog("clear 2");
				return true;
			} else {
				return false;
			}
		} else {
			// DCLogger.debugLog("clear 2");
			return true;
		}
	}
}
