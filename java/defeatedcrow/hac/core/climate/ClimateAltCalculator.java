package defeatedcrow.hac.core.climate;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.ClimateCalculateEvent;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IAirflowTile;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateCalculator;
import defeatedcrow.hac.api.climate.IHeatCanceler;
import defeatedcrow.hac.api.climate.IHeatTile;
import defeatedcrow.hac.api.climate.IHumidityTile;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * BlockPosの走査方法を変更したバージョン
 */
public class ClimateAltCalculator implements IClimateCalculator {

	@Override
	public IClimate getClimate(World world, BlockPos pos) {
		int[] r = new int[] {
				2, 1, 1
		};
		return getClimate(world, pos, r);
	}

	@Override
	public DCHeatTier getAverageTemp(World world, BlockPos pos) {
		return getAverageTemp(world, pos, 2, false);
	}

	@Override
	public DCHumidity getHumidity(World world, BlockPos pos) {
		return getHumidity(world, pos, 1, false);
	}

	@Override
	public DCAirflow getAirflow(World world, BlockPos pos) {
		return getAirflow(world, pos, 1, false);
	}

	@Override
	public IClimate getClimate(World world, BlockPos pos, int[] r) {
		if (r == null || r.length < 3)
			r = new int[] {
					2, 1, 1
			};
		DCHeatTier temp = ClimateAPI.calculator.getAverageTemp(world, pos, r[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, r[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, r[2], false);

		int code = (air.getID() << 6) + (hum.getID() << 4) + temp.getID();
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);

		ClimateCalculateEvent event = new ClimateCalculateEvent(world, pos, clm);
		IClimate result = event.result();

		return result == null ? clm : result;
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
		if (temp.getTier() < cold.getTier()) {
			temp = cold;
		}
		return temp;
	}

	@Override
	public DCHeatTier getHeat(World world, BlockPos pos, int r, boolean h) {
		if (world == null || pos == null) {
			return DCHeatTier.NORMAL;
		}
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		if (temp == null) {
			temp = DCHeatTier.NORMAL;
		}
		/*
		 * biomeの気温
		 * 屋根あり: Tierが1段階Normalに近づく
		 * 屋根無し: Biome気温そのまま
		 * 標高100以上: Tireが1段階下がる
		 */
		DCHeatTier hot = temp;
		if (hasRoof(world, pos)) {
			if (temp.getTier() < 0) {
				hot = temp.addTier(1);
			} else if (temp.getTier() > 0) {
				hot = temp.addTier(-1);
			}
		} else {
			int offset = WeatherChecker.getTempOffset(world.provider.getDimension(),
					world.provider.doesWaterVaporize());
			if (offset < 0 || DCTimeHelper.isDayTime(world)) {
				hot = temp.addTier(offset);
			}
		}

		if (pos.getY() >= 100) {
			hot = hot.addTier(-1);
		}

		/*
		 * blockの気温
		 */
		if (r < 0) {
			r = 0;
		}
		int h1 = h ? 0 : r;
		for (int x = pos.getX() - r; x <= pos.getX() + r; x++) {
			for (int z = pos.getZ() - r; z <= pos.getZ() + r; z++) {
				for (int y = pos.getY() - h1; y <= pos.getY() + h1; y++) {
					BlockPos up = new BlockPos(x, y + 1, z);
					IBlockState upb = world.getBlockState(up);
					if (upb != null && upb.getBlock() instanceof IHeatCanceler) {
						if (((IHeatCanceler) upb.getBlock()).isActive(upb)) {
							continue;
						}
					}

					BlockPos p2 = new BlockPos(x, y, z);
					Block block = world.getBlockState(p2).getBlock();
					int m = block.getMetaFromState(world.getBlockState(p2));
					DCHeatTier current = hot;
					if (block instanceof IHeatTile) {
						current = ((IHeatTile) block).getHeatTier(world, pos, p2);
					} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
						current = ClimateAPI.registerBlock.getHeatTier(block, m);
					} else if (block instanceof IFluidBlock) {
						Fluid type = ((IFluidBlock) block).getFluid();
						if (type != null) {
							current = DCHeatTier.getTypeByTemperature(type.getTemperature());
						}
					}

					if (current == null) {
						current = hot;
					}

					if (CoreConfigDC.wall) {
						boolean wall = false;
						int xi = x;
						int yi = y;
						int zi = z;
						if (x > pos.getX() + 1) {
							xi = x - 1;
							wall = true;
						} else if (x < pos.getX() - 1) {
							xi = x + 1;
							wall = true;
						}
						if (y > pos.getY() + 1) {
							yi = y - 1;
							wall = true;
						} else if (y < pos.getY() - 1) {
							yi = y + 1;
							wall = true;
						}
						if (z > pos.getZ() + 1) {
							zi = z - 1;
							wall = true;
						} else if (z < pos.getZ() - 1) {
							zi = z + 1;
							wall = true;
						}

						if (current.getTier() > hot.getTier()) {
							if (wall) {
								int pre = ThermalInsulationUtil.getInsulation(world, new BlockPos(xi, yi, zi));
								if (pre != 0) {
									if (pre == -1) {
										continue;
									} else {
										int curT = current.getTier();
										if (curT < 0) {
											curT += pre;
											if (curT < 0 && curT > hot.getTier()) {
												current = DCHeatTier.getHeatEnum(curT);
											} else {
												continue;
											}
										} else {
											curT -= pre;
											if (curT > hot.getTier()) {
												current = DCHeatTier.getHeatEnum(curT);
											} else {
												continue;
											}
										}
									}
								}
							}
							hot = current;
						}
					} else {
						if (current.getTier() > hot.getTier()) {
							hot = current;
						}
					}

				}
			}
		}
		return hot;
	}

	@Override
	public DCHeatTier getCold(World world, BlockPos pos, int r, boolean h) {
		if (world == null || pos == null) {
			return DCHeatTier.NORMAL;
		}
		DCHeatTier temp = ClimateAPI.register.getHeatTier(world, pos);
		if (temp == null) {
			temp = DCHeatTier.NORMAL;
		}
		/*
		 * biomeの気温
		 * 屋根あり: Tierが1段階Normalに近づく
		 * 屋根無し: 天候によって変化
		 * 晴れ: Biome気温のまま
		 * 雨: Tierが1段階低い物になる (夜雨はクライアント側でうまく検知できないため廃止)
		 */
		DCHeatTier cold = temp;
		if (hasRoof(world, pos)) {
			if (temp.getTier() < 0) {
				cold = cold.addTier(1);
			} else if (temp.getTier() > 0) {
				cold = cold.addTier(-1);
			}
		} else {
			int offset = WeatherChecker.getTempOffset(world.provider.getDimension(),
					world.provider.doesWaterVaporize());
			cold = cold.addTier(offset);
		}

		if (pos.getY() > 100) {
			cold = cold.addTier(-1);
		}

		if (cold == DCHeatTier.ABSOLUTE) {
			// ABSOLUTEは自然発生しない
			cold = DCHeatTier.FROSTBITE;
		}

		/*
		 * blockの気温
		 */
		if (r < 0) {
			r = 0;
		}
		int h1 = h ? 0 : r;
		for (int x = pos.getX() - r; x <= pos.getX() + r; x++) {
			for (int z = pos.getZ() - r; z <= pos.getZ() + r; z++) {
				for (int y = pos.getY() - h1; y <= pos.getY() + h1; y++) {
					BlockPos up = new BlockPos(x, y + 1, z);
					IBlockState upb = world.getBlockState(up);
					if (upb != null && upb.getBlock() instanceof IHeatCanceler) {
						if (((IHeatCanceler) upb.getBlock()).isActive(upb)) {
							continue;
						}
					}

					BlockPos p2 = new BlockPos(x, y, z);
					Block block = world.getBlockState(p2).getBlock();
					int m = block.getMetaFromState(world.getBlockState(p2));
					DCHeatTier current = cold;
					if (block instanceof IHeatTile) {
						current = ((IHeatTile) block).getHeatTier(world, pos, p2);
					} else if (ClimateAPI.registerBlock.isRegisteredHeat(block, m)) {
						current = ClimateAPI.registerBlock.getHeatTier(block, m);
					} else if (block instanceof IFluidBlock) {
						Fluid type = ((IFluidBlock) block).getFluid();
						if (type != null) {
							current = DCHeatTier.getTypeByTemperature(type.getTemperature());
						}
					}

					if (current == null) {
						current = cold;
					}

					if (CoreConfigDC.wall) {
						boolean wall = false;
						int xi = x;
						int yi = y;
						int zi = z;
						if (x > pos.getX() + 1) {
							xi = x - 1;
							wall = true;
						} else if (x < pos.getX() - 1) {
							xi = x + 1;
							wall = true;
						}
						if (y > pos.getY() + 1) {
							yi = y - 1;
							wall = true;
						} else if (y < pos.getY() - 1) {
							yi = y + 1;
							wall = true;
						}
						if (z > pos.getZ() + 1) {
							zi = z - 1;
							wall = true;
						} else if (z < pos.getZ() - 1) {
							zi = z + 1;
							wall = true;
						}

						if (current.getTier() < cold.getTier()) {
							if (wall) {
								int pre = ThermalInsulationUtil.getInsulation(world, new BlockPos(xi, yi, zi));
								if (pre != 0) {
									if (pre == -1) {
										continue;
									} else {
										int curT = current.getTier();
										if (curT < 0) {
											curT += pre;
											if (curT < cold.getTier()) {
												current = DCHeatTier.getHeatEnum(curT);
											} else {
												continue;
											}
										} else {
											curT -= pre;
											if (curT > 0 && curT < cold.getTier()) {
												current = DCHeatTier.getHeatEnum(curT);
											} else {
												continue;
											}
										}
									}
								}
							}
							cold = current;

						}

					}
				}
			}
		}
		return cold;
	}

	// 合計値で考える
	@Override
	public DCHumidity getHumidity(World world, BlockPos pos, int r, boolean h) {
		if (world == null || pos == null) {
			return DCHumidity.NORMAL;
		}
		if (r < 0 || r > 15)
			r = 1;

		// biomeの基礎湿度
		DCHumidity hum = ClimateAPI.register.getHumidity(world, pos);
		Biome biome = world.getBiomeForCoordsBody(pos);
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
				DCHumidity current = ((IHumidityTile) block).getHumdiity(world, pos, pos);
				if (current == DCHumidity.UNDERWATER)
					hasWater = true;
			} else if (ClimateAPI.registerBlock.isRegisteredHum(block, m)) {
				DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block, m);
				if (cur == DCHumidity.UNDERWATER)
					hasWater = true;
			} else if (world.getBlockState(p1).getMaterial() == Material.WATER) {
				hasWater = true;
			} else if (!world.getBlockState(p1).isNormalCube()) {
				hasAir = true;
			}
		}
		if (hasWater && !hasAir) {
			return DCHumidity.UNDERWATER;
		}

		// 雨が降っている
		if (!hasRoof(world, pos)) {
			int offset = WeatherChecker.getHumOffset(world.provider.getDimension(), world.provider.doesWaterVaporize());
			ret += offset;
		}
		/*
		 * blockの値
		 */
		int h1 = h ? 0 : r;
		for (int x = pos.getX() - r; x <= pos.getX() + r; x++) {
			for (int z = pos.getZ() - r; z <= pos.getZ() + r; z++) {
				for (int y = pos.getY() - h1; y <= pos.getY() + h1; y++) {
					BlockPos p2 = new BlockPos(x, y, z);
					Block block = world.getBlockState(p2).getBlock();
					int m = block.getMetaFromState(world.getBlockState(p2));
					if (block instanceof IHumidityTile) {
						DCHumidity current = ((IHumidityTile) block).getHumdiity(world, pos, p2);
						if (current == DCHumidity.DRY) {
							ret--;
						} else if (current != null && current.getID() > 1) {
							ret++;
						}
					} else if (ClimateAPI.registerBlock.isRegisteredHum(block, m)) {
						DCHumidity cur = ClimateAPI.registerBlock.getHumidity(block, m);
						if (cur == DCHumidity.DRY) {
							ret--;
						} else if (cur != null && cur.getID() > 1) {
							ret++;
						}
					} else if (world.getBlockState(p2).getMaterial() == Material.WATER) {
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

	// Airの数をカウントして決定
	@Override
	public DCAirflow getAirflow(World world, BlockPos pos, int r, boolean h) {
		if (world == null || pos == null) {
			return DCAirflow.NORMAL;
		}
		if (r < 0 || r > 15)
			r = 1;

		// biomeの基礎通気
		DCAirflow air = ClimateAPI.register.getAirflow(world, pos);

		int count = 0; // 空気量カウント
		boolean hasWind = false;
		boolean hasBlow = false;

		// biomeベース通気 -> 屋内ではNORMALになる
		if (!hasRoof(world, pos)) {
			if (pos.getY() > 135) {
				air = DCAirflow.WIND;
				hasWind = true;
				hasBlow = true;
			} else {
				air = DCAirflow.FLOW;
				hasWind = true;
			}
		}
		if (CoreConfigDC.tightUnderworld && pos.getY() < 30) {
			air = DCAirflow.TIGHT;
		}

		/*
		 * blockの値
		 */
		int h1 = h ? 0 : r;
		for (int x = pos.getX() - r; x <= pos.getX() + r; x++) {
			for (int z = pos.getZ() - r; z <= pos.getZ() + r; z++) {
				for (int y = pos.getY() - h1; y <= pos.getY() + h1; y++) {
					BlockPos p2 = new BlockPos(x, y, z);
					IBlockState s2 = world.getBlockState(p2);
					Block block = s2.getBlock();
					int m = block.getMetaFromState(world.getBlockState(p2));
					if (block instanceof IAirflowTile) {
						DCAirflow current = ((IAirflowTile) block).getAirflow(world, pos, p2);
						if (current != null && current.getID() > 0) {
							if (current.getID() > 1) {
								if (current == DCAirflow.WIND) {
									hasBlow = true;
								}
								hasWind = true;
							}
							count++;
						}
					} else if (ClimateAPI.registerBlock.isRegisteredAir(block, m)) {
						DCAirflow cur = ClimateAPI.registerBlock.getAirflow(block, m);
						if (cur != null && cur.getID() > 0) {
							count++;
							if (cur.getID() > 1) {
								hasWind = true;
								if (cur == DCAirflow.WIND)
									hasBlow = true;
							}
						}
					} else if (s2.getMaterial() == Material.PLANTS || s2.getMaterial() == Material.VINE) {
						count++;
					}
				}
			}
		}
		if (hasBlow) {
			return DCAirflow.WIND;
		}
		if (count > 2) {
			DCAirflow ret = air;
			if (hasWind) {
				ret = DCAirflow.FLOW;
				if (WeatherChecker.getWindOffset(world.provider.getDimension(),
						world.provider.doesWaterVaporize()) > 0) {
					ret = DCAirflow.getTypeByID(ret.getID() + 1);
				}
			} else {
				ret = air;
			}

			return ret;

		} else {
			return DCAirflow.TIGHT;
		}
	}

	boolean hasRoof(World world, BlockPos pos) {
		BlockPos pos2 = pos.up();
		int lim = pos.getY() + 16;
		if (world.provider.hasSkyLight()) {
			lim = pos.getY() + 12;
		}
		while (pos2.getY() < lim && pos2.getY() < world.getActualHeight()) {
			IBlockState state = world.getBlockState(pos2);
			Block block = world.getBlockState(pos2).getBlock();
			if (!world.isAirBlock(pos2) && (block.getLightOpacity(state, world, pos2) > 0.0F
					|| state.getMobilityFlag() == EnumPushReaction.NORMAL)) {
				return true;
			}
			pos2 = pos2.up();
		}
		return false;
	}
}
