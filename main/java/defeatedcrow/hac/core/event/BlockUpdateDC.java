package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.ClimateSupplier;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.hook.DCBlockUpdateEvent;
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

			if (st.getMaterial() == Material.GRASS || block.isLeaves(st, world, p)) {
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
			ClimateSupplier clm_down = new ClimateSupplier(world, p.down());
			boolean roof = hasRoof(world, p);

			// 直接指定の仕様
			if (CoreConfigDC.enableFarmland && BlockFarmland.class.isInstance(block)) {
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, p, 4, true);
				DCHumidity hum2 = clm.get().getHumidity();
				// 耕地はWET以上の湿度では湿る
				if (hum.getID() > 1 || hum2.getID() > 1) {
					IBlockState next = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
					world.setBlockState(p, next, 2);
					// DCLogger.debugLog("farmland update");
					event.setCanceled(true);
				}
				return;
			} else if (CoreConfigDC.enableVanillaCrop && block instanceof IGrowable) {
				// 寒冷地では枯れる
				if (clm.get().getHeat().isCold()) {
					// 寒冷耐性のある作物はスキップ
					if (block instanceof IClimateCrop && ((IClimateCrop) block).getSuitableTemp(st).contains(clm.get()
							.getHeat())) {

					} else {
						// 光が必要
						if (CoreConfigDC.harderCrop && world.getLight(p) < 8) {
							event.setCanceled(true);
						}
						if (CoreConfigDC.harderVanilla && clm.get().getHeat().getTier() < DCHeatTier.FROSTBITE
								.getTier()) {
							if (block == Blocks.TALLGRASS && world.rand.nextInt(3) == 0) {
								world.setBlockState(p, Blocks.DEADBUSH.getDefaultState(), 2);
							} else {
								world.setBlockToAir(p);
							}
						} else {
							int c = 1 - clm.get().getHeat().getTier();
							if (world.rand.nextInt(c) > 0)
								event.setCanceled(true);
						}
					}
				} else if ((clm.get().getHeat() == DCHeatTier.WARM || clm.get().getHeat() == DCHeatTier.HOT) && clm_down
						.get().getHumidity() == DCHumidity.WET) {
					// WETの参照posを真下に
					if (block == Blocks.TALLGRASS) {
						// WARMかつWETの場合に成長が促進される
						IGrowable grow = (IGrowable) block;
						if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
							grow.grow(world, world.rand, p, st);
						}
					} else if (block != Blocks.DOUBLE_PLANT) {
						IGrowable grow = (IGrowable) block;
						if (grow.canGrow(world, p, st, false) && world.rand.nextInt(5) == 0) {
							grow.grow(world, world.rand, p, st);
						}
					}
				}
				return;
			}

			/*
			 * ICE
			 * 屋内かつCOOL以下であれば氷が溶けなくなり、COLDより冷たいTierでは周囲を強制凍結
			 * WARM以上で強制溶解
			 */
			if (CoreConfigDC.enableIce) {
				if (block == Blocks.ICE) {
					DCHeatTier h2 = clm.get().getHeat();
					float f2 = ClimateAPI.register.getBiomeTemp(world, p);
					if (h2.isCold()) {
						if (clm.get().getHeat() == DCHeatTier.ABSOLUTE) {
							world.setBlockState(p, Blocks.PACKED_ICE.getDefaultState(), 2);
							world.notifyNeighborsOfStateChange(p, Blocks.PACKED_ICE, false);
							event.setCanceled(true);
							return;
							// DCLogger.debugLog("Freeze!!");
						} else if (roof || f2 < 0.15F) {
							event.setCanceled(true);
							return;
						}
					}
					if ((!roof && f2 >= 0.4F) || h2.getTier() > DCHeatTier.NORMAL.getTier()) {
						world.setBlockState(p, Blocks.WATER.getDefaultState(), 2);
						world.notifyNeighborsOfStateChange(p, Blocks.WATER, false);
						event.setCanceled(true);
						return;
						// DCLogger.debugLog("Melted");
					}
					return;
				}
			}
			if (CoreConfigDC.enableSnow) {
				if (block == Blocks.SNOW_LAYER) {
					DCHeatTier h2 = clm.get().getHeat();
					float f2 = ClimateAPI.register.getBiomeTemp(world, p);
					/*
					 * SNOW
					 * バニラの雨/雪境界の0.15Fを境にする
					 */
					if ((clm.get().getHeat().isCold() && roof) || f2 < 0.15F) {
						event.setCanceled(true);
						return;
					}
					if ((!roof && f2 >= 0.15F) || h2.getTier() > DCHeatTier.NORMAL.getTier()) {
						world.setBlockToAir(p);
						event.setCanceled(true);
						return;
						// DCLogger.debugLog("Snow Melted");
					}
					return;
				}
			}

			boolean f2 = false;
			// レシピ判定
			if (CoreConfigDC.enableVanilla) {
				IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, new ItemStack(block, 1, meta));
				if (recipe != null && recipe.matchClimate(clm.get()) && recipe.additionalRequire(world, p) && recipe
						.hasPlaceableOutput() == 1) {
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
				if (clm.get().getHeat().getTier() > DCHeatTier.SMELTING.getTier() && world.rand.nextInt(3) == 0) {

					if (clm.get().getHeat() == DCHeatTier.INFERNO) {
						// 融解
						if (st.getMaterial() == Material.ROCK || st.getMaterial() == Material.SAND || st
								.getMaterial() == Material.GROUND) {
							if (st.getBlock() != Blocks.OBSIDIAN && !ThermalInsulationUtil.BLOCK_MAP
									.containsKey(block)) {
								world.setBlockState(p, Blocks.LAVA.getDefaultState(), 3);
							}
						}
					}

					if (st.getBlock().isFlammable(world, p, EnumFacing.UP) && world.isAirBlock(p.up()) && Blocks.FIRE
							.canPlaceBlockAt(world, p.up())) {
						world.setBlockState(p.up(), Blocks.FIRE.getDefaultState());
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
