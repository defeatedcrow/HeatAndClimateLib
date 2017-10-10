package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.recipe.ISpinningRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SpinningRecipe implements ISpinningRecipe {

	private final Object input;
	private final int count;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;

	public SpinningRecipe(ItemStack o, int c, Object i) {
		input = i;
		output = o;
		count = c;
		processedInput = new ArrayList<ItemStack>();
		if (input instanceof String) {
			List<ItemStack> ret = new ArrayList<ItemStack>();
			ret.addAll(OreDictionary.getOres((String) input));
			for (ItemStack r : ret) {
				if (!DCUtil.isEmpty(r)) {
					processedInput.add(new ItemStack(r.getItem(), count, r.getItemDamage()));
				}
			}
		} else if (input instanceof ItemStack) {
			processedInput
					.add((new ItemStack(((ItemStack) input).getItem(), count, ((ItemStack) input).getItemDamage())));
		} else if (input instanceof Item) {
			processedInput.add(new ItemStack((Item) input, count, 0));
		} else if (input instanceof Block) {
			processedInput.add(new ItemStack((Block) input, count, 0));
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

		if (input instanceof String) {
			String inputName = (String) input;
			int[] ids = OreDictionary.getOreIDs(item);
			if (OreDictionary.doesOreNameExist(inputName) && ids != null) {
				for (int i : ids) {
					if (i == OreDictionary.getOreID(inputName) && item.getCount() >= count)
						return true;
				}
			}
		} else {
			ArrayList<ItemStack> required = new ArrayList<ItemStack>(this.processedInput);
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
