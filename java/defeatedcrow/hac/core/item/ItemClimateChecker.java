package defeatedcrow.hac.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
import defeatedcrow.hac.api.climate.ClimateAPI;
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
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && !world.isAirBlock(pos)) {
			IBlockState state = world.getBlockState(pos);
			IClimate c = null;
			IClimateSmelting recipe = null;
			if (state.getBlock() instanceof IClimateObject) {
				IClimateObject co = (IClimateObject) state.getBlock();
				if (co.checkingRange() != null) {
					c = ClimateAPI.calculator.getClimate(world, pos, co.checkingRange());
				}
			}

			if (c == null) {
				// heatのみ2ブロック
				c = ClimateAPI.calculator.getClimate(world, pos, null);
			}

			if (c != null) {
				player.addChatMessage(new TextComponentString("== Current Climate =="));
				player.addChatMessage(new TextComponentString("Temperature: " + c.getHeat().name()));
				player.addChatMessage(new TextComponentString("Humidity: " + c.getHumidity().name()));
				player.addChatMessage(new TextComponentString("Airflow: " + c.getAirflow().name()));
				if (ClimateCore.isDebug) {
					// player.addChatMessage(new TextComponentString("Climate int: " +
					// Integer.toBinaryString(c.getClimateInt())));
				}
				// recipe
				Block block = state.getBlock();
				int i = block.getMetaFromState(state);
				String s = block.getRegistryName() + ":" + i;
				recipe = RecipeAPI.registerSmelting.getRecipe(c, new ItemStack(block, 1, i));
				if (recipe != null) {
					player.addChatMessage(new TextComponentString(s + " ** Climate Smelting Confotable! **"));
				} else {
					player.addChatMessage(new TextComponentString(s));
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
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
		String[] s = { "normal" };
		return s;
	}

}
