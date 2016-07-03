package defeatedcrow.hac.core.climate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
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
	public IClimate getClimate(World world, BlockPos pos, int[] r) {
		if (r == null || r.length < 3)
			r = new int[] {
					2,
					1,
					1 };
		DCHeatTier temp = ClimateAPI.calculator.getAverageTemp(world, pos, r[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, r[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, r[2], false);

		int code = (air.getID() << 6) + (hum.getID() << 4) + temp.getID();
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return clm;
	}

	@Override
	public DCHeatTier getAverageTemp(World world, BlockPos pos, int r, boolean h) {
		if (r < 0 || r > 15)
			r = 1;
		DCHeatTier temp = ClimateAPI.calculator.getHeat(world, pos, r, h);
		DCHeatTier cold = ClimateAPI.calculator.getCold(world, pos, r, h);
		// DCLogger.debugLog("Heat: " + temp.getTier() + " , Cold: " + cold.getTier());

		if (temp.getTier() > cold.getTier() && cold.getTier() < 0) {
			if (temp.getTier() < 0) {
				temp = cold;
			} else {
				temp = temp.addTier(cold.getTier());
			}
		}
		return temp;
	}

	@Override
	public DCHeatTier getHeat(World world, BlockPos pos, int r, boolean h) {
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		/*
		 * biomeの気温
		 * 屋根あり: Tierが1段階Normalに近づく
		 * 屋根無し: Biome気温そのまま
		 * MOUNTAINで標高100以上: Tireが1段階下がる
		 */
		DCHeatTier hot = temp;
		if (hasRoof(world, pos)) {
			if (temp.getTier() < 0) {
				hot = temp.addTier(1);
			} else if (temp.getTier() > 0) {
				hot = temp.addTier(-1);
			}
		}

		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			int m = block.getMetaFromState(world.getBlockState(pos));
			if (block instanceof IHeatTile) {
				DCHeatTier current = ((IHeatTile) block).getHeatTier(world, pos);
				if (current.getTier() > hot.getTier()) {
					hot = current;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
				DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block, m);
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
				int m = block.getMetaFromState(world.getBlockState(p2));
				if (block instanceof IHeatTile) {
					DCHeatTier current = ((IHeatTile) block).getHeatTier(world, p2);
					if (current.getTier() > hot.getTier()) {
						hot = current;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
					DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block, m);
					if (cur.getTier() > hot.getTier()) {
						hot = cur;
					}
				}
			}
		}
		return hot;
	}

	@Override
	public DCHeatTier getCold(World world, BlockPos pos, int r, boolean h) {
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		/*
		 * biomeの気温
		 * 屋根あり: Tierが1段階Normalに近づく
		 * 屋根無し: 天候によって変化
		 * 晴れ: Biome気温のまま
		 * 夜雨 or 雷雨: Tierが1段階低い物になる
		 */
		DCHeatTier cold = temp;
		if (hasRoof(world, pos)) {
			if (temp.getTier() < 0) {
				cold = cold.addTier(1);
			} else if (temp.getTier() > 0) {
				cold = cold.addTier(-1);
			}
		} else {
			if (world.isThundering()) {
				cold = cold.addTier(-1);
			} else if (world.isRaining() && !world.isDaytime()) {
				cold = cold.addTier(-1);
			}
			if (cold.getTier() < -2) {
				// ABSOLUTEは自然発生しない
				cold = DCHeatTier.FROSTBITE;
			}
		}

		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			int m = block.getMetaFromState(world.getBlockState(pos));
			if (block instanceof IHeatTile) {
				DCHeatTier current = ((IHeatTile) block).getHeatTier(world, pos);
				if (current.getTier() < cold.getTier()) {
					cold = current;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
				DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block, m);
				if (cur.getTier() < cold.getTier()) {
					cold = cur;
				}
			}
		} else {
			int h1 = h ? 0 : r;
			BlockPos min = new BlockPos(pos.add(-r, -h1, -r));
			BlockPos max = new BlockPos(pos.add(r, h1, r));
			Iterable<BlockPos> itr = pos.getAllInBox(min, max);
			for (BlockPos p2 : itr) {
				Block block = world.getBlockState(p2).getBlock();
				int m = block.getMetaFromState(world.getBlockState(p2));
				if (block instanceof IHeatTile) {
					DCHeatTier current = ((IHeatTile) block).getHeatTier(world, p2);
					if (current.getTier() < cold.getTier()) {
						cold = current;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
					DCHeatTier cur = ClimateAPI.registerBlock.getHeatTier(block, m);
					if (cur.getTier() < cold.getTier()) {
						cold = cur;
					}
				}
			}
		}
		return cold;
	}

	// 合計値で考える
	@Override
	public DCHumidity getHumidity(World world, BlockPos pos, int r, boolean h) {
		if (r < 0 || r > 15)
			r = 1;
		DCHumidity hum = ClimateAPI.register.getHumidity(world, pos);
		int ret = hum.getID() - 1;
		boolean isUnderwater = false;
		boolean hasWater = false;
		boolean hasAir = false;
		// さきに水没判定をやる
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos p1 = new BlockPos(pos.getX() + face.getFrontOffsetX(), pos.getY() + face.getFrontOffsetY(),
					pos.getZ() + face.getFrontOffsetZ());
			Block block = world.getBlockState(p1).getBlock();
			int m = block.getMetaFromState(world.getBlockState(p1));
			if (block instanceof IHumidityTile) {
				DCHumidity current = ((IHumidityTile) block).getHumdiity(world, pos);
				if (current == DCHumidity.UNDERWATER)
					hasWater = true;
			} else if (ClimateAPI.registerBlock.isRegisteredHum(block, m)) {
				DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block, m);
				if (cur == DCHumidity.UNDERWATER)
					hasWater = true;
			} else if (!world.getBlockState(p1).isNormalCube()) {
				hasAir = true;
			}
		}
		if (hasWater && !hasAir) {
			return DCHumidity.UNDERWATER;
		}
		// 雨が降っている
		if (world.isRaining() && hum != DCHumidity.DRY && world.canBlockSeeSky(pos.up())) {
			ret++;
		}
		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			int m = block.getMetaFromState(world.getBlockState(pos));
			if (block instanceof IHumidityTile) {
				DCHumidity current = ((IHumidityTile) block).getHumdiity(world, pos);
				if (current == DCHumidity.DRY) {
					ret--;
				} else if (current.getID() > 1) {
					ret++;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredHum(block, m)) {
				DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block, m);
				if (cur == DCHumidity.DRY) {
					ret--;
				} else if (cur.getID() > 1) {
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
				int m = block.getMetaFromState(world.getBlockState(p2));
				if (block instanceof IHumidityTile) {
					DCHumidity current = ((IHumidityTile) block).getHumdiity(world, p2);
					if (current == DCHumidity.DRY) {
						ret--;
					} else if (current.getID() > 1) {
						ret++;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredHum(block, m)) {
					DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block, m);
					if (cur == DCHumidity.DRY) {
						ret--;
					} else if (cur.getID() > 1) {
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
		if (r < 0 || r > 15)
			r = 1;
		DCAirflow air = ClimateAPI.register.getAirflow(world, pos);
		// biomeベース通気 -> 屋内ではNORMALになる
		if (!hasRoof(world, pos)) {
			air = DCAirflow.FLOW;
		}
		int count = 0; // 空気量カウント
		boolean hasWind = false;
		boolean hasBlow = false;

		if (r < 0) {
			Block block = world.getBlockState(pos).getBlock();
			int m = block.getMetaFromState(world.getBlockState(pos));
			if (block instanceof IAirflowTile) {
				DCAirflow current = ((IAirflowTile) block).getAirflow(world, pos);
				if (current.getID() > 1) {
					count++;
					hasWind = true;
					if (current == DCAirflow.WIND)
						hasBlow = true;
				}
			} else if (ClimateAPI.registerBlock.isRegisteredAir(block, m)) {
				DCAirflow cur = ClimateAPI.registerBlock.getAirflow(block, m);
				if (cur.getID() > 0) {
					count++;
					if (cur.getID() > 1) {
						hasWind = true;
						if (cur == DCAirflow.WIND)
							hasBlow = true;
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
				int m = block.getMetaFromState(world.getBlockState(p2));
				if (block instanceof IAirflowTile) {
					DCAirflow current = ((IAirflowTile) block).getAirflow(world, p2);
					if (current.getID() > 0) {
						if (current.getID() > 1) {
							if (current == DCAirflow.WIND)
								hasBlow = true;
							hasWind = true;
						}
						count++;
					}
				} else if (ClimateAPI.registerBlock.isRegisteredAir(block, m)) {
					DCAirflow cur = ClimateAPI.registerBlock.getAirflow(block, m);
					if (cur.getID() > 0) {
						count++;
						if (cur.getID() > 1) {
							hasWind = true;
							if (cur == DCAirflow.WIND)
								hasBlow = true;
						}
					}
				}
			}
		}
		if (hasBlow) {
			return DCAirflow.WIND;
		}
		if (count > 2) {
			if (hasWind) {
				if (world.isThundering()) {
					return DCAirflow.WIND;
				}
				return DCAirflow.FLOW;
			} else {
				return air;
			}
		} else {
			return DCAirflow.TIGHT;
		}
	}

	boolean hasRoof(World world, BlockPos pos) {
		BlockPos pos2 = pos.up();
		int lim = pos.getY() + 16;
		while (pos2.getY() < lim && pos2.getY() < world.getActualHeight()) {
			IBlockState state = world.getBlockState(pos2);
			Block block = world.getBlockState(pos2).getBlock();
			if (!world.isAirBlock(pos2)) {
				return true;
			}
			pos2 = pos2.up();
		}
		return false;
	}

}
