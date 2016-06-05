package defeatedcrow.hac.core.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.DCLogger;

public class NotifyClimateEvent {

	@SubscribeEvent
	public void onNotify(BlockEvent.NeighborNotifyEvent event) {
		IBlockState state = event.getState();
		World world = event.getWorld();
		BlockPos pos = event.getPos();

		if (!world.isRemote && !world.isAirBlock(pos)) {
			Block block = world.getBlockState(pos).getBlock();
			int meta = block.getMetaFromState(state);
			if (block instanceof IClimateObject || block instanceof BlockContainer) {

			} else {
				DCHeatTier heat = ClimateAPI.calculator.getHeatTier(world, pos, 2, false);
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, 1, false);
				DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, 1, false);
				IClimate c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
				if (RecipeAPI.registerSmelting.getRecipe(c, new ItemStack(block, 1, meta)) != null) {
					if (this.onClimateChange(world, pos, state, c, new BlockSet(block, meta))) {
						// event.setCanceled(true);
					}
				}
			}
		}
	}

	private boolean onClimateChange(World world, BlockPos pos, IBlockState state, IClimate clm, BlockSet set) {
		if (clm != null) {
			DCHeatTier heat = clm.getHeat();
			DCHumidity hum = clm.getHumidity();
			DCAirflow air = clm.getAirflow();
			int meta = set.meta;
			ItemStack check = new ItemStack(set.block, 1, meta);
			IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, check);
			if (recipe != null) {
				ItemStack output = recipe.getOutput();
				if (output != null && output.getItem() instanceof ItemBlock) {
					Block ret = ((ItemBlock) output.getItem()).block;
					IBlockState retS = ret.getStateFromMeta(output.getItemDamage());
					if (world.setBlockState(pos, retS, 3)) {
						world.markBlockRangeForRenderUpdate(pos, pos.down());

						world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 2.0F);
						DCLogger.debugLog("Smelting! " + output.getDisplayName());
						return true;
					}
				}
			}
		}
		return false;
	}

}
