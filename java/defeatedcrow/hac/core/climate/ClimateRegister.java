package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IBiomeClimateRegister;
import defeatedcrow.hac.api.climate.IClimate;

public class ClimateRegister implements IBiomeClimateRegister {

	private Map<Integer, DCClimate> recipes;

	public ClimateRegister() {
		this.recipes = new HashMap<Integer, DCClimate>();
	}

	@Override
	public void addBiomeClimate(Biome biome, DCHeatTier temp, DCHumidity hum, DCAirflow airflow) {
		if (biome != null && !isAlreadyRegistered(biome.getIdForBiome(biome))) {
			DCClimate clm = new DCClimate(temp, hum, airflow);
			recipes.put(biome.getIdForBiome(biome), clm);
		}
	}

	@Override
	public Map<Integer, ? extends IClimate> getClimateList() {
		return recipes;
	}

	private boolean isAlreadyRegistered(int id) {
		return recipes.containsKey(id);
	}

	private IClimate getClimateFromList(int id) {
		if (recipes.containsKey(id)) {
			return recipes.get(id);
		}
		return null;
	}

	@Override
	public IClimate getClimateFromBiome(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		return getClimateFromBiome(biome);
	}

	@Override
	public IClimate getClimateFromBiome(Biome biome) {
		IClimate clm = getClimateFromList(biome.getIdForBiome(biome));
		if (clm == null) {
			DCHeatTier t = getHeatTier(biome);
			DCHumidity h = getHumidity(biome);
			DCAirflow a = getAirflow(biome);
			clm = new DCClimate(t, h, a);
		}
		// DCLogger.debugLog("climate", "climate to byte: " +
		// Integer.toBinaryString(clm.getClimateInt()));
		return clm;
	}

	@Override
	public IClimate getClimateFromInt(int code) {
		int t = code & 15;
		int h = (code >> 4) & 3;
		int a = (code >> 6) & 3;
		DCHeatTier temp = DCHeatTier.getTypeByID(t);
		DCHumidity hum = DCHumidity.getTypeByID(h);
		DCAirflow air = DCAirflow.getTypeByID(a);
		return new DCClimate(temp, hum, air);
	}

	@Override
	public IClimate getClimateFromParam(DCHeatTier heat, DCHumidity hum, DCAirflow air) {
		return new DCClimate(heat, hum, air);
	}

	@Override
	public DCHeatTier getHeatTier(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		DCHeatTier tier = getHeatTier(biome);
		if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN) && pos.getY() > 100) {
			tier = tier.addTier(-1);
		}
		return tier;
	}

	@Override
	public DCAirflow getAirflow(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		return getAirflow(biome);
	}

	@Override
	public DCHumidity getHumidity(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		return getHumidity(biome);
	}

	@Override
	public DCHeatTier getHeatTier(Biome biome) {
		IClimate clm = getClimateFromList(biome.getIdForBiome(biome));
		if (clm != null) {
			return clm.getHeat();
		} else {
			if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.NETHER)) {
				return DCHeatTier.OVEN;
			} else if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HOT)) {
				return DCHeatTier.HOT;
			} else if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.COLD)) {
				return DCHeatTier.COLD;
			}
		}
		return DCHeatTier.NORMAL;
	}

	@Override
	public DCAirflow getAirflow(Biome biome) {
		IClimate clm = getClimateFromList(biome.getIdForBiome(biome));
		if (clm != null) {
			return clm.getAirflow();
		}
		if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HILLS)) {
			return DCAirflow.FLOW;
		}
		return DCAirflow.NORMAL;
	}

	@Override
	public DCHumidity getHumidity(Biome biome) {
		IClimate clm = getClimateFromList(biome.getIdForBiome(biome));
		if (clm != null) {
			return clm.getHumidity();
		} else {
			if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY)) {
				return DCHumidity.DRY;
			} else if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WET)) {
				return DCHumidity.WET;
			}
		}
		return DCHumidity.NORMAL;
	}

	public class DCClimate implements IClimate {

		private final DCHeatTier temp;
		private final DCHumidity hum;
		private final DCAirflow flow;
		private final int code; // 0bAABBCCCC;

		public DCClimate(DCHeatTier t, DCHumidity h, DCAirflow f) {
			temp = t;
			hum = h;
			flow = f;
			int i1 = t.getID(); // 0-15
			int i2 = h.getID(); // 0-2
			int i3 = f.getID(); // 0-2
			i2 = i2 << 4;
			i3 = i3 << 6;
			code = i1 + i2 + i3;
		}

		@Override
		public DCHeatTier getHeat() {
			return temp;
		}

		@Override
		public DCHumidity getHumidity() {
			return hum;
		}

		@Override
		public DCAirflow getAirflow() {
			return flow;
		}

		@Override
		public int getClimateInt() {
			return code;
		}

	}

}
