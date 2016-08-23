package defeatedcrow.hac.core.event;

import net.minecraft.block.Block;
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
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.recipe.DCBlockFreezeEvent;
import defeatedcrow.hac.api.recipe.DCBlockUpdateEvent;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;

public class BlockUpdateDC {

	@SubscribeEvent
	public void onUpdate(DCBlockUpdateEvent event) {
		if (!event.world.isRemote && event.state != null) {
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
				// 耕地はWET以上の湿度では湿る
				if (hum.getID() > 1) {
					world.setBlockState(p, block.getStateFromMeta(7), 3);
					// DCLogger.debugLog("farmland update");
					event.setCanceled(true);
					return;
				}
			} else if (block instanceof IGrowable && !(block instanceof IClimateCrop)) {
				if (block == Blocks.TALLGRASS) {
					// HOTかつWETの場合に成長が促進され、COLD以下の場合は成長が遅くなる
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(4) == 0) {
						if (clm.getHeat() == DCHeatTier.HOT && clm.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
						}
					}
				} else if (block != Blocks.DOUBLE_PLANT && block != Blocks.GRASS) {
					// HOTかつWETの場合に成長が促進され、COLD以下の場合は成長が遅くなる
					IGrowable grow = (IGrowable) block;
					if (grow.canGrow(world, p, st, false) && world.rand.nextInt(4) == 0) {
						if (clm.getHeat() == DCHeatTier.HOT && clm.getHumidity() == DCHumidity.WET) {
							grow.grow(world, world.rand, p, st);
							DCLogger.debugLog("Grow!");
						} else if (clm.getHeat().getTier() < 0) {
							event.setCanceled(true);
							DCLogger.debugLog("Grow Canceled");
						}
					}
				}

			}
			/*
			 * ICE
			 * COLD以下であれば氷が溶けなくなり、FROSTでは周囲を強制凍結
			 * HOT以上で強制溶解
			 */
			else if (block == Blocks.ICE) {
				if (clm.getHeat().getTier() < 0) {
					// 隣接4つもチェックする
					int r1 = world.rand.nextInt(4);
					if (clm.getHeat().getTier() < -1) {
						EnumFacing facing = EnumFacing.getHorizontal(r1);
						BlockPos p2 = p.offset(facing);
						if (canFreezePos(world, p2)) {
							world.setBlockState(p2, Blocks.ICE.getDefaultState());
							world.notifyBlockOfStateChange(p2, Blocks.ICE);
							event.setCanceled(true);
							DCLogger.debugLog("Freeze!!");
						}
					}
					event.setCanceled(true);
				} else if (clm.getHeat().getTier() > 0) {
					world.setBlockState(p, Blocks.WATER.getDefaultState());
					world.notifyBlockOfStateChange(p, Blocks.WATER);
					event.setCanceled(true);
					DCLogger.debugLog("Melted");
				}
				/*
				 * SNOW
				 * COLD以下であれば氷が溶けなくなり、HOT以上で強制溶解
				 */
			} else if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER) {
				if (clm.getHeat().getTier() < 0) {
					event.setCanceled(true);
				} else if (clm.getHeat().getTier() > 0) {
					world.setBlockToAir(p);
					event.setCanceled(true);
					DCLogger.debugLog("Melted");
				}
			}

			// レシピ判定
			IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, new ItemStack(block, 1, meta));
			if (recipe != null && recipe.matchClimate(clm) && recipe.hasPlaceableOutput() == 1) {
				if (recipe.getOutput() != null && recipe.getOutput().getItem() instanceof ItemBlock) {
					Block retB = Block.getBlockFromItem(recipe.getOutput().getItem());
					int retM = recipe.getOutput().getMetadata();
					IBlockState ret = retB.getStateFromMeta(retM);
					world.setBlockState(p, ret, 3);
					world.notifyBlockOfStateChange(p, ret.getBlock());
					event.setCanceled(true);
					DCLogger.debugLog("Update climate change!");
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
				world.setBlockState(pos, Blocks.ICE.getDefaultState());
				world.notifyBlockOfStateChange(pos, Blocks.ICE);
				event.setResult(Result.ALLOW);
				DCLogger.debugLog("Success to freeze!!");
			}
		}
	}

	boolean hasRoof(World world, BlockPos pos) {
		if (world.provider.getHasNoSky()) {
			return false;
		}
		BlockPos pos2 = pos.up();
		int lim = pos.getY() + 16;
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
				return clm.getTier() < -1;
			}
		}
		return false;
	}

}
