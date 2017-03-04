package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.IMillRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
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
		if (this.secondary != null) {
			return this.secondary.copy();
		} else {
			return null;
		}
	}

	@Override
	public float getSecondaryChance() {
		return chance;
	}

	@Override
	public ItemStack getContainerItem(ItemStack item) {
		if (item == null) {
			return null;
		} else if (FluidContainerRegistry.isFilledContainer(item)) {
			return FluidContainerRegistry.drainFluidContainer(item);
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
		if (item != null && item.getItem() != null && !required.isEmpty()) {
			Iterator<ItemStack> itr = required.iterator();
			boolean match = false;
			while (itr.hasNext() && !match) {
				match = OreDictionary.itemMatches(itr.next(), item, false);
			}
			itr = null;
			return match;
		}
		return false;
	}

	@Override
	public boolean matchOutput(List<ItemStack> items, ItemStack in, int slotsize) {
		if (items != null && !items.isEmpty()) {
			int req = 3;
			for (ItemStack get : items) {
				if (getOutput() == null || DCUtil.isStackable(getOutput(), get)) {
					req--;
				}
				if (getSecondary() == null || DCUtil.isStackable(getSecondary(), get)) {
					req--;
				}
				if (getContainerItem(in) == null || DCUtil.isStackable(getContainerItem(in), get)) {
					req--;
				}
			}
			if (items.size() <= slotsize - req) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
