package defeatedcrow.hac.api.placeable;

import net.minecraft.item.ItemStack;

/**
 * 特定のItemをドロップするEntity。IEntityItemと対で使用する。
 */
public interface IItemDropEntity {

	ItemStack getDropItem();

}
