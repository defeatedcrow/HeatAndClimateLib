package defeatedcrow.hac.core.base;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DCItem extends Item implements ITexturePath {

	public DCItem() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return Math.min(damage, getMaxMeta());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int j = Math.min(stack.getItemDamage(), getMaxMeta());
		return getNameSuffix() != null && j < getNameSuffix().length ? super.getUnlocalizedName() + "_"
				+ getNameSuffix()[j] : super.getUnlocalizedName();
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
}
