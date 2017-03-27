package defeatedcrow.hac.core.base;

import java.util.Random;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * ClimateSmeltingレシピを持つBlockのテンプレート
 */
public class ClimateBlock extends Block implements IClimateObject {

	public final boolean forceUpdate;

	public ClimateBlock(Material m, boolean f) {
		super(m);
		forceUpdate = f;
	}

	@Override
	public int tickRate(World world) {
		return 40;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (this.isForcedTickUpdate()) {
			world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(21));
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (!worldIn.isRemote && state != null && state.getBlock() != null) {
			IClimate clm = this.onUpdateClimate(worldIn, pos, state);
			if (!this.onClimateChange(worldIn, pos, state, clm) && this.isForcedTickUpdate()) {
				worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(21));
			}
		}
	}

	@Override
	public IClimate onUpdateClimate(World world, BlockPos pos, IBlockState state) {
		DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(world, pos, checkingRange()[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, checkingRange()[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, checkingRange()[2], false);
		IClimate c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return c;
	}

	@Override
	public boolean onClimateChange(World world, BlockPos pos, IBlockState state, IClimate clm) {
		if (clm != null) {
			DCHeatTier heat = clm.getHeat();
			DCHumidity hum = clm.getHumidity();
			DCAirflow air = clm.getAirflow();
			int meta = this.damageDropped(state);
			ItemStack check = new ItemStack(this, 1, meta);
			IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, check);
			if (recipe != null && recipe.additionalRequire(world, pos)) {
				ItemStack output = recipe.getOutput();
				if (!DCUtil.isEmpty(output) && output.getItem() instanceof ItemBlock) {
					Block ret = ((ItemBlock) output.getItem()).block;
					IBlockState retS = ret.getStateFromMeta(output.getMetadata());
					if (world.setBlockState(pos, retS, 2)) {
						world.notifyBlockOfStateChange(pos, ret);

						// 効果音
						if (playSEOnChanging(meta)) {
							world.playSound(null, pos, getSE(meta), SoundCategory.BLOCKS, 0.8F, 2.0F);
							DCLogger.debugLog("Smelting! " + output.getDisplayName());
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public SoundEvent getSE(int meta) {
		return SoundEvents.BLOCK_LAVA_EXTINGUISH;
	}

	@Override
	public boolean playSEOnChanging(int meta) {
		return true;
	}

	@Override
	public boolean isForcedTickUpdate() {
		return forceUpdate;
	}

	@Override
	public int[] checkingRange() {
		return CoreConfigDC.ranges;
	}

}
