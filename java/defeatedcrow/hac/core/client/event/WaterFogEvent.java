package defeatedcrow.hac.core.client.event;

import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaterFogEvent {

	@SubscribeEvent
	public void onFogDencity(EntityViewRenderEvent.FogDensity event) {
		if (CoreConfigDC.waterFix && event.getState() != null && event.getState().getMaterial() == Material.WATER) {
			// water fog の場合
			event.setDensity(0.025F);
			event.setCanceled(true);
		}
	}

}
