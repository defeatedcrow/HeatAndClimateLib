package defeatedcrow.hac.core.climate;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.IAirflowTile;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateCalculator;
import defeatedcrow.hac.api.climate.IHeatTile;
import defeatedcrow.hac.api.climate.IHumidityTile;

/**
 * BiomeとBlockの環境要因の合成
 */
public class ClimateCalculator implements IClimateCalculator {

	@Override
	public IClimate getClimate(World world, BlockPos pos, int r) {
		DCHeatTier temp = ClimateAPI.calculator.getHeatTier(world, pos.down(), r, false);
		DCHeatTier cold = ClimateAPI.calculator.getColdTier(world, pos, r, false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, r, false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, r, false);

		if (temp.getTier() >= 0 && cold.getTier() < 0) {
			temp = temp.addTier(cold.getTier());
		}

		int code = (air.getID() << 5) + (hum.getID() << 3) + temp.getID();
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return clm;
	}

	@Override
	public DCHeatTier getHeatTier(World world, BlockPos pos, int r, boolean h) {
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		DCHeatTier hot = temp;
		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof IHeatTile) {
				DCHeatTier current = ((IHeatTile) block).getHeatTier(world, pos);
				if (current.getTier() > hot.getTier()) {
					hot = current;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHeat(block)) {
				DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block);
				if (cur.getTier() > hot.getTier()) {
					hot = cur;
				}
			}
		} else {
			int h1 = h ? 0 : r;
			BlockPos min = new BlockPos(pos.add(-r, -h1, -r));
			BlockPos max = new BlockPos(pos.add(r, h1, r));
			Iterable<BlockPos> itr = pos.getAllInBox(min, max);
			for (BlockPos p2 : itr) {
				Block block = world.getBlockState(p2).getBlock();
				if (block instanceof IHeatTile) {
					DCHeatTier current = ((IHeatTile) block).getHeatTier(world, p2);
					if (current.getTier() > hot.getTier()) {
						hot = current;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHeat(block)) {
					DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block);
					if (cur.getTier() > hot.getTier()) {
						hot = cur;
					}
				}
			}
		}
		return hot;
	}

	@Override
	public DCHeatTier getColdTier(World world, BlockPos pos, int r, boolean h) {
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		DCHeatTier hot = temp;
		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof IHeatTile) {
				DCHeatTier current = ((IHeatTile) block).getHeatTier(world, pos);
				if (current.getTier() < hot.getTier()) {
					hot = current;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHeat(block)) {
				DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block);
				if (cur.getTier() < hot.getTier()) {
					hot = cur;
				}
			}
		} else {
			int h1 = h ? 0 : r;
			BlockPos min = new BlockPos(pos.add(-r, -h1, -r));
			BlockPos max = new BlockPos(pos.add(r, h1, r));
			Iterable<BlockPos> itr = pos.getAllInBox(min, max);
			for (BlockPos p2 : itr) {
				Block block = world.getBlockState(p2).getBlock();
				if (block instanceof IHeatTile) {
					DCHeatTier current = ((IHeatTile) block).getHeatTier(world, p2);
					if (current.getTier() < hot.getTier()) {
						hot = current;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHeat(block)) {
					DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block);
					if (cur.getTier() < hot.getTier()) {
						hot = cur;
					}
				}
			}
		}
		return hot;
	}

	// 合計値で考える
	@Override
	public DCHumidity getHumidity(World world, BlockPos pos, int r, boolean h) {
		DCHumidity hum = ClimateAPI.register.getHumidity(world, pos);
		int ret = hum.getID() - 1;
		// 雨が降っている
		if (world.isRaining() && hum != DCHumidity.DRY) {
			ret++;
		}
		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof IHumidityTile) {
				DCHumidity current = ((IHumidityTile) block).getHumdiity(world, pos);
				if (current == DCHumidity.DRY) {
					ret--;
				} else if (current == DCHumidity.WET) {
					ret++;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHum(block)) {
				DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block);
				if (cur == DCHumidity.DRY) {
					ret--;
				} else if (cur == DCHumidity.WET) {
					ret++;
				}
			}
		} else {
			int h1 = h ? 0 : r;
			BlockPos min = new BlockPos(pos.add(-r, -h1, -r));
			BlockPos max = new BlockPos(pos.add(r, h1, r));
			Iterable<BlockPos> itr = pos.getAllInBox(min, max);
			for (BlockPos p2 : itr) {
				Block block = world.getBlockState(p2).getBlock();
				if (block instanceof IHumidityTile) {
					DCHumidity current = ((IHumidityTile) block).getHumdiity(world, p2);
					if (current == DCHumidity.DRY) {
						ret--;
					} else if (current == DCHumidity.WET) {
						ret++;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHum(block)) {
					DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block);
					if (cur == DCHumidity.DRY) {
						ret--;
					} else if (cur == DCHumidity.WET) {
						ret++;
					}
				}
			}
		}

		if (ret < 0) {
			return DCHumidity.DRY;
		} else {
			return ret == 0 ? DCHumidity.NORMAL : DCHumidity.WET;
		}
	}

	// ひとつでもAirがあれば窒息はしない
	@Override
	public DCAirflow getAirflow(World world, BlockPos pos, int r, boolean h) {
		DCAirflow air = ClimateAPI.register.getAirflow(world, pos);
		boolean hasAir = false;
		boolean hasWind = false;

		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof IAirflowTile) {
				DCAirflow current = ((IAirflowTile) block).getAirflow(world, pos);
				if (current == DCAirflow.FLOW) {
					hasAir = true;
					hasWind = true;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredAir(block)) {
				DCAirflow cur = ClimateAPI.registerBlock.getAirflow(block);
				if (cur.getID() > 0) {
					hasAir = true;
					if (world.canBlockSeeSky(pos) && !world.provider.getHasNoSky()) {
						hasWind = true;
					}
					if (cur == DCAirflow.FLOW) {
						hasWind = true;
					}
				}
			}
		} else {
			int h1 = h ? 0 : r;
			BlockPos min = new BlockPos(pos.add(-r, -h1, -r));
			BlockPos max = new BlockPos(pos.add(r, h1, r));
			Iterable<BlockPos> itr = pos.getAllInBox(min, max);
			for (BlockPos p2 : itr) {
				Block block = world.getBlockState(p2).getBlock();
				if (block instanceof IAirflowTile) {
					DCAirflow current = ((IAirflowTile) block).getAirflow(world, p2);
					if (current == DCAirflow.FLOW) {
						hasAir = true;
						hasWind = true;
					} else if (current == DCAirflow.NORMAL)
						hasAir = true;
				} else if (ClimateAPI.registerBlock.isRegisteredAir(block)) {
					DCAirflow cur = ClimateAPI.registerBlock.getAirflow(block);
					if (cur.getID() > 0) {
						hasAir = true;
						if (world.canBlockSeeSky(p2) && !world.provider.getHasNoSky()) {
							hasWind = true;
						}
						if (cur == DCAirflow.FLOW) {
							hasWind = true;
						}
					}
				} else if (!block.getMaterial().isLiquid() && block.getMaterial().getMaterialMobility() == 1) {
					hasAir = true;
				}
			}
		}

		if (hasAir) {
			if (hasWind) {
				return DCAirflow.FLOW;
			} else {
				return air;
			}
		} else {
			return DCAirflow.TIGHT;
		}
	}

}
