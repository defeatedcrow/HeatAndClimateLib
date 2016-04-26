package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		int j = Math.min(stack.getItemDamage(), getMaxMeta());
		return getNameSuffix() != null && j < getNameSuffix().length ? super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super
				.getUnlocalizedName();
	}

	public int getMaxMeta() {
		return 3;
	}

	public abstract String[] getNameSuffix();

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < getMaxMeta() + 1; i++) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		int meta = stack.getItemDamage();
		--stack.stackSize;
		playerIn.getFoodStats().addStats(getFoodAmo(meta), getSaturation(meta));
		worldIn.playSoundAtEntity(playerIn, "random.burp", 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
		this.addEffects(stack, worldIn, playerIn);
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
		this.addEffects(stack, player.worldObj, target);
		return super.itemInteractionForEntity(stack, player, target);
	}

	public void addEffects(ItemStack stack, World worldIn, EntityLivingBase living) {
		if (!worldIn.isRemote && stack != null) {
			int meta = stack.getItemDamage();
			List<PotionEffect> effects = this.getPotionEffect(meta);
			for (PotionEffect get : effects) {
				if (get != null && get.getPotionID() > 0) {
					Potion por = Potion.potionTypes[get.getPotionID()];
					if (por == null)
						continue;
					int amp = get.getAmplifier();
					int dur = get.getDuration();
					if (living.isPotionActive(get.getPotionID())) {
						PotionEffect check = living.getActivePotionEffect(por);
						dur += check.getDuration();
					}
					living.addPotionEffect(new PotionEffect(get.getPotionID(), dur, amp));
				}
			}
		}
	}

	public abstract int getFoodAmo(int meta);

	public abstract float getSaturation(int meta);

	public List<PotionEffect> getPotionEffect(int meta) {
		List<PotionEffect> ret = new ArrayList<PotionEffect>();
		return ret;
	}
}
