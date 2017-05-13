package defeatedcrow.hac.core.plugin.jei;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * JEI表示専用
 */
public class ClimateEffectiveTile {

	private DCHeatTier temp;
	private DCHumidity hum;
	private DCAirflow flow;
	private final Item item;
	private final int meta;

	public ClimateEffectiveTile(Block b, int m, @Nullable DCHeatTier t, @Nullable DCHumidity h, @Nullable DCAirflow f) {
		Item i = null;
		if (b == Blocks.WATER || b == Blocks.FLOWING_WATER) {
			i = Items.WATER_BUCKET;
		} else if (b == Blocks.LAVA || b == Blocks.FLOWING_LAVA) {
			i = Items.LAVA_BUCKET;
		} else if (b == Blocks.LIT_FURNACE) {
			i = Item.getItemFromBlock(Blocks.FURNACE);
		} else {
			i = Item.getItemFromBlock(b);
		}
		item = i;
		meta = m;
		temp = t;
		hum = h;
		flow = f;
	}

	public DCHeatTier getHeat() {
		return temp;
	}

	public DCHumidity getHumidity() {
		return hum;
	}

	public DCAirflow getAirflow() {
		return flow;
	}

	public void setHeat(DCHeatTier t) {
		temp = t;
	}

	public void setHumidity(DCHumidity h) {
		hum = h;
	}

	public void setAirflow(DCAirflow a) {
		flow = a;
	}

	public Item getInputItem() {
		return item;
	}

	public int getInputMeta() {
		return meta;
	}

	public boolean isSameBlock(Block b) {
		if (b == Blocks.WATER || b == Blocks.FLOWING_WATER) {
			return item == Items.WATER_BUCKET;
		} else if (b == Blocks.LAVA || b == Blocks.FLOWING_LAVA) {
			return item == Items.LAVA_BUCKET;
		} else if (b == Blocks.LIT_FURNACE) {
			return item == Item.getItemFromBlock(Blocks.FURNACE);
		} else if (item != null) {
			return item == Item.getItemFromBlock(b);
		} else {
			return false;
		}
	}

}
