package defeatedcrow.hac.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.DCLogger;

// 色々不足しているもの
public class DCUtil {

	public static double getDist(BlockPos p1, BlockPos p2) {
		double x = Math.abs(p1.getX() - p2.getX());
		double y = Math.abs(p1.getY() - p2.getY());
		double z = Math.abs(p1.getZ() - p2.getZ());
		return Math.sqrt(x * x + y * y + z * z);
	}

	// 防具の登録時の並び
	public static final EntityEquipmentSlot[] SLOTS = new EntityEquipmentSlot[] {
			EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET };

	public static Map<Integer, ItemStack> getPlayerCharm(EntityPlayer player, CharmType type) {
		Map<Integer, ItemStack> ret = new HashMap<Integer, ItemStack>();
		if (player == null)
			return ret;
		for (int i = 9; i < 18; i++) {
			ItemStack check = player.inventory.getStackInSlot(i);
			if (check != null && check.getItem() != null && check.getItem() instanceof IJewelCharm) {
				IJewelCharm charm = (IJewelCharm) check.getItem();
				int m = check.getItemDamage();
				if (charm.getType(m) == type)
					ret.put(i, check);
			}
		}
		return ret;
	}

	// デバッグモード
	public static boolean checkDebugModePass(String pass) {
		byte[] b = null;
		String get = "";
		MessageDigest md5;

		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(pass.getBytes());
			b = md5.digest();
		} catch (NoSuchAlgorithmException e) {
			DCLogger.LOGGER.warn("Failed to check password...", e);
		}

		get = getStringFromBytes(b);
		DCLogger.debugLog("Get String : " + get);

		if (!get.isEmpty()) {
			boolean match = get.matches("7805f2fa0adc68cd9a8f7cb2135e0b57");
			DCLogger.LOGGER.info("DebugMode : " + match);
			return match;
		}

		return true;
	}

	private static String getStringFromBytes(byte[] b) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {

			if ((b[i] & 0xff) < 0x10) {
				builder.append("0");
			}
			builder.append(Integer.toHexString(0xff & b[i]));
		}

		return builder.toString();
	}

}
