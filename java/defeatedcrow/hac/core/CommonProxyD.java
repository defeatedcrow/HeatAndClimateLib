package defeatedcrow.hac.core;

import net.minecraftforge.common.MinecraftForge;
import defeatedcrow.hac.core.event.LivingPotionEvent;

public class CommonProxyD {

	public void loadMaterial() {
		MaterialRegister.load();
	}

	public void loadTE() {
		TileRegister.load();
	}

	public void loadWorldGen() {
	}

	public void loadInit() {
		MinecraftForge.EVENT_BUS.register(new LivingPotionEvent());
		// VanillaRecipeRegister.load();
		// MinecraftForge.EVENT_BUS.register(new NotifyClimateEvent());
	}

}
