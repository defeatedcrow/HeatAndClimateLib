package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatBlockRegister;

public class HeatBlockRegister implements IHeatBlockRegister {

	private Map<Block, DCHeatTier> heats;
	private Map<Block, DCHumidity> hums;
	private Map<Block, DCAirflow> airs;

	public HeatBlockRegister() {
		this.heats = new HashMap<Block, DCHeatTier>();
		this.hums = new HashMap<Block, DCHumidity>();
		this.airs = new HashMap<Block, DCAirflow>();
	}

	@Override
	public void registerHeatBlock(Block block, DCHeatTier temp) {
		if (block != null && !isRegisteredHeat(block)) {
			heats.put(block, temp);
		}
	}

	@Override
	public void registerHumBlock(Block block, DCHumidity hum) {
		if (block != null && !isRegisteredHum(block)) {
			hums.put(block, hum);
		}
	}

	@Override
	public void registerAirBlock(Block block, DCAirflow air) {
		if (block != null && !isRegisteredAir(block)) {
			airs.put(block, air);
		}
	}

	@Override
	public DCHeatTier getHeatTier(Block block) {
		if (isRegisteredHeat(block))
			return heats.get(block);
		else
			return DCHeatTier.NORMAL;
	}

	@Override
	public DCHumidity getHumidity(Block block) {
		if (isRegisteredHum(block))
			return hums.get(block);
		else
			return DCHumidity.NORMAL;
	}

	@Override
	public DCAirflow getAirflow(Block block) {
		if (isRegisteredAir(block))
			return airs.get(block);
		else
			return DCAirflow.NORMAL;
	}

	@Override
	public boolean isRegisteredHeat(Block block) {
		return heats.containsKey(block);
	}

	@Override
	public boolean isRegisteredHum(Block block) {
		return hums.containsKey(block);
	}

	@Override
	public boolean isRegisteredAir(Block block) {
		return airs.containsKey(block);
	}

	@Override
	public Map<Block, DCHeatTier> getHeatList() {
		return heats;
	}

	@Override
	public Map<Block, DCHumidity> getHumList() {
		return hums;
	}

	@Override
	public Map<Block, DCAirflow> getAirList() {
		return airs;
	}

}
