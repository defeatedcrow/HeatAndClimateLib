/**
 * Copyright (c) defeatedcrow, 2016
 * URL:http://defeatedcrow.jp/modwiki/Mainpage
 * Please check the License.txt included in the package file of this Mod.
 */

package defeatedcrow.hac.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.fluid.FluidIDRegisterDC;
import defeatedcrow.hac.core.packet.command.DCServerCommand;
import defeatedcrow.hac.core.recipe.CustomizeVanillaRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import defeatedcrow.hac.core.util.DCWaterOpaque;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

// @SortingIndex(1102)
@Mod(modid = ClimateCore.MOD_ID, name = ClimateCore.MOD_NAME,
		version = ClimateCore.MOD_MEJOR + "." + ClimateCore.MOD_MINOR + "." + ClimateCore.MOD_BUILD,
		dependencies = ClimateCore.MOD_DEPENDENCIES, acceptedMinecraftVersions = ClimateCore.MOD_ACCEPTED_MC_VERSIONS,
		updateJSON = ClimateCore.UPDATE_JSON, certificateFingerprint = ClimateCore.KEY, useMetadata = true)
public class ClimateCore {
	public static final String MOD_ID = "dcs_lib";
	public static final String MOD_NAME = "HeatAndClimateLib";
	public static final int MOD_MEJOR = 3;
	public static final int MOD_MINOR = 2;
	public static final int MOD_BUILD = 1;
	public static final String MOD_DEPENDENCIES = "before:cavern;before:mekanism";
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
	public static final String PACKAGE_BASE = "dcs";
	public static final String PACKAGE_ID = "dcs_climate";
	public static final String UPDATE_JSON = "https://defeatedcrow.jp/version/haclib.json";
	public static final String KEY = "4cd12b92959105443b7b694fffe0cea9ed004886";

	@SidedProxy(clientSide = "defeatedcrow.hac.core.client.ClientProxyD",
			serverSide = "defeatedcrow.hac.core.CommonProxyD")
	public static CommonProxyD proxy;

	@Instance("dcs_lib")
	public static ClimateCore instance;

	public static final Logger LOGGER = LogManager.getLogger(PACKAGE_ID);

	public static final CreativeTabs climate = new CreativeTabClimate(MOD_ID);

	public static boolean isDebug = false;
	public static boolean serverStarted = false;
	public static boolean loadedMain = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ClimateConfig.INSTANCE.load(event.getModConfigurationDirectory());
		isDebug = DCUtil.checkDebugModePass(CoreConfigDC.debugPass);
		if (Loader.isModLoaded("dcs_climate")) {
			loadedMain = true;
		}
		// API
		APILoader.loadAPI();

		proxy.loadMaterial();
		proxy.loadEntity();

		// water opaque
		DCWaterOpaque.load();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.loadInit();
		proxy.loadTE();
		proxy.loadWorldGen();
		if (!CoreConfigDC.disableCustomRecipe) {
			CustomizeVanillaRecipe.initCustomize();
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MobResistantRegister.pre();
		ArmorResistantRegister.pre();
		HeatBlockRegister.pre();
		ClimateRegister.pre();

		// default property
		APILoader.registerClimate();
		APILoader.registerMaterial();
		APILoader.registerMobResistant();

		FluidIDRegisterDC.post();
		MobResistantRegister.post();
		ArmorResistantRegister.post();
		HeatBlockRegister.post();
		ClimateRegister.post();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new DCServerCommand());
		serverStarted = true;
	}
}
