package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.ClimateSupplier;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
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

			if (block instanceof IClimateObject) {
				return;
			}

			if (block == Blocks.GRASS || block.isLeaves(st, world, p)) {
				return;
			}

			int meta = block.getMetaFromState(st);
			for (BlockSet set : CoreConfigDC.blackListBlock) {
				if (block == set.block) {
					if (set.meta == 32767 || meta == set.meta) {
						return;
					}
				}
			}

			ClimateSupplier clm = new ClimateSupplier(world, p);
			boolean roof = hasRoof(world, p);

			// 直接指定の仕様
			if (BlockFarmland.class.isInstance(block)) {
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, p, 4, true);
				DCHumidity hum2 = ClimateAPI.calculator.getHumidity(world, p);
				// 耕地はWET以上の湿度では湿る
				if (hum.getID() > 1 || hum2.getID() > 1) {
					IBlockState next = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
					world.setBlockState(p, next, 2);
					// DCLogger.debugLog("farmland update");
					event.setCanceled(true);
				}
				return;
			} else if (block instanceof IGrowable) {
				// WETの参照posを真下に
				if (block == Blocks.TALLGRASS) {
					// WARMかつWETの場合に成長が促進される
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
						IClimate clm2 = ClimateAPI.calculator.getClimate(world, p.down());
						if ((clm.get().getHeat() == DCHeatTier.WARM || clm.get().getHeat() == DCHeatTier.HOT)
								&& clm2.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
						}
					}
					return;
				} else if (block != Blocks.DOUBLE_PLANT) {
					// WARMかつWETの場合に成長が促進され、COLD以下の場合は成長が遅くなる
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
						IClimate clm2 = ClimateAPI.calculator.getClimate(world, p.down());
						if ((clm.get().getHeat() == DCHeatTier.WARM || clm.get().getHeat() == DCHeatTier.HOT)
								&& clm2.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
							// DCLogger.debugLog("Grow!");
						} else if (clm.get().getHeat().getTier() < -1) {
							event.setCanceled(true);
							// DCLogger.debugLog("Grow Canceled");
						}
					}
					return;
				}
			}
			/*
			 * ICE
			 * 屋内かつCOOL以下であれば氷が溶けなくなり、COLDより冷たいTierでは周囲を強制凍結
			 * WARM以上で強制溶解
			 */
			else if (block == Blocks.ICE) {
				DCHeatTier h2 = ClimateAPI.register.getHeatTier(world, p);
				if (clm.get().getHeat().getTier() < 0) {
					if (clm.get().getHeat().getTier() == DCHeatTier.ABSOLUTE.getTier()) {
						world.setBlockState(p, Blocks.PACKED_ICE.getDefaultState(), 2);
						world.notifyNeighborsOfStateChange(p, Blocks.PACKED_ICE, false);
						event.setCanceled(true);
						return;
						// DCLogger.debugLog("Freeze!!");
					} else if (roof) {
						event.setCanceled(true);
						return;
					}
				}
				if (!roof && h2.getTier() >= 0) {
					world.setBlockState(p, Blocks.WATER.getDefaultState(), 2);
					world.notifyNeighborsOfStateChange(p, Blocks.WATER, false);
					event.setCanceled(true);
					return;
					// DCLogger.debugLog("Melted");
				}
				/*
				 * SNOW
				 * COOL以下であれば氷が溶けなくなり、WARM以上で強制溶解
				 */
				return;
			} else if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER) {
				DCHeatTier h2 = ClimateAPI.register.getHeatTier(world, p);
				if (clm.get().getHeat().getTier() < 0 && roof) {
					event.setCanceled(true);
					return;
				}
				if (!roof && h2.getTier() >= 0) {
					world.setBlockToAir(p);
					event.setCanceled(true);
					return;
					// DCLogger.debugLog("Melted");
				}
				return;
			}

			boolean f2 = false;
			// レシピ判定
			if (CoreConfigDC.enableVanilla) {
				IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, new ItemStack(block, 1, meta));
				if (recipe != null && recipe.matchClimate(clm.get()) && recipe.additionalRequire(world, p)
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
				if (clm.get().getHeat().getTier() > DCHeatTier.SMELTING.getTier()) {

					if (clm.get().getHeat() == DCHeatTier.INFERNO) {
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

			if (f2 && CoreConfigDC.enableVanilla && !block.getTickRandomly()) {

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

	boolean hasRoof(World world, BlockPos pos) {
		BlockPos pos2 = pos.up();
		int lim = pos.getY() + 16;
		if (world.provider.hasSkyLight()) {
			lim = pos.getY() + 5;
		}
		if (world.canSeeSky(pos))
			return false;
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

}
