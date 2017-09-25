package defeatedcrow.hac.core;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.event.BlockUpdateDC;
import defeatedcrow.hac.core.event.CaveGenLavaDC;
import defeatedcrow.hac.core.event.ClickEventDC;
import defeatedcrow.hac.core.event.CoreAnvilEvent;
import defeatedcrow.hac.core.event.LivingEventDC;
import defeatedcrow.hac.core.event.LivingHurtDC;
import defeatedcrow.hac.core.event.SuffocationEventDC;
import defeatedcrow.hac.core.event.TickEventDC;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.util.DCPotion;
import defeatedcrow.hac.core.util.PotionFreezeResistance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxyD {

	public void loadMaterial() {
		DCPotion.init();
		MaterialRegister.load();

		DCInit.prevFreeze = new PotionFreezeResistance();
		GameRegistry.register(DCInit.prevFreeze, new ResourceLocation(ClimateCore.MOD_ID, "dcs.potion.freeze_res"));
		DCInit.prevFreezeType = new PotionType("dcs.freeze_res", new PotionEffect[] {
				new PotionEffect(DCInit.prevFreeze, 3600, 0)
		});
		GameRegistry.register(DCInit.prevFreezeType, new ResourceLocation(ClimateCore.MOD_ID, "dcs.freeze_res"));
	}

	public void loadTE() {}

	public void loadWorldGen() {}

	public void loadEntity() {}

	public void loadInit() {
		OreRegister.load();
		if (CoreConfigDC.enableVanilla) {
			VanillaRecipeRegister.load();
		}
		MinecraftForge.EVENT_BUS.register(new LivingEventDC());
		MinecraftForge.EVENT_BUS.register(new BlockUpdateDC());
		MinecraftForge.EVENT_BUS.register(new LivingHurtDC());
		MinecraftForge.EVENT_BUS.register(new ClickEventDC());
		MinecraftForge.TERRAIN_GEN_BUS.register(new CaveGenLavaDC());
		MinecraftForge.EVENT_BUS.register(new TickEventDC());
		if (CoreConfigDC.enableSuffocation) {
			MinecraftForge.EVENT_BUS.register(new SuffocationEventDC());
		}
		MinecraftForge.EVENT_BUS.register(new CoreAnvilEvent());

		HaCPacket.init();
	}

	public ModelThinBiped getArmorModel(int slot) {
		return null;
	}

	public boolean isJumpKeyDown() {
		return false;
	}

	public boolean isSneakKeyDown() {
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

	public int getWeatherHeatOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getTempOffset(dim, isHell);
		}
		return 0;
	}

	public int getWeatherHumOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getHumOffset(dim, isHell);
		}
		return 0;
	}

	public int getWeatherAirOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getWindOffset(dim, isHell);
		}
		return 0;
	}

}
