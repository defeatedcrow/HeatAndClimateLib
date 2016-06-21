package defeatedcrow.hac.core.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.CommonProxyD;
import defeatedcrow.hac.core.client.base.ModelThinBiped;

public class ClientProxyD extends CommonProxyD {

	private static final ModelThinBiped metModel = new ModelThinBiped(0);
	private static final ModelThinBiped bodyModel = new ModelThinBiped(1.0F, 1);
	private static final ModelThinBiped legginsModel = new ModelThinBiped(2);
	private static final ModelThinBiped bootsModel = new ModelThinBiped(1.0F, 3);

	@Override
	public void loadMaterial() {
		super.loadMaterial();
		JsonRegisterHelper.INSTANCE.load();
	}

	@Override
	public void loadTE() {
		super.loadTE();
	}

	@Override
	public ModelThinBiped getArmorModel(int slot) {
		switch (slot) {
		case 3:
			return metModel;
		case 2:
			return bodyModel;
		case 1:
			return legginsModel;
		case 0:
			return bootsModel;
		default:
			return null;
		}
	}

	@Override
	public void loadInit() {
		super.loadInit();
		MinecraftForge.EVENT_BUS.register(JsonBakery.instance);
	}

	@Override
	public boolean isShiftKeyDown() {
		return Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}

	@Override
	public boolean isJumpKeyDown() {
		return Keyboard.isCreated() && Keyboard.isKeyDown(getJumpKey());
	}

	@Override
	public boolean isWarpKeyDown() {
		return CoreConfigDC.charmWarpKey == 0 ? false : Keyboard.isCreated()
				&& Keyboard.isKeyDown(CoreConfigDC.charmWarpKey);
	}

	private int getJumpKey() {
		return Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
	}

	private int getSneakKey() {
		return Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
	}

}
