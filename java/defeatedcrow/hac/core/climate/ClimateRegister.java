package defeatedcrow.hac.core.climate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.api.climate.IBiomeClimateRegister;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class ClimateRegister implements IBiomeClimateRegister {

	private static Map<Integer, DCClimate> recipes;
	private static List<Integer> seasons;

	public ClimateRegister() {
		this.recipes = new HashMap<Integer, DCClimate>();
		this.seasons = new ArrayList<Integer>();
	}

	@Override
	public void addBiomeClimate(Biome biome, DCHeatTier temp, DCHumidity hum, DCAirflow airflow) {
		if (biome != null && !isAlreadyRegistered(Biome.getIdForBiome(biome))) {
			DCClimate clm = new DCClimate(temp, hum, airflow);
			recipes.put(biome.getIdForBiome(biome), clm);
		}
	}

	@Override
	public void addBiomeClimate(Biome biome, int dim, DCHeatTier temp, DCHumidity hum, DCAirflow airflow) {
		if (biome != null) {
			// dimを加味した気候を登録
			int i = getKey(biome, dim);
			if (!isAlreadyRegistered(i)) {
				DCClimate clm = new DCClimate(temp, hum, airflow);
				recipes.put(i, clm);
			}
		}
	}

	@Override
	public void addBiomeClimate(Biome biome, DCHeatTier temp, DCHumidity hum, DCAirflow airflow, boolean hasSeason) {
		if (biome != null && !hasSeason && !seasons.contains(Biome.getIdForBiome(biome))) {
			seasons.add(Biome.getIdForBiome(biome));
		}
		addBiomeClimate(biome, temp, hum, airflow);
	}

	@Override
	public void setNoSeason(Biome biome) {
		if (biome != null && !seasons.contains(Biome.getIdForBiome(biome))) {
			seasons.add(Biome.getIdForBiome(biome));
		}
	}

	@Override
	public Map<Integer, ? extends IClimate> getClimateList() {
		return recipes;
	}

	@Override
	public List<Integer> getNoSeasonList() {
		return seasons;
	}

	private boolean isAlreadyRegistered(int id) {
		return recipes.containsKey(Integer.valueOf(id));
	}

	private boolean isAlreadyRegistered(int id, int dim) {
		int i = dim << 8 + id;
		return recipes.containsKey(Integer.valueOf(i));
	}

	private IClimate getClimateFromList(int id) {
		if (recipes.containsKey(Integer.valueOf(id))) {
			return recipes.get(Integer.valueOf(id));
		}
		return null;
	}

	private int getKey(Biome biome, int dim) {
		if (biome == null) {
			return 0;
		}
		if (dim == 0) {
			return Biome.getIdForBiome(biome);
		} else {
			int i = dim << 8;
			i += Biome.getIdForBiome(biome);
			return i;
		}
	}

	@Override
	public IClimate getClimateFromBiome(World world, BlockPos pos) {
		Biome biome = world.getBiomeForCoordsBody(pos);
		int dim = world.provider.getDimension();
		int i = getKey(biome, dim);
		return getClimateFromBiome(i);
	}

	@Override
	public IClimate getClimateFromBiome(int biome) {
		IClimate clm = getClimateFromList(biome);
		if (clm == null) {
			DCHeatTier t = getHeatTier(biome);
			DCHumidity h = getHumidity(biome);
			DCAirflow a = getAirflow(biome);
			clm = new DCClimate(t, h, a);
		}
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
		Biome biome = world.getBiomeForCoordsBody(pos);
		int dim = world.provider.getDimension();
		int i = getKey(biome, dim);
		DCHeatTier tier = getHeatTier(i);

		// season
		EnumSeason season = DCTimeHelper.getSeasonEnum(world);
		if (biome != null && !seasons.contains(Biome.getIdForBiome(biome))) {
			if (season == EnumSeason.SUMMER && tier.getTier() < DCHeatTier.HOT.getTier()) {
				return tier.addTier(1);
			} else if (season == EnumSeason.WINTER && tier.getTier() > DCHeatTier.COLD.getTier()) {
				return tier.addTier(-1);
			}
		}
		return tier;
	}

	@Override
	public DCAirflow getAirflow(World world, BlockPos pos) {
		Biome biome = world.getBiomeForCoordsBody(pos);
		int dim = world.provider.getDimension();
		int i = getKey(biome, dim);
		return getAirflow(i);
	}

	@Override
	public DCHumidity getHumidity(World world, BlockPos pos) {
		Biome biome = world.getBiomeForCoordsBody(pos);
		int dim = world.provider.getDimension();
		int i = getKey(biome, dim);
		return getHumidity(i);
	}

	@Override
	public DCHeatTier getHeatTier(int biome) {
		IClimate clm = getClimateFromList(biome);
		int id = biome & 127;
		Biome b = Biome.getBiome(id);
		if (clm != null) {
			return clm.getHeat();
		} else if (b != null) {
			if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.NETHER)) {
				return DCHeatTier.OVEN;
			} else if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.END)) {
				return DCHeatTier.COLD;
			} else {
				float temp = b.getTemperature();
				if (temp > 1.1F) {
					return DCHeatTier.HOT;
				} else if (temp > 0.8F) {
					return DCHeatTier.WARM;
				} else if (temp > 0.4F) {
					return DCHeatTier.NORMAL;
				} else if (temp > 0.1F) {
					return DCHeatTier.COOL;
				} else {
					return DCHeatTier.COLD;
				}
			}
		}
		return DCHeatTier.NORMAL;
	}

	@Override
	public DCAirflow getAirflow(int biome) {
		IClimate clm = getClimateFromList(biome);
		int id = biome & 127;
		Biome b = Biome.getBiome(id);
		if (clm != null) {
			return clm.getAirflow();
		}
		if (b != null && BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.HILLS)) {
			return DCAirflow.FLOW;
		}
		return DCAirflow.NORMAL;
	}

	@Override
	public DCHumidity getHumidity(int biome) {
		IClimate clm = getClimateFromList(biome);
		int id = biome & 127;
		Biome b = Biome.getBiome(id);
		if (clm != null) {
			return clm.getHumidity();
		} else if (b != null) {
			if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.DRY) || !b.canRain()) {
				return DCHumidity.DRY;
			} else if (BiomeDictionary.isBiomeOfType(b, BiomeDictionary.Type.WET) || b.getRainfall() > 0.8F) {
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
