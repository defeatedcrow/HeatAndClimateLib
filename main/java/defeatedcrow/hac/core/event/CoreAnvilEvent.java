package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.item.IAnvilTool;
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
		if (!DCUtil.isEmpty(right) && !DCUtil.isEmpty(left) && DCUtil.isEmpty(ret)) {
			if (right.getItem() instanceof IPlatingTool) {
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
		} else if (right.getItem() instanceof IAnvilTool) {
			IAnvilTool tool = (IAnvilTool) right.copy().getItem();
			if (tool.canEnhance(left, right) && right.getCount() >= tool.getMaterialCost(right.getItemDamage())) {
				ItemStack result = tool.getEnhancedItem(left, right);
				if (!DCUtil.isEmpty(result)) {
					event.setOutput(result);
					event.setMaterialCost(tool.getMaterialCost(right.getItemDamage()));
					event.setCost(tool.getCost(right.getItemDamage()));
				}
			}
		}
	}

}
