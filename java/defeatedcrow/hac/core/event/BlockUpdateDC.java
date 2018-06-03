package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.recipe.DCBlockFreezeEvent;
import defeatedcrow.hac.api.recipe.DCBlockUpdateEvent;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.ThermalInsulationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockUpdateDC {

	@SubscribeEvent
	public void onUpdate(DCBlockUpdateEvent event) {
		if (event.world != null && !event.world.isRemote && event.state != null) {
			World world = event.world;
			BlockPos p = event.pos;
			IBlockState st = event.state;
			Block block = st.getBlock();

			boolean f = true;

			if (block == null || st.getMaterial() == Material.AIR)
				return;

			if (p.getY() < 0 || p.getY() > 255)
				return;

			int meta = block.getMetaFromState(st);
			IClimate clm = ClimateAPI.calculator.getClimate(world, p);
			boolean roof = hasRoof(world, p);

			// 直接指定の仕様
			if (block == Blocks.FARMLAND) {
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, p, 4, true);
				DCHumidity hum2 = ClimateAPI.calculator.getHumidity(world, p);
				// 耕地はWET以上の湿度では湿る
				if (hum.getID() > 1 || hum2.getID() > 1) {
					IBlockState next = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
					world.setBlockState(p, next, 2);
					// DCLogger.debugLog("farmland update");
					event.setCanceled(true);
					return;
				}
			} else if (block instanceof IGrowable) {
				// WETの参照posを真下に
				IClimate clm2 = ClimateAPI.calculator.getClimate(world, p.down());
				if (block == Blocks.TALLGRASS) {
					// WARMかつWETの場合に成長が促進される
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
						if ((clm.getHeat() == DCHeatTier.WARM || clm.getHeat() == DCHeatTier.HOT)
								&& clm2.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
						}
					}
				} else if (block == Blocks.GRASS) {
					// なにもしない
				} else if (block instanceof IClimateCrop) {
					// WARMかつWETの場合に成長が促進されるが、バニラ植物ほど加速はしない
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(10) == 0) {
						if ((clm.getHeat() == DCHeatTier.WARM || clm.getHeat() == DCHeatTier.HOT)
								&& clm2.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
						}
					}
				} else if (block != Blocks.DOUBLE_PLANT) {
					// WARMかつWETの場合に成長が促進され、COLD以下の場合は成長が遅くなる
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
						if ((clm.getHeat() == DCHeatTier.WARM || clm.getHeat() == DCHeatTier.HOT)
								&& clm2.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
							// DCLogger.debugLog("Grow!");
						} else if (clm.getHeat().getTier() < -1) {
							event.setCanceled(true);
							// DCLogger.debugLog("Grow Canceled");
						}
					}
				}
			}
			/*
			 * ICE
			 * COOL以下であれば氷が溶けなくなり、COLDより冷たいTierでは周囲を強制凍結
			 * WARM以上で強制溶解
			 */
			else if (block == Blocks.ICE) {
				if (clm.getHeat().getTier() < 0) {
					// 隣接4つもチェックする
					int r1 = world.rand.nextInt(4);
					if (clm.getHeat().getTier() == DCHeatTier.ABSOLUTE.getTier()) {
						world.setBlockState(p, Blocks.PACKED_ICE.getDefaultState(), 2);
						world.notifyNeighborsOfStateChange(p, Blocks.PACKED_ICE, false);
						event.setCanceled(true);
						// DCLogger.debugLog("Freeze!!");
					}
					event.setCanceled(true);
				} else if (clm.getHeat().getTier() > 0) {
					world.setBlockState(p, Blocks.WATER.getDefaultState(), 2);
					world.notifyNeighborsOfStateChange(p, Blocks.WATER, false);
					event.setCanceled(true);
					// DCLogger.debugLog("Melted");
				}
				/*
				 * SNOW
				 * COOL以下であれば氷が溶けなくなり、WARM以上で強制溶解
				 */
			} else if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER) {
				if (clm.getHeat().getTier() < 0) {
					event.setCanceled(true);
				} else if (clm.getHeat().getTier() > 0) {
					world.setBlockToAir(p);
					event.setCanceled(true);
					// DCLogger.debugLog("Melted");
				}
			}

			boolean f2 = false;
			// レシピ判定
			if (CoreConfigDC.enableVanilla && !(block instanceof IClimateObject)) {
				IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, new ItemStack(block, 1, meta));
				if (recipe != null && recipe.matchClimate(clm) && recipe.additionalRequire(world, p)
						&& recipe.hasPlaceableOutput() == 1) {
					if (recipe.getOutput() != null && recipe.getOutput().getItem() instanceof ItemBlock) {
						Block retB = Block.getBlockFromItem(recipe.getOutput().getItem());
						int retM = recipe.getOutput().getMetadata();
						IBlockState ret = retB.getStateFromMeta(retM);
						world.setBlockState(p, ret, 2);
						world.notifyNeighborsOfStateChange(p, ret.getBlock(), false);
						event.setCanceled(true);
						f2 = true;
						// DCLogger.debugLog("Update climate change!");
					}
				}
			}

			// ハードモード
			if (CoreConfigDC.harderVanilla) {
				if (clm.getHeat().getTier() > DCHeatTier.SMELTING.getTier()) {

					if (clm.getHeat() == DCHeatTier.INFERNO) {
						// 融解
						if (st.getMaterial() == Material.ROCK || st.getMaterial() == Material.SAND
								|| st.getMaterial() == Material.GROUND) {
							if (st.getBlock() != Blocks.OBSIDIAN
									&& !ThermalInsulationUtil.BLOCK_MAP.containsKey(block)) {
								world.setBlockState(p, Blocks.LAVA.getDefaultState(), 2);
								world.notifyNeighborsOfStateChange(p, Blocks.LAVA, false);
							}
						}
					}
					// 自然発火
					if (st.getMaterial().getCanBurn() && world.isAirBlock(p.up())
							&& block.isFlammable(world, p, EnumFacing.UP)) {
						world.setBlockState(p.up(), Blocks.FIRE.getDefaultState(), 2);
						world.notifyNeighborsOfStateChange(p.up(), Blocks.FIRE, false);
					}
				}
			}

			if (f2) {

				for (BlockPos p3 : BlockPos.getAllInBox(p.east().north(), p.west().south())) {
					if (!world.isAirBlock(p3)) {
						Block target = world.getBlockState(p3).getBlock();
						if (!world.isUpdateScheduled(p3, target) && world.rand.nextBoolean())
							world.scheduleUpdate(p3, target, 600 + world.rand.nextInt(600));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onFreeze(DCBlockFreezeEvent event) {
		if (!event.world.isRemote && event.pos != null) {
			World world = event.world;
			BlockPos pos = event.pos;
			IBlockState st = world.getBlockState(pos);

			if (st.getMaterial() == Material.WATER && world.isAirBlock(pos.up()) && canFreezePos(world, pos)) {
				world.setBlockState(pos, Blocks.ICE.getDefaultState(), 2);
				world.notifyNeighborsOfStateChange(pos, Blocks.ICE, false);
				event.setResult(Result.ALLOW);
				// DCLogger.debugLog("Success to freeze!!");
			}
		}
	}

	boolean hasRoof(World world, BlockPos pos) {
		BlockPos pos2 = pos.up();
		int lim = pos.getY() + 16;
		if (world.provider.hasSkyLight()) {
			lim = pos.getY() + 5;
		}
		while (pos2.getY() < lim && pos2.getY() < world.getActualHeight()) {
			IBlockState state = world.getBlockState(pos2);
			Block block = world.getBlockState(pos2).getBlock();
			if (state.getLightOpacity(world, pos2) > 0) {
				return true;
			}
			pos2 = pos2.up();
		}
		return false;
	}

	boolean canFreezePos(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!world.isRemote && pos != null && state != null) {
			Block block = state.getBlock();
			if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && block.getMetaFromState(state) == 0) {
				DCHeatTier clm = ClimateAPI.calculator.getAverageTemp(world, pos);
				return clm.getTier() < DCHeatTier.COLD.getTier();
			}
		}
		return false;
	}

}
