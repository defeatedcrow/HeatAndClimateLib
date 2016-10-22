package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.placeable.IEntityItem;
import defeatedcrow.hac.api.recipe.IClimateRecipe;
import defeatedcrow.hac.api.recipe.IRecipePanel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ClimateRecipe implements IClimateRecipe {

	private final Object[] input;
	private ArrayList<Object> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final float chance;
	private final boolean needCooling;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private List<DCHumidity> hum = new ArrayList<DCHumidity>();
	private List<DCAirflow> air = new ArrayList<DCAirflow>();

	public ClimateRecipe(ItemStack o, ItemStack s, DCHeatTier t, DCHumidity h, DCAirflow a, float c, boolean cooling,
			Object... inputs) {
		input = inputs;
		output = o;
		secondary = s;
		chance = c;
		needCooling = cooling;
		if (t != null) {
			heat.add(t);
			heat.add(t);
			if (t.getID() < 11) {
				if (t.getID() == 4 || t.getID() == 5) {
					heat.add(t.addTier(1));
					heat.add(t.addTier(-1));
				} else if (t.getID() > 0 && t.getID() < 4) {
					heat.add(t.addTier(-1));
				} else if (t.getID() > 0) {
					heat.add(t.addTier(1));
				}
			}
		}
		if (h != null)
			hum.add(h);
		if (a != null)
			air.add(a);
		processedInput = new ArrayList<Object>();
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i] instanceof String) {
				processedInput.add(OreDictionary.getOres((String) inputs[i]));
			} else if (inputs[i] instanceof ItemStack) {
				processedInput.add(((ItemStack) inputs[i]).copy());
			} else if (inputs[i] instanceof Item) {
				processedInput.add(new ItemStack((Item) inputs[i], 1, 0));
			} else if (inputs[i] instanceof Block) {
				processedInput.add(new ItemStack((Block) inputs[i], 1, 0));
			} else {
				throw new IllegalArgumentException("Unknown Object passed to recipe!");
			}
		}
	}

	@Override
	public Object[] getInput() {
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
	public List<ItemStack> getContainerItems(List<ItemStack> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < items.size(); i++) {
			ItemStack next = items.get(i);
			ItemStack cont = null;
			if (next != null) {
				cont = next.getItem().getContainerItem(next);
				if (cont != null) {
					list.add(cont);
				} else {
					cont = FluidContainerRegistry.drainFluidContainer(next);
					if (cont != null) {
						list.add(cont);
					}
				}
			}
		}

		return list;
	}

	@Override
	public List<Object> getProcessedInput() {
		return processedInput;
	}

	@Override
	public boolean matches(List<ItemStack> items) {
		ArrayList<Object> required = new ArrayList<Object>(this.processedInput);

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

					if (next instanceof ItemStack) {
						match = OreDictionary.itemMatches((ItemStack) next, slot, false);
					} else if (next instanceof ArrayList) {
						Iterator<ItemStack> itr = ((ArrayList<ItemStack>) next).iterator();
						while (itr.hasNext() && !match) {
							match = OreDictionary.itemMatches(itr.next(), slot, false);
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
		return required.isEmpty();
	}

	@Override
	public boolean matchClimate(int code) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return matchClimate(clm);
	}

	@Override
	public boolean matchClimate(IClimate climate) {
		boolean t = requiredHeat().isEmpty() || requiredHeat().contains(climate.getHeat());
		boolean h = requiredHum().isEmpty() || requiredHum().contains(climate.getHumidity());
		boolean a = requiredAir().isEmpty() || requiredAir().contains(climate.getAirflow());
		return t && h && a;
	}

	@Override
	public boolean additionalRequire(World world, BlockPos pos) {
		if (isNeedCooling()) {
			return ClimateAPI.calculator.getCold(world, pos, 1, false).getID() <= 0;
		}
		return true;
	}

	@Override
	public int hasPlaceableOutput() {
		if (output.getItem() instanceof IEntityItem) {
			return 2;
		} else if (output.getItem() instanceof ItemBlock) {
			return 1;
		}
		return 0;
	}

	@Override
	public List<DCHeatTier> requiredHeat() {
		return heat;
	}

	@Override
	public List<DCHumidity> requiredHum() {
		return hum;
	}

	@Override
	public List<DCAirflow> requiredAir() {
		return air;
	}

	@Override
	public int getRecipeSize() {
		return input.length;
	}

	@Override
	public boolean isNeedCooling() {
		return needCooling;
	}

}
