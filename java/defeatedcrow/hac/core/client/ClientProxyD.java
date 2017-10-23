package defeatedcrow.hac.core.client;

import org.lwjgl.input.Keyboard;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.CommonProxyD;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.client.event.RenderTempHUDEvent;
import defeatedcrow.hac.core.client.event.WaterFogEvent;
import defeatedcrow.hac.core.climate.WeatherChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxyD extends CommonProxyD {

	private static final ModelThinBiped metModel = new ModelThinBiped(0);
	private static final ModelThinBiped bodyModel = new ModelThinBiped(1.0F, 1);
	private static final ModelThinBiped legginsModel = new ModelThinBiped(2);
	private static final ModelThinBiped bootsModel = new ModelThinBiped(1.0F, 3);

	@Override
	public void loadMaterial() {
		MinecraftForge.EVENT_BUS.register(JsonBakery.instance);
		super.loadMaterial();
		JsonRegisterHelper.INSTANCE.regSimpleItem(DCInit.climate_checker, ClimateCore.PACKAGE_ID, "checker", "tool", 0);
	}

	@Override
	public void loadTE() {}

	@Override
	public void loadEntity() {
		super.loadEntity();
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

		MinecraftForge.EVENT_BUS.register(RenderTempHUDEvent.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new WaterFogEvent());
	}

	@Override
	public boolean isShiftKeyDown() {
		return Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}

	@Override
	public boolean isJumpKeyDown() {
		return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
	}

	@Override
	public boolean isSneakKeyDown() {
		return Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();
	}

	@Override
	public boolean isWarpKeyDown() {
		return CoreConfigDC.charmWarpKey <= 0 || CoreConfigDC.charmWarpKey >= Keyboard.KEYBOARD_SIZE ? false
				: Keyboard.isCreated() && Keyboard.isKeyDown(CoreConfigDC.charmWarpKey);
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	// ruby氏に無限に感謝
	/**
	 * @param cls
	 *        えんちちーのくらす
	 * @param render
	 *        Renderの継承クラス
	 */
	private void registRender(Class<? extends Entity> cls, final Class<? extends Render> render) {
		RenderingRegistry.registerEntityRenderingHandler(cls, new IRenderFactory() {
			@Override
			public Render createRenderFor(RenderManager manager) {
				try {
					return render.getConstructor(manager.getClass()).newInstance(manager);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	@Override
	public int getWeatherHeatOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getTempOffset(dim, isHell);
		}
		return 0;
	}

	@Override
	public int getWeatherHumOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getHumOffset(dim, isHell);
		}
		return 0;
	}

	@Override
	public int getWeatherAirOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getWindOffset(dim, isHell);
		}
		return 0;
	}

}
