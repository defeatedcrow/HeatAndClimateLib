package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.core.packet.command.MHandlerComConfig;
import defeatedcrow.hac.core.packet.command.MHandlerComDrought;
import defeatedcrow.hac.core.packet.command.MHandlerComSeason;
import defeatedcrow.hac.core.packet.command.MessageComConfig;
import defeatedcrow.hac.core.packet.command.MessageComDrought;
import defeatedcrow.hac.core.packet.command.MessageComSeason;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class HaCPacket {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("dcs_climate");

	public static void init() {
		INSTANCE.registerMessage(MHandlerCharmKey.class, MessageCharmKey.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MHandlerTorqueTile.class, MessageTorqueTile.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(MHandlerClimateTile.class, MessageClimateUpdate.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(MHandlerWeatherUpdate.class, MessageWeatherUpdate.class, 3, Side.CLIENT);

		INSTANCE.registerMessage(MHandlerComSeason.class, MessageComSeason.class, 4, Side.CLIENT);
		INSTANCE.registerMessage(MHandlerComDrought.class, MessageComDrought.class, 5, Side.CLIENT);
		INSTANCE.registerMessage(MHandlerComConfig.class, MessageComConfig.class, 6, Side.CLIENT);
	}
}
