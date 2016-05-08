package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatBlockRegister;

public class HeatBlockRegister implements IHeatBlockRegister {

	private Map<BlockSet, DCHeatTier> heats;
	private Map<BlockSet, DCHumidity> hums;
	private Map<BlockSet, DCAirflow> airs;

	public HeatBlockRegister() {
		this.heats = new HashMap<BlockSet, DCHeatTier>();
		this.hums = new HashMap<BlockSet, DCHumidity>();
		this.airs = new HashMap<BlockSet, DCAirflow>();
	}

	@Override
	public void registerHeatBlock(Block block, int meta, DCHeatTier temp) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredHeat(block, meta))
				heats.put(set, temp);
		}
	}

	@Override
	public void registerHumBlock(Block block, int meta, DCHumidity hum) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredHum(block, meta))
				hums.put(set, hum);
		}
	}

	@Override
	public void registerAirBlock(Block block, int meta, DCAirflow air) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredAir(block, meta))
				airs.put(set, air);
		}
	}

	@Override
	public DCHeatTier getHeatTier(Block block, int meta) {
		Set<BlockSet> s = heats.keySet();
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null)
			return heats.get(b);
		else
			return DCHeatTier.NORMAL;
	}

	@Override
	public DCHumidity getHumidity(Block block, int meta) {
		Set<BlockSet> s = hums.keySet();
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null)
			return hums.get(b);
		else
			return DCHumidity.NORMAL;
	}

	@Override
	public DCAirflow getAirflow(Block block, int meta) {
		Set<BlockSet> s = airs.keySet();
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null)
			return airs.get(b);
		else
			return DCAirflow.NORMAL;
	}

	@Override
	public boolean isRegisteredHeat(Block block, int meta) {
		Set<BlockSet> s = heats.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public boolean isRegisteredHum(Block block, int meta) {
		Set<BlockSet> s = hums.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public boolean isRegisteredAir(Block block, int meta) {
		Set<BlockSet> s = airs.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public Map<BlockSet, DCHeatTier> getHeatList() {
		return heats;
	}

	@Override
	public Map<BlockSet, DCHumidity> getHumList() {
		return hums;
	}

	@Override
	public Map<BlockSet, DCAirflow> getAirList() {
		return airs;
	}

	private BlockSet include(Set<BlockSet> list, BlockSet target) {
		BlockSet ret = null;
		for (BlockSet b : list) {
			if (b.equals(target)) {
				ret = b;
			}
		}
		return ret;
	}

}
