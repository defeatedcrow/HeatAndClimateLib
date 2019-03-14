package defeatedcrow.hac.core.plugin.baubles;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import baubles.api.BaublesApi;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DCPluginBaubles {

	public static List<ItemStack> getBaublesCharm(EntityPlayer player, CharmType type) {
		ArrayList<ItemStack> ret = Lists.newArrayList();
		for (int i = 0; i < 7; i++) {
			ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
			if (!DCUtil.isEmpty(item) && item.getItem() instanceof IJewelCharm) {
				IJewelCharm charm = (IJewelCharm) item.getItem();
				if (type == null || charm.getCharmType(item.getItemDamage()) == type) {
					ret.add(item);
				}
			}
		}

		return ret;
	}

	public static boolean hasBaublesCharm(EntityPlayer player, ItemStack item) {
		if (item.getItem() instanceof CharmItemBase) {
			int[] num = ((CharmItemBase) item.getItem()).getBaubleType(item).getValidSlots();
			if (num != null) {
				for (int i : num) {
					ItemStack item2 = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
					if (!DCUtil.isEmpty(item2) && !DCUtil.isEmpty(item)) {
						return item2.getItem() == item.getItem() && item2.getItemDamage() == item.getItemDamage();
					}
				}
			}
		}

		return false;
	}

	public static void setBaublesCharmEmpty(EntityPlayer player) {
		BaublesApi.getBaublesHandler(player).setStackInSlot(6, ItemStack.EMPTY);
		player.inventory.markDirty();
	}

}
