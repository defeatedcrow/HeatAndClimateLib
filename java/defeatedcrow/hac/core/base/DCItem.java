package defeatedcrow.hac.core.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DCItem extends Item {

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
		return j > 0 ? super.getUnlocalizedName() + "_" + j : super.getUnlocalizedName();
	}

	public static int getMaxMeta() {
		return 3;
	}
}
