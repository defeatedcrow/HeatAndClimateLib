package defeatedcrow.hac.core.client.event;

import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaterFogEvent {

	@SubscribeEvent
	public void onFogDencity(EntityViewRenderEvent.FogDensity event) {
		if (CoreConfigDC.waterFix > 0D && event.getState() != null
				&& event.getState().getMaterial() == Material.WATER) {
			// water fog fix
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			event.setDensity((float) CoreConfigDC.waterFix);
			event.setCanceled(true);
		}
		if (CoreConfigDC.lavaFix > 0D && event.getState() != null && event.getState().getMaterial() == Material.LAVA) {
			// lava fog fix
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			event.setDensity((float) CoreConfigDC.lavaFix);
			event.setCanceled(true);
		}
	}

}
