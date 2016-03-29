package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.placeable.IEntityItem;
import defeatedcrow.hac.api.recipe.IClimateSmelting;

public class ClimateSmelting implements IClimateSmelting {

	private final Object input;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final float chance;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private List<DCHumidity> hum = new ArrayList<DCHumidity>();
	private List<DCAirflow> air = new ArrayList<DCAirflow>();

	public ClimateSmelting(ItemStack o, ItemStack s, DCHeatTier t, DCHumidity h, DCAirflow a, float c, Object i) {
		input = i;
		output = o;
		secondary = s;
		chance = c;
		if (t != null)
			heat.add(t);
		if (h != null)
			hum.add(h);
		if (a != null)
			air.add(a);
		processedInput = new ArrayList<ItemStack>();
		if (input instanceof String) {
			processedInput.addAll(OreDictionary.getOres((String) input));
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
		return output;
	}

	@Override
	public ItemStack getSecondary() {
		return secondary;
	}

	@Override
	public float getSecondaryChance() {
		return chance;
	}

	@Override
	public ItemStack getContaierItem(ItemStack item) {
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
	public boolean matcheInput(ItemStack item) {
		ArrayList<ItemStack> required = new ArrayList<ItemStack>(this.processedInput);
		if (item != null && item.getItem() != null && !required.isEmpty()) {
			Iterator<ItemStack> itr = required.iterator();
			boolean match = false;
			while (itr.hasNext() && !match) {
				match = OreDictionary.itemMatches(itr.next(), item, false);
			}
			return match;
		}
		return false;
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

}
