package defeatedcrow.hac.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatTile;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.energy.IWrenchDC;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.plugin.ChastMobPlugin;
import defeatedcrow.hac.core.plugin.baubles.DCPluginBaubles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

// 色々不足しているもの
public class DCUtil {

	public static Random rand = new Random();

	public static boolean isEmpty(ItemStack item) {
		if (item == null) {
			item = ItemStack.EMPTY;
			return true;
		}
		return item.getItem() == null || item.isEmpty();
	}

	public static int getSize(ItemStack item) {
		if (isEmpty(item)) {
			return 0;
		}
		return item.getCount();
	}

	public static ItemStack setSize(ItemStack item, int i) {
		if (!isEmpty(item) && i > 0) {
			item.setCount(i);
			return item;
		}
		return ItemStack.EMPTY;
	}

	public static boolean isEmptyIngredient(Ingredient item) {
		return item == null || item.getMatchingStacks().length < 1 || isEmpty(item.getMatchingStacks()[0]);
	}

	public static int reduceStackSize(ItemStack item, int i) {
		if (!isEmpty(item)) {
			int i1 = Math.min(i, item.getCount());
			item.shrink(i1);
			return i1;
		}
		return 0;
	}

	public static ItemStack redAndDel(ItemStack item, int i) {
		if (!isEmpty(item)) {
			int ret = Math.min(i, item.getCount());
			item.splitStack(ret);
			if (item.getCount() <= 0) {
				item = ItemStack.EMPTY;
			}
			return item;
		}
		return ItemStack.EMPTY;
	}

	public static int addStackSize(ItemStack item, int i) {
		if (!isEmpty(item)) {
			int ret = item.getMaxStackSize() - item.getCount();
			ret = Math.min(i, ret);
			item.grow(ret);
			return ret;
		}
		return 0;
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
				} else if (t1 == null || t2 == null) {
					return false;
				} else {
					return t1.equals(t2);
				}
			}
			return false;
		}
	}

	public static boolean isSameItem2(ItemStack i1, ItemStack i2, boolean nullable) {
		if (isEmpty(i1) && isEmpty(i2)) {
			return nullable;
		} else if (isEmpty(i1) || isEmpty(i2)) {
			return false;
		} else {
			if (i1.getItem() == i2.getItem() && i1.getItemDamage() == i2.getItemDamage()) {
				NBTTagCompound t1 = i1.getTagCompound();
				NBTTagCompound t2 = i2.getTagCompound();
				if (t1 == null && t2 == null) {
					return true;
				} else if (t1 == null || t2 == null) {
					return false;
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
					} else if (t1 == null || t2 == null) {
						return false;
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
			return i1.getCount() <= (i2.getMaxStackSize() - i2.getCount());
		}
		return false;
	}

	public static boolean canInsert(ItemStack i1, ItemStack i2) {
		if (!isEmpty(i1) && isEmpty(i2)) {
			return true;
		} else if (isSameItem(i1, i2, false)) {
			return i1.getCount() <= (i2.getMaxStackSize() - i2.getCount());
		}
		return false;
	}

	/*
	 * 辞書チェック
	 */
	public static boolean matchDicName(String name, ItemStack item) {
		if (name == null || isEmpty(item)) {
			return false;
		} else {
			List<ItemStack> ores = OreDictionary.getOres(name);
			if (ores != null && !ores.isEmpty()) {
				for (ItemStack check : ores) {
					if (isIntegratedItem(item, check, false)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean hasSameDic(ItemStack item, ItemStack check) {
		if (!DCUtil.isEmpty(item) && !DCUtil.isEmpty(check)) {
			int[] ids = OreDictionary.getOreIDs(check);
			int[] ids2 = OreDictionary.getOreIDs(item);
			if (ids.length < 1 || ids2.length < 1) {
				return false;
			}
			for (int id : ids) {
				for (int id2 : ids2) {
					if (id == id2) {
						String s = OreDictionary.getOreName(id);
						if (!s.contains("All") && !s.contains("dye") && !s.contains("list"))
							return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean containItem(NonNullList<ItemStack> list, ItemStack item) {
		if (list.isEmpty() || isEmpty(item)) {
			return false;
		} else {
			for (ItemStack check : list) {
				if (OreDictionary.itemMatches(item, check, false))
					return true;
			}
		}
		return false;
	}

	public static ArrayList<ItemStack> getProcessedList(Object obj) {
		ArrayList<ItemStack> ret = Lists.newArrayList();
		if (obj == null) {
			return ret;
		}
		if (obj instanceof String) {
			ret.addAll(OreDictionary.getOres((String) obj));
		} else if (obj instanceof List && !((List) obj).isEmpty()) {
			ret.addAll((List<ItemStack>) obj);
		} else if (obj instanceof ItemStack) {
			if (!DCUtil.isEmpty((ItemStack) obj))
				ret.add(((ItemStack) obj).copy());
		} else if (obj instanceof Item) {
			ret.add(new ItemStack((Item) obj, 1, 0));
		} else if (obj instanceof Block) {
			ret.add(new ItemStack((Block) obj, 1, 0));
		} else {
			throw new IllegalArgumentException("Unknown Object passed to recipe!");
		}
		return ret;
	}

	public static ArrayList<ItemStack> getProcessedList(Object obj, int count) {
		ArrayList<ItemStack> ret = Lists.newArrayList();
		if (obj == null) {
			return ret;
		}
		if (obj instanceof String) {
			List<ItemStack> ores = new ArrayList<ItemStack>();
			ores.addAll(OreDictionary.getOres((String) obj));
			for (ItemStack o : ores) {
				if (!DCUtil.isEmpty(o)) {
					ret.add(new ItemStack(o.getItem(), count, o.getItemDamage()));
				}
			}
		} else if (obj instanceof List && !((List) obj).isEmpty()) {
			ret.addAll((List<ItemStack>) obj);
		} else if (obj instanceof ItemStack) {
			if (!DCUtil.isEmpty((ItemStack) obj)) {
				ItemStack copy = ((ItemStack) obj).copy();
				copy.setCount(count);
				ret.add(copy);
			}
		} else if (obj instanceof Item) {
			ret.add(new ItemStack((Item) obj, count, 0));
		} else if (obj instanceof Block) {
			ret.add(new ItemStack((Block) obj, count, 0));
		} else {
			throw new IllegalArgumentException("Unknown Object passed to recipe!");
		}
		return ret;
	}

	/* Playerの所持チェック */
	public static boolean isPlayerHeldItem(Item item, EntityPlayer player) {
		if (item == null || player == null)
			return false;

		return isPlayerHeldItem(new ItemStack(item, 1, 0), player);
	}

	public static boolean isPlayerHeldItem(ItemStack item, EntityPlayer player) {
		if (DCUtil.isEmpty(item) || player == null)
			return false;

		if (player.getHeldItem(EnumHand.MAIN_HAND) != null) {
			if (DCUtil.isSameItem(item, player.getHeldItem(EnumHand.MAIN_HAND), false))
				return true;
		}
		if (player.getHeldItem(EnumHand.OFF_HAND) != null) {
			if (DCUtil.isSameItem(item, player.getHeldItem(EnumHand.OFF_HAND), false))
				return true;
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
		EntityEquipmentSlot.HEAD,
		EntityEquipmentSlot.CHEST,
		EntityEquipmentSlot.LEGS,
		EntityEquipmentSlot.FEET };

	// チャームを保持しているかのチェック
	public static NonNullList<ItemStack> getPlayerCharm(EntityPlayer player, CharmType type) {
		NonNullList<ItemStack> ret = NonNullList.create();
		if (player == null)
			return ret;
		for (int i = 9; i < 18; i++) {
			ItemStack check = player.inventory.getStackInSlot(i);
			if (!isEmpty(check) && check.getItem() instanceof IJewelCharm) {
				IJewelCharm charm = (IJewelCharm) check.getItem();
				int m = check.getItemDamage();
				if (type == null || charm.getCharmType(m) == type) {
					boolean b = false;
					for (ItemStack c2 : ret) {
						if (OreDictionary.itemMatches(check, c2, false)) {
							c2.grow(1);
							b = true;
							break;
						}
					}
					if (!b) {
						ret.add(check.copy());
					}
				}
			}
		}

		if (Loader.isModLoaded("baubles")) {
			NonNullList<ItemStack> charms2 = DCPluginBaubles.getBaublesCharm(player, type);
			if (!charms2.isEmpty()) {
				for (ItemStack check : charms2) {
					boolean b = false;
					for (ItemStack c2 : ret) {
						if (OreDictionary.itemMatches(check, c2, false)) {
							c2.grow(1);
							b = true;
							break;
						}
					}
					if (!b) {
						ret.add(check.copy());
					}
				}
			}
		}

		return ret;
	}

	public static NonNullList<ItemStack> getMobCharm(EntityLivingBase living) {
		NonNullList<ItemStack> ret = NonNullList.create();
		if (living == null || living instanceof EntityPlayer) {
			return ret;
		} else {
			if (Loader.isModLoaded("schr0chastmob") && ChastMobPlugin.isChastMob(living)) {
				NonNullList<ItemStack> chast = ChastMobPlugin.getCharms(living);
				for (ItemStack check : chast) {
					boolean b = false;
					for (ItemStack c2 : ret) {
						if (OreDictionary.itemMatches(check, c2, false)) {
							c2.grow(1);
							b = true;
							break;
						}
					}
					if (!b) {
						ret.add(check.copy());
					}
				}
				return ret;
			}

			IItemHandler handler = living.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (handler != null) {
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack check = handler.getStackInSlot(i);
					if (!isEmpty(check) && check.getItem() instanceof IJewelCharm) {
						boolean b = false;
						for (ItemStack c2 : ret) {
							if (OreDictionary.itemMatches(check, c2, false)) {
								c2.grow(1);
								b = true;
								break;
							}
						}
						if (!b) {
							ret.add(check.copy());
						}
					}
				}
			} else if (living instanceof EntityVillager) {
				IInventory inv = ((EntityVillager) living).getVillagerInventory();
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack check = inv.getStackInSlot(i);
					if (!isEmpty(check) && check.getItem() instanceof IJewelCharm) {
						boolean b = false;
						for (ItemStack c2 : ret) {
							if (OreDictionary.itemMatches(check, c2, false)) {
								c2.grow(1);
								b = true;
								break;
							}
						}
						if (!b) {
							ret.add(check.copy());
						}
					}
				}
			}
		}
		return ret;
	}

	// チャームを保持しているかのチェック2
	public static boolean hasItemInTopSlots(EntityPlayer player, ItemStack item) {
		if (player == null || isEmpty(item))
			return false;
		for (int i = 9; i < 18; i++) {
			ItemStack check = player.inventory.getStackInSlot(i);
			if (!isEmpty(check) && OreDictionary.itemMatches(check, item, false)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasCharmItem(EntityLivingBase living, ItemStack item) {
		if (living == null || isEmpty(item))
			return false;
		if (living instanceof EntityPlayer) {
			NonNullList<ItemStack> checkList = getPlayerCharm((EntityPlayer) living, null);
			for (ItemStack check : checkList) {
				if (!isEmpty(check) && OreDictionary.itemMatches(check, item, false)) {
					return true;
				}
			}
		} else {
			NonNullList<ItemStack> charms = getMobCharm(living);
			for (ItemStack check : charms) {
				if (!isEmpty(check) && OreDictionary.itemMatches(check, item, false)) {
					return true;
				}
			}
		}
		return false;
	}

	// レンチアイテム
	public static boolean isHeldWrench(EntityLivingBase player, EnumHand hand) {
		ItemStack main = player.getHeldItem(hand);
		return !DCUtil.isEmpty(main) && main.getItem() instanceof IWrenchDC;
	}

	// 気候チェック
	public static DCHeatTier getBlockTemp(BlockSet set, World world, BlockPos pos) {
		if (ClimateAPI.registerBlock.isRegisteredHeat(set.block, set.meta)) {
			return ClimateAPI.registerBlock.getHeatTier(set.block, set.meta);
		} else if (set.block instanceof IHeatTile) {
			return ((IHeatTile) set.block).getHeatTier(world, pos, pos);
		} else if (set.block instanceof IFluidBlock) {
			Fluid f = ((IFluidBlock) set.block).getFluid();
			if (f != null) {
				return DCHeatTier.getTypeByTemperature(f.getTemperature(world, pos));
			}
		}
		return DCHeatTier.NORMAL;
	}

	public static DCHumidity getBlockHum(BlockSet set, World world, BlockPos pos) {
		if (ClimateAPI.registerBlock.isRegisteredHum(set.block, set.meta)) {
			return ClimateAPI.registerBlock.getHumidity(set.block, set.meta);
		} else if (set.block.getDefaultState().getMaterial() == Material.WATER) {
			return DCHumidity.WET;
		}
		return DCHumidity.NORMAL;
	}

	public static DCAirflow getBlockAir(BlockSet set, World world, BlockPos pos) {
		if (ClimateAPI.registerBlock.isRegisteredAir(set.block, set.meta)) {
			return ClimateAPI.registerBlock.getAirflow(set.block, set.meta);
		}
		return DCAirflow.NORMAL;
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
			if (!world.provider.hasSkyLight()) {
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

	public static boolean machCreativeTab(CreativeTabs target, CreativeTabs tab) {
		return tab != null && (target == CreativeTabs.SEARCH || target == tab);
	}

	public static List<BlockSet> getListFromStrings(String[] names, String logname) {
		List<BlockSet> list = Lists.newArrayList();
		if (names != null && names.length > 0) {
			for (String name : names) {
				if (name != null) {
					String itemName = name;
					String modid = "minecraft";
					int meta = 32767;
					if (name.contains(":")) {
						String[] n2 = name.split(":");
						if (n2 != null && n2.length > 0) {
							if (n2.length > 2) {
								Integer m = null;
								try {
									m = Integer.parseInt(n2[2]);
								} catch (NumberFormatException e) {
									DCLogger.debugLog("Tried to parse non Integer target: " + n2[2]);
								}
								if (m != null && m >= 0) {
									meta = m;
								}
							}

							if (n2.length == 1) {
								itemName = n2[0];
							} else {
								modid = n2[0];
								itemName = n2[1];
							}
						}
					}

					Block block = Block.REGISTRY.getObject(new ResourceLocation(modid, itemName));
					if (block != null && block != Blocks.AIR) {
						DCLogger.infoLog(logname + " add target: " + modid + ":" + itemName + ", " + meta);
						BlockSet set = new BlockSet(block, meta);
						list.add(set);
					} else {
						DCLogger.infoLog("Failed find target: " + modid + ":" + itemName);
					}
				}
			}
		}
		return list;
	}

	public static List<Class<? extends Entity>> getEntityListFromStrings(String[] names, String logname) {
		List<Class<? extends Entity>> list = Lists.newArrayList();
		if (names != null && names.length > 0) {
			for (String name : names) {
				if (name != null) {
					ResourceLocation res = new ResourceLocation(name);
					if (res.getResourceDomain().equalsIgnoreCase("minecraft")) {
						String n = res.getResourcePath();
						res = new ResourceLocation(n);
					}
					if (EntityList.getClass(res) != null) {
						Class<? extends Entity> entity = EntityList.getClass(res);
						if (entity != null) {
							list.add(entity);
							DCLogger.infoLog("Registered to the update blacklist: " + name);
						}
						continue;
					}
				}
				DCLogger.infoLog("Failed find target: " + name);
			}
		}
		return list;
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
