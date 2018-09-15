package defeatedcrow.hac.core.packet;

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
	}
}
