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
	public static final ItemSet EMPTY = new ItemSet(null, 0);

	public ItemSet(Item i, int j) {
		item = i;
		meta = j;
	}

	public ItemSet(Item i) {
		item = i;
		meta = 0;
	}

	public ItemSet(ItemStack stack) {
		item = stack.getItem();
		meta = stack.getItemDamage();
	}

	public static boolean isEmpty(ItemSet set) {
		if (set == null || set.item == null)
			return true;
		return false;
	}

	public ItemStack getSingleStack() {
		if (item == null)
			return ItemStack.EMPTY;
		return new ItemStack(item, 1, meta);
	}

	public String localizedname() {
		if (item == null)
			return "empty";
		return getSingleStack().getDisplayName();
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
		if (item == null)
			return 0;
		int i = item.getUnlocalizedName().hashCode() + meta;
		return i;
	}
}
