package defeatedcrow.hac.core.plugin;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ChastMobPlugin {

	public static Map<Integer, ItemStack> getCharms(EntityLivingBase living) {
		Map<Integer, ItemStack> ret = new HashMap<Integer, ItemStack>();
		if (living instanceof schr0.chastmob.entity.EntityChast) {
			IInventory inv = ((schr0.chastmob.entity.EntityChast) living).getInventoryMain();
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack check = inv.getStackInSlot(i);
				if (!DCUtil.isEmpty(check) && check.getItem() instanceof IJewelCharm) {
					ret.put(i, check);
				}
			}
		}
		return ret;
	}

	public static boolean isChastMob(EntityLivingBase living) {
		return living instanceof schr0.chastmob.entity.EntityChast;
	}
}
