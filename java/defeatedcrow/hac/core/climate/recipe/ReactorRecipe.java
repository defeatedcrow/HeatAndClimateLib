package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.IReactorRecipe;
import defeatedcrow.hac.api.recipe.IRecipePanel;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ReactorRecipe implements IReactorRecipe {

	private final Object[] input;
	private final FluidStack inputF1;
	private final FluidStack inputF2;
	private ArrayList<Object> processedInput = new ArrayList<Object>();
	private ArrayList<Object> inputList = new ArrayList<Object>();
	private final ItemStack output;
	private final FluidStack outputF1;
	private final FluidStack outputF2;
	private final ItemStack secondary;
	private final float chance;
	private final ItemStack catalyst;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private String type = "";
	private static final ArrayList<Object> EMPTY = new ArrayList<Object>();
	private final int count;

	public ReactorRecipe(ItemStack o, ItemStack s, FluidStack oF1, FluidStack oF2, DCHeatTier t, float c, ItemStack cat,
			FluidStack iF1, FluidStack iF2, Object... inputs) {
		input = inputs;
		inputF1 = iF1;
		inputF2 = iF2;
		output = o;
		outputF1 = oF1;
		outputF2 = oF2;
		secondary = s;
		chance = c;
		catalyst = cat;
		if (t != null) {
			heat.add(t);
			if (t.getID() < DCHeatTier.INFERNO.getID()) {
				if (t.getID() == DCHeatTier.NORMAL.getID() || t.getID() == DCHeatTier.WARM.getID()) {
					heat.add(t.addTier(1));
					heat.add(t.addTier(-1));
				} else if (t.getID() > 0 && t.getID() < DCHeatTier.NORMAL.getID()) {
					heat.add(t.addTier(-1));
				} else if (t.getID() > DCHeatTier.WARM.getID()) {
					heat.add(t.addTier(1));
				}
			}
		}
		if (inputs != null && input.length > 0) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof String) {
					List<ItemStack> ret = new ArrayList<ItemStack>();
					ret.addAll(OreDictionary.getOres((String) inputs[i]));
					processedInput.add(ret);
					inputList.add(inputs[i]);
				} else if (inputs[i] instanceof ItemStack) {
					ItemStack ret = ((ItemStack) inputs[i]).copy();
					processedInput.add(ret);
					inputList.add(ret);
				} else if (inputs[i] instanceof Item) {
					ItemStack ret = new ItemStack((Item) inputs[i], 1, 0);
					processedInput.add(ret);
					inputList.add(ret);
				} else if (inputs[i] instanceof Block) {
					ItemStack ret = new ItemStack((Block) inputs[i], 1, 0);
					processedInput.add(ret);
					inputList.add(ret);
				} else {
					throw new IllegalArgumentException("Unknown Object passed to recipe!");
				}
			}
			count = input.length;
		} else {
			count = 0;
		}
	}

	@Override
	public Object[] getInput() {
		return input;
	}

	@Override
	public ItemStack getOutput() {
		return output == null ? null : output.copy();
	}

	@Override
	public ItemStack getSecondary() {
		if (!DCUtil.isEmpty(secondary)) {
			return this.secondary.copy();
		} else {
			List<ItemStack> ret = getContainerItems(processedInput);
			if (ret != null && !ret.isEmpty()) {
				return ret.get(0);
			}
			return null;
		}
	}

	@Override
	public float getSecondaryChance() {
		return chance;
	}

	@Override
	public FluidStack getInputFluid() {
		return this.inputF1;
	}

	@Override
	public FluidStack getOutputFluid() {
		return this.outputF1;
	}

	@Override
	public ItemStack getCatalyst() {
		return catalyst;
	}

	@Override
	public FluidStack getSubInputFluid() {
		return inputF2;
	}

	@Override
	public FluidStack getSubOutputFluid() {
		return outputF2;
	}

	@Override
	public List<ItemStack> getContainerItems(List<Object> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) instanceof ItemStack) {
				ItemStack next = (ItemStack) items.get(i);
				ItemStack cont = null;
				if (!DCUtil.isEmpty(next)) {
					cont = next.getItem().getContainerItem(next);
					if (!DCUtil.isEmpty(cont)) {
						list.add(cont);
					} else {
						cont = FluidContainerRegistry.drainFluidContainer(next);
						if (!DCUtil.isEmpty(cont)) {
							list.add(cont);
						}
					}
				}
			}
		}

		return list;
	}

	@Override
	public List<Object> getProcessedInput() {
		if (processedInput == null || this.processedInput.isEmpty()) {
			return EMPTY;
		} else {
			return new ArrayList<Object>(this.processedInput);
		}
	}

	@Override
	public int getRecipeSize() {
		if (input != null)
			return input.length;
		return 0;
	}

	@Override
	public boolean matches(List<ItemStack> items, FluidStack fluid1, FluidStack fluid2) {
		boolean b1 = false;
		if (this.inputF1 == null) {
			b1 = true;
		} else if (fluid1 != null) {
			if (inputF1.getFluid() == fluid1.getFluid()
					|| FluidDictionaryDC.matchFluid(fluid1.getFluid(), inputF1.getFluid())) {
				b1 = inputF1.amount <= fluid1.amount;
			}
		}

		boolean b2 = false;
		if (this.inputF2 == null) {
			b2 = true;
		} else if (fluid2 != null) {
			if (inputF2.getFluid() == fluid2.getFluid()
					|| FluidDictionaryDC.matchFluid(fluid2.getFluid(), inputF2.getFluid())) {
				b2 = inputF2.amount <= fluid2.amount;
			}
		}

		if (b1 && b2) {
			// DCLogger.debugLog("1: fluid match");
			ArrayList<Object> required = new ArrayList<Object>(this.inputList);
			if (required.isEmpty())
				return true;

			for (int x = 0; x < items.size(); x++) {
				ItemStack slot = items.get(x);

				if (slot != null) {
					boolean inRecipe = false;
					Iterator<Object> req = required.iterator();

					if (slot.getItem() instanceof IRecipePanel) {
						inRecipe = true;
						continue;
					}

					while (req.hasNext()) {
						boolean match = false;

						Object next = req.next();
						if (next == null) {
							continue;
						}

						if (next instanceof ItemStack) {
							// DCLogger.debugLog("target: item");
							match = DCUtil.isSameItem((ItemStack) next, slot, false);
						} else if (next instanceof String) {
							// DCLogger.debugLog("target: string " + "[" + (String) next + "]");
							int target = OreDictionary.getOreID((String) next);
							int[] ids = OreDictionary.getOreIDs(slot);
							for (int i : ids) {
								if (i == target) {
									match = true;
								}
							}
						}

						if (match) {
							inRecipe = true;
							required.remove(next);
							break;
						}
					}

					req = null;

					if (!inRecipe) {
						return false;
					}
				}
			}
			// if (required.isEmpty())
			// DCLogger.debugLog("2: item match");
			return required.isEmpty();
		} else {
			return false;
		}
	}

	@Override
	public boolean matchOutput(List<ItemStack> items, FluidStack fluid1, FluidStack fluid2, int slotsize) {
		boolean b1 = false;
		if (this.outputF1 == null) {
			b1 = true;
		} else if (fluid1 != null) {
			b1 = (outputF1.getFluid() == fluid1.getFluid());
		}

		boolean b2 = false;
		if (this.inputF2 == null) {
			b2 = true;
		} else if (fluid2 != null) {
			b2 = (outputF2.getFluid() == fluid2.getFluid());
		}

		if (b1 && b2) {
			if (items != null && !items.isEmpty()) {
				boolean b4 = false;
				boolean b3 = false;
				for (ItemStack get : items) {
					if (!DCUtil.isEmpty(getOutput()) || DCUtil.isStackable(getOutput(), get)) {
						b4 = true;
					}
					if (!DCUtil.isEmpty(getSecondary()) || DCUtil.isStackable(getSecondary(), get)) {
						b3 = true;
					}
				}
				if (items.size() < slotsize - 1) {
					return true;
				} else if (items.size() == slotsize - 1) {
					return b4 || b3;
				} else {
					return b4 && b3;
				}
			} else {
				if (slotsize > 1) {
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
		if (DCUtil.isEmpty(catalyst) || DCUtil.isSameItem(catalyst, cat, false)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean matchHeatTier(int id) {
		DCHeatTier tier = DCHeatTier.getTypeByID(id);
		return matchHeatTier(tier);
	}

	@Override
	public boolean matchHeatTier(DCHeatTier tier) {
		boolean t = requiredHeat().isEmpty() || requiredHeat().contains(tier);
		return t;
	}

	@Override
	public boolean additionalRequire(World world, BlockPos pos) {
		return true;
	}

	@Override
	public List<DCHeatTier> requiredHeat() {
		return heat;
	}

	@Override
	public String additionalString() {
		return type;
	}

	@Override
	public int recipeCoincidence() {
		return count;
	}
}
