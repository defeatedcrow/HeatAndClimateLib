package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.ISpinningRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class SpinningRecipe implements ISpinningRecipe {

	private final Object input;
	private final int count;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;

	public SpinningRecipe(ItemStack o, int c, Object i) {
		input = i;
		output = o;
		count = c;
		processedInput = DCUtil.getProcessedList(input, count);
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
	public int getInputCount() {
		return count;
	}

	@Override
	public List<ItemStack> getProcessedInput() {
		return processedInput;
	}

	@Override
	public boolean matchInput(ItemStack item) {
		if (DCUtil.isEmpty(item))
			return false;

		ArrayList<ItemStack> required = new ArrayList<>(DCUtil.getProcessedList(input, count));
		if (!required.isEmpty()) {
			Iterator<ItemStack> itr = required.iterator();
			while (itr.hasNext()) {
				ItemStack next = itr.next();
				if (DCUtil.isIntegratedItem(item, next, false)) {
					if (item.getCount() >= count) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean matchOutput(ItemStack item) {
		if (DCUtil.isEmpty(item))
			return true;

		if (DCUtil.isStackable(item, output))
			return true;
		return false;
	}
}
