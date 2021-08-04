package defeatedcrow.hac.core.client;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import defeatedcrow.hac.api.module.HaCModule;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.CommonProxyD;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.client.event.DCGuiInfomationEvent;
import defeatedcrow.hac.core.client.event.RenderTempHUDEvent;
import defeatedcrow.hac.core.client.event.WaterFogEvent;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.recipe.RecipeJsonMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

		MinecraftForge.EVENT_BUS.register(new AdvancedTooltipEvent());
		MinecraftForge.EVENT_BUS.register(new AdvancedHUDEvent());
		MinecraftForge.EVENT_BUS.register(RenderTempHUDEvent.INSTANCE);
		MinecraftForge.EVENT_BUS.register(DCGuiInfomationEvent.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new WaterFogEvent());
	}

	@Override
	public boolean isShiftKeyDown() {
		return Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}

	@Override
	public boolean isJumpKeyDown() {
		if (CoreConfigDC.altJumpKey == -1)
			return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
		else
			return CoreConfigDC.altJumpKey <= 0 || CoreConfigDC.altJumpKey >= Keyboard.KEYBOARD_SIZE ? false : Keyboard
					.isCreated() && Keyboard.isKeyDown(CoreConfigDC.altJumpKey);
	}

	@Override
	public boolean isSneakKeyDown() {
		if (CoreConfigDC.altSneakKey == -1)
			return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
		else
			return CoreConfigDC.altSneakKey <= 0 || CoreConfigDC.altSneakKey >= Keyboard.KEYBOARD_SIZE ? false :
					Keyboard.isCreated() && Keyboard.isKeyDown(CoreConfigDC.altSneakKey);
	}

	@Override
	public boolean isWarpKeyDown() {
		return CoreConfigDC.charmWarpKey <= 0 || CoreConfigDC.charmWarpKey >= Keyboard.KEYBOARD_SIZE ? false : Keyboard
				.isCreated() && Keyboard.isKeyDown(CoreConfigDC.charmWarpKey);
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public World getWorld() {
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

	@Override
	public void addShapedRecipeJson(HaCModule module, String name, int num, @Nonnull ItemStack result,
			Object... recipe) {
		RecipeJsonMaker.buildShapedRecipe(module, name, num, result, recipe);
	}

	@Override
	public void addShapedRecipeJson(String name, int num, @Nonnull ItemStack result, Object... recipe) {
		RecipeJsonMaker.buildShapedRecipe(HaCModule.CORE, name, num, result, recipe);
	}

	@Override
	public void addShapelessRecipeJson(HaCModule module, String name, int num, @Nonnull ItemStack result,
			Object... recipe) {
		RecipeJsonMaker.buildShapelessRecipe(module, name, num, result, recipe);
	}

	@Override
	public void addShapelessRecipeJson(String name, int num, @Nonnull ItemStack result, Object... recipe) {
		RecipeJsonMaker.buildShapelessRecipe(HaCModule.CORE, name, num, result, recipe);
	}

	@Override
	public void updatePlayerClimate() {
		if (localCount <= 0) {
			localCount = 10;
			ClientClimateData.INSTANCE.updatePlayerClimate(getClientWorld(), getPlayer());
		} else {
			localCount--;
		}
	}

	private static int localCount = 10;

}
