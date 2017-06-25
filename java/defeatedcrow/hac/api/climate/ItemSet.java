package defeatedcrow.hac.api.climate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Item、metaのセット
 * ItemStackではMapのキーにならないので面倒くさい
 */
public class ItemSet {

	public final Item item;
	public final int meta;

	public ItemSet(Item i, int j) {
		item = i;
		meta = j;
	}

	public ItemStack getSingleStack() {
		return new ItemStack(item, 1, meta);
	}

	/**
	 * metaにはwildcard指定可能
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ItemSet) {
			ItemSet p = (ItemSet) obj;
			return p.item == item && (meta == 32767 || p.meta == 32767 || p.meta == meta);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int i = item.getUnlocalizedName().hashCode() + meta;
		return i;
	}
}
