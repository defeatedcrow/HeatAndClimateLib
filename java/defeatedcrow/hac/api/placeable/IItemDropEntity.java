package defeatedcrow.hac.api.placeable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IItemDropEntity {

	ItemStack getDropItem();

	@SideOnly(Side.CLIENT)
	ModelBase getPlaceModel();

	@SideOnly(Side.CLIENT)
	String getTexture();

}
