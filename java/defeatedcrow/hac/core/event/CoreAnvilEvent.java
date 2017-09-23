package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.item.IPlatingTool;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoreAnvilEvent {

	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		ItemStack ret = event.getOutput();
		/* Plating */
		if (!DCUtil.isEmpty(right) && !DCUtil.isEmpty(left)) {
			if (right.getItem() instanceof IPlatingTool && ret == null) {
				IPlatingTool tool = (IPlatingTool) right.copy().getItem();
				if (tool.canEnchant(left)) {
					ItemStack result = tool.getEnchantedItem(left);
					if (result != null) {
						event.setOutput(result);
						event.setMaterialCost(1);
						event.setCost(3);
					}
				}
			}
		}
	}

}
