package defeatedcrow.hac.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

// 色々不足しているもの
public class DCUtil {

	public static Random rand = new Random();

	public static boolean isEmpty(ItemStack item) {
		return item == null || item.getItem() == null;
	}

	public static boolean reduceStackSize(ItemStack item, int i) {
		if (!isEmpty(item)) {
			if (item.stackSize > i) {
				item.stackSize -= i;
				return false;
			} else {
				item.stackSize = 0;
				return true;
			}
		}
		return true;
	}

	public static ItemStack reduceAndDeleteStack(ItemStack item, int i) {
		if (reduceStackSize(item, i)) {
			return null;
		} else {
			return item;
		}
	}

	/*
	 * stacksize以外の比較
	 */
	public static boolean isSameItem(ItemStack i1, ItemStack i2, boolean nullable) {
		if (isEmpty(i1) || isEmpty(i2)) {
			return nullable;
		} else {
			if (i1.getItem() == i2.getItem() && i1.getItemDamage() == i2.getItemDamage()) {
				NBTTagCompound t1 = i1.getTagCompound();
				NBTTagCompound t2 = i2.getTagCompound();
				if (t1 == null && t2 == null) {
					return true;
				} else {
					return t1.equals(t2);
				}
			}
			return false;
		}
	}

	/*
	 * stacksize以外の比較、ワイルドカード付き
	 */
	public static boolean isIntegratedItem(ItemStack i1, ItemStack i2, boolean nullable) {
		if (isEmpty(i1) || isEmpty(i2)) {
			return nullable;
		} else {
			if (i1.getItem() == i2.getItem()) {
				if (i1.getItemDamage() == i2.getItemDamage() || i2.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					NBTTagCompound t1 = i1.getTagCompound();
					NBTTagCompound t2 = i2.getTagCompound();
					if (t1 == null && t2 == null) {
						return true;
					} else {
						return t1.equals(t2);
					}
				}
			}
			return false;
		}
	}

	/*
	 * stackが重ねられるかどうか
	 */
	public static boolean isStackable(ItemStack i1, ItemStack i2) {
		if (isSameItem(i1, i2, false)) {
			return i1.stackSize <= (i2.getMaxStackSize() - i2.stackSize);
		}
		return false;
	}

	/*
	 * 直線距離
	 */
	public static double getDist(BlockPos p1, BlockPos p2) {
		double x = Math.abs(p1.getX() - p2.getX());
		double y = Math.abs(p1.getY() - p2.getY());
		double z = Math.abs(p1.getZ() - p2.getZ());
		return Math.sqrt(x * x + y * y + z * z);
	}

	/*
	 * Block数での距離
	 */
	public static int getDistInt(BlockPos p1, BlockPos p2) {
		int x = Math.abs(p1.getX() - p2.getX());
		int y = Math.abs(p1.getY() - p2.getY());
		int z = Math.abs(p1.getZ() - p2.getZ());
		return x + y + z;
	}

	// 防具の登録時の並び
	public static final EntityEquipmentSlot[] SLOTS = new EntityEquipmentSlot[] {
			EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
	};

	// チャームを保持しているかのチェック
	public static Map<Integer, ItemStack> getPlayerCharm(EntityPlayer player, CharmType type) {
		Map<Integer, ItemStack> ret = new HashMap<Integer, ItemStack>();
		if (player == null)
			return ret;
		for (int i = 9; i < 18; i++) {
			ItemStack check = player.inventory.getStackInSlot(i);
			if (!isEmpty(check) && check.getItem() instanceof IJewelCharm) {
				IJewelCharm charm = (IJewelCharm) check.getItem();
				int m = check.getItemDamage();
				if (type == null || charm.getType(m) == type)
					ret.put(i, check);
			}
		}
		return ret;
	}

	// Itemクラスのやつがprotectedだった
	public static RayTraceResult getRayTrace(World world, EntityPlayer player) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		double d0 = player.posX;
		double d1 = player.posY + player.getEyeHeight();
		double d2 = player.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if (player instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
		}
		Vec3d vec3d1 = vec3d.addVector(f6 * d3, f5 * d3, f7 * d3);
		return world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
	}

	public static BlockPos getRayTracePos(World world, EntityPlayer player) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		double d0 = player.posX;
		double d1 = player.posY + player.getEyeHeight();
		double d2 = player.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if (player instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
		}
		Vec3d vec3d1 = vec3d.addVector(f6 * d3, f5 * d3, f7 * d3);
		return new BlockPos(vec3d1);
	}

	// cloudの向き。+X方向固定っぽい
	public static EnumFacing getWorldWind(World world) {
		if (world != null) {
			if (world.provider.getHasNoSky()) {
				return EnumFacing.UP;
			}
		}
		return EnumFacing.EAST;
	}

	public static float getItemResistantData(ItemStack item, boolean isCold) {
		if (DCUtil.isEmpty(item))
			return 0;

		float p = 0F;
		if (isCold) {
			p += DamageAPI.itemRegister.getColdPreventAmount(item);
		} else {
			p += DamageAPI.itemRegister.getHeatPreventAmount(item);
		}
		if (p == 0F && item.getItem() instanceof ItemArmor) {
			ArmorMaterial mat = ((ItemArmor) item.getItem()).getArmorMaterial();
			if (isCold) {
				p += DamageAPI.armorRegister.getColdPreventAmount(mat);
			} else {
				p += DamageAPI.armorRegister.getHeatPreventAmount(mat);
			}
			if (!isCold && EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, item) > 0) {
				p += EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, item) * 1.0F;
			}
		}
		return p;
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
			ClimateCore.LOGGER.warn("Failed to check password...", e);
		}

		get = getStringFromBytes(b);
		DCLogger.debugLog("Get String : " + get);

		if (!get.isEmpty()) {
			boolean match = get.matches("7805f2fa0adc68cd9a8f7cb2135e0b57");
			DCLogger.infoLog("DebugMode : " + match);
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
