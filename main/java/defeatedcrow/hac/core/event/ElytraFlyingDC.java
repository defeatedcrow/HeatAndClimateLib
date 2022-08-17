package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.hook.DCElytraFlyingEvent;
import defeatedcrow.hac.main.MainInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlyingDC {

	@SubscribeEvent
	public void onElytraCheck(DCElytraFlyingEvent event) {
		if (event.living instanceof EntityPlayer && !event.living.getEntityWorld().isRemote) {
			if (event.living.isPotionActive(MainInit.bird) || event.living.isPotionActive(MainInit.warp)) {
				event.setResult(Result.ALLOW);
			}
		}
	}

}
