package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.hook.DCItemEatEvent;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/* 設置できない食べ物 */
public abstract class DCFoodItem extends ItemFood implements ITexturePath {

	public DCFoodItem(boolean isWolfFood) {
		super(0, 0, isWolfFood);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setAlwaysEdible();
	}

	@Override
	public int getMetadata(int damage) {
		return Math.min(damage, getMaxMeta());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int j = Math.min(stack.getMetadata(), getMaxMeta());
		return getNameSuffix() != null && j < getNameSuffix().length ?
				super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super.getUnlocalizedName();
	}

	public int getMaxMeta() {
		return 3;
	}

	public abstract String[] getNameSuffix();

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase living) {
		int meta = stack.getMetadata();

		if (living instanceof EntityPlayer) {

			PotionEffect potion = null;
			if (!getPotionEffect(meta).isEmpty()) {
				potion = getPotionEffect(meta).get(0);
			}
			if (new DCItemEatEvent(stack, worldIn, living, potion).result()) {
				return stack;
			}

			EntityPlayer player = (EntityPlayer) living;
			player.getFoodStats().addStats(this, stack);
			worldIn.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, worldIn.rand
					.nextFloat() * 0.1F + 0.9F);
			this.addEffects(stack, worldIn, living);
			this.dropContainerItem(worldIn, stack, living);
			DCUtil.reduceStackSize(stack, 1);
		}

		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target,
			EnumHand hand) {
		if (this.addEffects(stack, player.world, target)) {
			this.dropContainerItem(player.world, stack, player);
			DCUtil.reduceStackSize(stack, 1);
			return true;
		}
		return super.itemInteractionForEntity(stack, player, target, hand);
	}

	public boolean addEffects(ItemStack stack, World worldIn, EntityLivingBase living) {
		if (!worldIn.isRemote && stack != null) {
			int meta = stack.getMetadata();
			List<PotionEffect> effects = this.getPotionEffect(meta);
			if (effects.isEmpty())
				return false;
			for (PotionEffect get : effects) {
				if (get != null && get.getPotion() != null) {
					Potion por = get.getPotion();
					if (por == null)
						continue;
					int amp = get.getAmplifier();
					int dur = get.getDuration();
					if (living.isPotionActive(get.getPotion())) {
						PotionEffect check = living.getActivePotionEffect(por);
						dur += check.getDuration();
					}
					living.addPotionEffect(new PotionEffect(get.getPotion(), dur, amp));
				}
			}
			return true;
		}
		return false;
	}

	public void dropContainerItem(World world, ItemStack food, EntityLivingBase living) {
		if (!world.isRemote && living != null) {
			ItemStack stack = this.getFoodContainerItem(food);
			if (!DCUtil.isEmpty(stack)) {
				EntityItem drop = new EntityItem(world, living.posX, living.posY + 0.25D, living.posZ, stack);
				world.spawnEntity(drop);
			}
		}
	}

	public abstract int getFoodAmo(int meta);

	public abstract float getSaturation(int meta);

	@Override
	public int getHealAmount(ItemStack stack) {
		if (!DCUtil.isEmpty(stack)) {
			return (int) (getFoodAmo(stack.getItemDamage()) * CoreConfigDC.food_amount);
		}
		return super.getHealAmount(stack);
	}

	@Override
	public float getSaturationModifier(ItemStack stack) {
		if (!DCUtil.isEmpty(stack)) {
			return getSaturation(stack.getItemDamage());
		}
		return super.getSaturationModifier(stack);
	}

	public List<PotionEffect> getPotionEffect(int meta) {
		List<PotionEffect> ret = new ArrayList<PotionEffect>();
		return ret;
	}

	/*
	 * コンテナアイテム。
	 */
	public ItemStack getFoodContainerItem(ItemStack item) {
		ItemStack ret = this.getContainerItem(item);
		return ret;
	}

	/**
	 * 移植補助
	 */

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		return onItemUse2(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	public EnumActionResult onItemUse2(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		return onItemRightClick2(world, player, hand);
	}

	public ActionResult<ItemStack> onItemRightClick2(World world, EntityPlayer player, EnumHand hand) {
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab) && tab != CreativeTabs.FOOD) {
			List<ItemStack> itms = getSubItemList();
			subItems.addAll(itms);
		}
	}

	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		for (int i = 0; i < getMaxMeta() + 1; i++) {
			list.add(new ItemStack(this, 1, i));
		}
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		addInformation2(stack, world, tooltip);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {
		super.addInformation(stack, world, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
	}
}
