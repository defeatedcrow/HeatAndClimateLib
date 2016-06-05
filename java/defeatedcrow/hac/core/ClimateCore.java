package defeatedcrow.hac.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;

@Mod(
		modid = ClimateCore.MOD_ID,
		name = ClimateCore.MOD_NAME,
		version = ClimateCore.MOD_MEJOR + "." + ClimateCore.MOD_MINOR + "." + ClimateCore.MOD_BUILD,
		dependencies = ClimateCore.MOD_DEPENDENCIES,
		acceptedMinecraftVersions = ClimateCore.MOD_ACCEPTED_MC_VERSIONS,
		useMetadata = true)
public class ClimateCore {
	public static final String MOD_ID = "dcs_climate|lib";
	public static final String MOD_NAME = "HeatAndClimateLib";
	public static final int MOD_MEJOR = 0;
	public static final int MOD_MINOR = 5;
	public static final String MOD_BUILD = "f";
	public static final String MOD_DEPENDENCIES = "required-after:Forge@[11.15.1.1722,)";
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.8.9]";

	@SidedProxy(
			clientSide = "defeatedcrow.hac.core.client.ClientProxyD",
			serverSide = "defeatedcrow.hac.core.CommonProxyD")
	public static CommonProxyD proxy;

	@Instance("dcs_climate|lib")
	public static ClimateCore instance;

	public static final String PACKAGE_BASE = "dcs";
	public static final String PACKAGE_ID = "dcs_climate";
	public static final CreativeTabs climate = new CreativeTabClimate(MOD_ID);

	public static boolean isDebug = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ClimateConfig.INSTANCE.load(event.getModConfigurationDirectory());
		isDebug = DCUtil.checkDebugModePass(CoreConfigDC.debugPass);
		APILoader.loadAPI();
		proxy.loadMaterial();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.loadInit();
		proxy.loadTE();
		proxy.loadWorldGen();
	}
}
