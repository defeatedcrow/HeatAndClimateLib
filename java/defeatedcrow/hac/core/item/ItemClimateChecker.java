package defeatedcrow.hac.core.item;

import java.util.List;

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
import defeatedcrow.hac.api.climate.IClimate;
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
		if (!world.isRemote) {
			IClimate c = ClimateAPI.calculator.getClimate(world, pos, 1);
			if (c != null) {
				player.addChatMessage(new ChatComponentText("== Current Climate =="));
				player.addChatMessage(new ChatComponentText("Temperature: " + c.getHeat().name()));
				player.addChatMessage(new ChatComponentText("Humidity: " + c.getHumidity().name()));
				player.addChatMessage(new ChatComponentText("Airflow: " + c.getAirflow().name()));
				if (ClimateCore.isDebug) {
					player.addChatMessage(new ChatComponentText("Climate int: " + Integer.toBinaryString(c.getClimateInt())));
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
