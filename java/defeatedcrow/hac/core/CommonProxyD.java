package defeatedcrow.hac.core;

import net.minecraftforge.common.MinecraftForge;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.event.BlockUpdateDC;
import defeatedcrow.hac.core.event.LivingEventDC;
import defeatedcrow.hac.core.util.DCPotion;

public class CommonProxyD {

	public void loadMaterial() {
		DCPotion.init();
		MaterialRegister.load();
	}

	public void loadTE() {
		TileRegister.load();
	}

	public void loadWorldGen() {
	}

	public void loadInit() {
		OreRegister.load();
		VanillaRecipeRegister.load();
		MinecraftForge.EVENT_BUS.register(new LivingEventDC());
		MinecraftForge.EVENT_BUS.register(new BlockUpdateDC());
	}

	public ModelThinBiped getArmorModel(int slot) {
		return null;
	}

	public boolean isJumpKeyDown() {
		return false;
	}

	public boolean isShiftKeyDown() {
		return false;
	}

	public boolean isWarpKeyDown() {
		return false;
	}

}
