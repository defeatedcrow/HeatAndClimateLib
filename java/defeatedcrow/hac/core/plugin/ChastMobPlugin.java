package defeatedcrow.hac.core.plugin;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.magic.IJewelAmulet;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import schr0.chastmob.entity.EntityChast;

public class ChastMobPlugin {

	public static Map<Integer, ItemStack> getAmulet(EntityLivingBase living) {
		Map<Integer, ItemStack> ret = new HashMap<Integer, ItemStack>();
		if (living instanceof EntityChast) {
			IInventory inv = ((EntityChast) living).getInventoryChastMain();
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack check = inv.getStackInSlot(i);
				if (!DCUtil.isEmpty(check) && check.getItem() instanceof IJewelAmulet) {
					ret.put(i, check);
				}
			}
		}
		return ret;
	}

	public static boolean isChastMob(EntityLivingBase living) {
		return living instanceof EntityChast;
	}
}
