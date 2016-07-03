package defeatedcrow.hac.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.event.BlockUpdateDC;
import defeatedcrow.hac.core.event.ClickEventDC;
import defeatedcrow.hac.core.event.LivingEventDC;
import defeatedcrow.hac.core.event.LivingHurtDC;
import defeatedcrow.hac.core.packet.HaCPacket;
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

	public void loadEntity() {
	}

	public void loadInit() {
		OreRegister.load();
		VanillaRecipeRegister.load();
		MinecraftForge.EVENT_BUS.register(new LivingEventDC());
		MinecraftForge.EVENT_BUS.register(new BlockUpdateDC());
		MinecraftForge.EVENT_BUS.register(new LivingHurtDC());
		MinecraftForge.EVENT_BUS.register(new ClickEventDC());

		HaCPacket.init();
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

	public EntityPlayer getPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}

}
