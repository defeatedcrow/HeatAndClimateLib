package defeatedcrow.hac.core.event;

import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.packet.command.ForcedSeasonHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AttachCapabilityDC {

	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<World> event) {
		if (event.getObject() != null) {
			ResourceLocation reg = new ResourceLocation(ClimateCore.MOD_ID, "forced_season");
			if (!event.getCapabilities().containsKey(reg))
				event.addCapability(reg, new ForcedSeasonHandler(event.getObject()));
		}
	}

}
