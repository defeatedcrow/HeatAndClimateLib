package defeatedcrow.hac.core.event;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import defeatedcrow.hac.config.CoreConfigDC;

public class CaveGenLavaDC {

	@SubscribeEvent
	public void initMapGen(InitMapGenEvent event) {
		if (CoreConfigDC.enableDeepWater && event.getType() == EventType.CAVE) {
			event.setNewGen(new MapGenCaveDC());
		}

	}

}
