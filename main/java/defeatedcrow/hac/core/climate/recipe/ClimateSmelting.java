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
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.core.fluid.DCFluidUtil;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ClimateSmelting implements IClimateSmelting {

	private final Object input;
	private ArrayList<ItemStack> processedInput;
	private final ItemStack output;
	private final ItemStack secondary;
	private final float chance;
	private final boolean needCooling;
	private boolean dropItem = false;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private List<DCHumidity> hum = new ArrayList<DCHumidity>();
	private List<DCAirflow> air = new ArrayList<DCAirflow>();

	public ClimateSmelting(ItemStack o, ItemStack s, DCHeatTier t, DCHumidity h, DCAirflow a, float c, boolean cooling,
			Object i) {
		input = i;
		output = o;
		secondary = s;
		chance = c;
		needCooling = cooling;
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
		if (h != null)
			hum.add(h);
		if (a != null)
			air.add(a);
		processedInput = DCUtil.getProcessedList(input);
		if (!processedInput.isEmpty()) {
			ItemStack check = processedInput.get(0);
			if (!DCUtil.isEmpty(check) && !(check.getItem() instanceof ItemBlock) && !(check
					.getItem() instanceof IEntityItem)) {
				dropItem = true;
			}
		}
	}

	public ClimateSmelting setProceedAsDropItem() {
		dropItem = true;
		return this;
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
	public boolean matcheInput(ItemStack item) {
		ArrayList<ItemStack> required = new ArrayList<>(DCUtil.getProcessedList(input));
		if (!DCUtil.isEmpty(item) && !required.isEmpty()) {
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
	public boolean isNeedCooling() {
		return needCooling;
	}

	@Override
	public boolean canProceedAsDropItem() {
		return dropItem;
	}

	@Override
	public int recipeFrequency() {
		if (processedInput.isEmpty() || DCUtil.isEmpty(processedInput.get(0)))
			return 2;
		ItemStack i = processedInput.get(0);
		if (i.getItem() instanceof IClimateObject) {
			IClimateObject c = (IClimateObject) i.getItem();
			return c.isForcedTickUpdate() ? 0 : 1;
		} else if (i.getItem() instanceof ItemBlock && Block.getBlockFromItem(i.getItem()) instanceof IClimateObject) {
			IClimateObject c = (IClimateObject) Block.getBlockFromItem(i.getItem());
			return c.isForcedTickUpdate() ? 0 : 1;
		} else if (i.getItem() instanceof IEntityItem) {
			return 0;
		} else if (dropItem) {
			return 0;
		}
		return 2;
	}

}
