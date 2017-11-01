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
			if (right.getItem() instanceof IPlatingTool && DCUtil.isEmpty(ret)) {
				IPlatingTool tool = (IPlatingTool) right.copy().getItem();
				if (tool.canEnchant(left, right)) {
					ItemStack result = tool.getEnchantedItem(left, right);
					if (!DCUtil.isEmpty(result)) {
						event.setOutput(result);
						event.setMaterialCost(1);
						event.setCost(3);
					}
				}
			}
		}
	}

}
