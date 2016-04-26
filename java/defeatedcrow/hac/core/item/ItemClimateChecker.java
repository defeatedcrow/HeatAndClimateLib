package defeatedcrow.hac.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.base.DCItem;

public class ItemClimateChecker extends DCItem {

	@Override
	public int getMaxMeta() {
		return 0;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (!world.isRemote && !world.isAirBlock(pos)) {
			IBlockState state = world.getBlockState(pos);
			IClimate c = null;
			IClimateSmelting recipe = null;
			if (state.getBlock() instanceof IClimateObject) {
				IClimateObject co = (IClimateObject) state.getBlock();
				if (co.checkingRange() != null) {
					DCHeatTier heat = ClimateAPI.calculator.getHeatTier(world, pos, co.checkingRange()[0], false);
					DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, co.checkingRange()[1], false);
					DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, co.checkingRange()[2], false);
					c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
				}
			}

			if (c == null) {
				// heatのみ2ブロック
				DCHeatTier heat = ClimateAPI.calculator.getHeatTier(world, pos, 2, false);
				DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, 1, false);
				DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, 1, false);
				c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
			}

			if (c != null) {
				player.addChatMessage(new ChatComponentText("== Current Climate =="));
				player.addChatMessage(new ChatComponentText("Temperature: " + c.getHeat().name()));
				player.addChatMessage(new ChatComponentText("Humidity: " + c.getHumidity().name()));
				player.addChatMessage(new ChatComponentText("Airflow: " + c.getAirflow().name()));
				if (ClimateCore.isDebug) {
					// player.addChatMessage(new ChatComponentText("Climate int: " +
					// Integer.toBinaryString(c.getClimateInt())));
				}
				// recipe
				Block block = state.getBlock();
				int i = block.getMetaFromState(state);
				String s = block.getRegistryName() + ":" + i;
				recipe = RecipeAPI.registerSmelting.getRecipe(c, new ItemStack(block, 1, i));
				if (recipe != null) {
					player.addChatMessage(new ChatComponentText(s + " ** Climate Smelting Confotable! **"));
				} else {
					player.addChatMessage(new ChatComponentText(s));
				}
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(StatCollector.translateToLocal("dcs.climate.tip.checker"));
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
		String[] s = { "normal" };
		return s;
	}

}
