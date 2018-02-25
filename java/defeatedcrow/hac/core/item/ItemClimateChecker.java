package defeatedcrow.hac.core.item;

import java.util.List;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.base.DCItem;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemClimateChecker extends DCItem {

	@Override
	public int getMaxMeta() {
		return 0;
	}

	@Override
	public EnumActionResult onItemUse2(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		if (!world.isAirBlock(pos)) {
			if (!world.isRemote) {
				IBlockState state = world.getBlockState(pos);
				IClimate c = null;
				if (state.getBlock() instanceof IClimateObject) {
					IClimateObject co = (IClimateObject) state.getBlock();
					if (co.checkingRange() != null) {
						c = ClimateAPI.calculator.getClimate(world, pos, co.checkingRange());
					}
				}

				if (c == null) {
					// heatのみ2ブロック
					c = ClimateAPI.calculator.getClimate(world, pos);
				}

				if (c != null) {
					player.addChatMessage(new TextComponentString("== Current Climate =="));
					player.addChatMessage(new TextComponentString("Temperature: " + c.getHeat().name()));
					player.addChatMessage(new TextComponentString("Humidity: " + c.getHumidity().name()));
					player.addChatMessage(new TextComponentString("Airflow: " + c.getAirflow().name()));
					if (ClimateCore.isDebug) {
						player.addChatMessage(
								new TextComponentString("Climate int: " + Integer.toBinaryString(c.getClimateInt())));
						// weather
						int time = DCTimeHelper.currentTime(world);
						int dim = world.provider.getDimension();
						int count = 0;
						int sun = 0;
						float rain = 0F;
						if (WeatherChecker.rainPowerMap.containsKey(dim)) {
							rain = WeatherChecker.rainPowerMap.get(dim);
						}
						if (WeatherChecker.rainCountMap.containsKey(dim)) {
							count = WeatherChecker.rainCountMap.get(dim);
						}
						if (WeatherChecker.sunCountMap.containsKey(dim)) {
							sun = WeatherChecker.sunCountMap.get(dim);
						}

						DCLogger.debugLog("== current weather info ==");
						DCLogger.debugLog("remote world: " + world.isRemote + ". time: " + time);
						DCLogger.debugLog("world rain: " + world.rainingStrength);
						DCLogger.debugLog("rain: " + rain + ", time: " + count);
						DCLogger.debugLog("sun time: " + sun);

						if (player.isSneaking()) {
							DCLogger.debugLog("== forced smelting ==");
							IBlockState st = world.getBlockState(pos);
							Block block = st.getBlock();
							if (st != null && block != null) {
								int meta = block.getMetaFromState(st);
								DCLogger.debugLog("target: " + block.getLocalizedName() + " " + meta);
								IClimate clm = ClimateAPI.calculator.getClimate(world, pos);
								IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm,
										new ItemStack(block, 1, meta));
								if (recipe != null && recipe.matchClimate(clm) && recipe.additionalRequire(world, pos)
										&& recipe.hasPlaceableOutput() == 1) {
									if (recipe.getOutput() != null
											&& recipe.getOutput().getItem() instanceof ItemBlock) {
										Block retB = Block.getBlockFromItem(recipe.getOutput().getItem());
										int retM = recipe.getOutput().getMetadata();
										IBlockState ret = retB.getStateFromMeta(retM);
										world.setBlockState(pos, ret, 2);
										world.notifyNeighborsOfStateChange(pos, ret.getBlock());
										DCLogger.debugLog("after: " + ret.getBlock().getLocalizedName() + " " + retM);
									}
								}
							}
						}
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {
		tooltip.add(I18n.translateToLocal("dcs.climate.tip.checker"));
	}

	@Override
	public String getTexPath(int meta, boolean f) {
		String s = "items/tool/checker";
		if (f) {
			s = "textures/" + s;
		}
		return ClimateCore.PACKAGE_ID + ":" + s;
	}

	@Override
	public String[] getNameSuffix() {
		String[] s = {
				"normal"
		};
		return s;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

}
