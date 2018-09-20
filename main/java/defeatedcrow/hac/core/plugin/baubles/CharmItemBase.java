package defeatedcrow.hac.core.plugin.baubles;

import baubles.api.IBauble;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.base.DCItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;

@Optional.InterfaceList({
		@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles"),
})
public abstract class CharmItemBase extends DCItem implements IJewelCharm, IBauble {

	@Override
	@Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack arg0) {
		return baubles.api.BaubleType.CHARM;
	}

}
