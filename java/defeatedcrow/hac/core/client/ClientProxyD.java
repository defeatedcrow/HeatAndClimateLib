package defeatedcrow.hac.core.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import defeatedcrow.hac.core.CommonProxyD;
import defeatedcrow.hac.machine.client.TESRFuelStove;
import defeatedcrow.hac.machine.common.StoveBase;

public class ClientProxyD extends CommonProxyD {

	@Override
	public void loadMaterial() {
		super.loadMaterial();
		JsonRegisterHelper.INSTANCE.load();
	}

	@Override
	public void loadTE() {
		super.loadTE();
		ClientRegistry.bindTileEntitySpecialRenderer(StoveBase.class, new TESRFuelStove());
	}

	@Override
	public void loadInit() {
		super.loadInit();
		MinecraftForge.EVENT_BUS.register(JsonBakery.instance);
	}

}
