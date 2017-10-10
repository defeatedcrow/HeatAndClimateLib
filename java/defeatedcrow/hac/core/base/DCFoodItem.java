package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
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
		return getNameSuffix() != null && j < getNameSuffix().length
				? super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super.getUnlocalizedName();
	}

	public int getMaxMeta() {
		return 3;
	}

	public abstract String[] getNameSuffix();

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (int i = 0; i < getMaxMeta() + 1; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase living) {
		int meta = stack.getMetadata();

		if (living instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) living;
			player.getFoodStats().addStats(getFoodAmo(meta), getSaturation(meta));
			worldIn.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ,
					SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F,
					worldIn.rand.nextFloat() * 0.1F + 0.9F);
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
			return getFoodAmo(stack.getItemDamage());
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
}
