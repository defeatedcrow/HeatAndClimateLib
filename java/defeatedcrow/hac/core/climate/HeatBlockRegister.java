package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatBlockRegister;
import defeatedcrow.hac.core.plugin.ClimateEffectiveTile;
import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

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
			if (!this.isRegisteredHeat(block, meta)) {
				heats.put(set, temp);
				registerEffectiveHeat(block, meta, temp);
			}
		}
	}

	@Override
	public void registerHumBlock(Block block, int meta, DCHumidity hum) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredHum(block, meta)) {
				hums.put(set, hum);
				registerEffectiveHum(block, meta, hum);
			}
		}
	}

	@Override
	public void registerAirBlock(Block block, int meta, DCAirflow air) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredAir(block, meta)) {
				airs.put(set, air);
				registerEffectiveAir(block, meta, air);
			}
		}
	}

	@Override
	public DCHeatTier getHeatTier(Block block, int meta) {
		Set<BlockSet> s = heats.keySet();
		DCHeatTier heat = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			heat = heats.get(b);
		}
		if (heat != null) {
			return heat;
		} else {
			return DCHeatTier.NORMAL;
		}

	}

	@Override
	public DCHumidity getHumidity(Block block, int meta) {
		Set<BlockSet> s = hums.keySet();
		DCHumidity hum = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			hum = hums.get(b);
		}
		if (hum != null) {
			return hum;
		} else {
			return DCHumidity.NORMAL;
		}
	}

	@Override
	public DCAirflow getAirflow(Block block, int meta) {
		Set<BlockSet> s = airs.keySet();
		DCAirflow air = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			air = airs.get(b);
		}
		if (air != null) {
			return air;
		} else {
			return DCAirflow.NORMAL;
		}
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

	private void registerEffectiveHeat(Block block, int meta, DCHeatTier t) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, t, null, null));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setHeat(t);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, t, null, null));
			}
		}
	}

	private void registerEffectiveHum(Block block, int meta, DCHumidity h) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, h, null));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setHumidity(h);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, h, null));
			}
		}
	}

	private void registerEffectiveAir(Block block, int meta, DCAirflow a) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, null, a));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setAirflow(a);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, null, a));
			}
		}
	}

}
