/**
 * Copyright (c) defeatedcrow, 2016
 * URL:http://defeatedcrow.jp/modwiki/Mainpage
 * defeatedcrow's mods are distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the License.txt included in the package file of this Mod.
 */

package defeatedcrow.hac.core;

import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.fluid.FluidIDRegisterDC;
import defeatedcrow.hac.core.recipe.CustomizeVanillaRecipe;
import defeatedcrow.hac.core.util.DCUtil;
import defeatedcrow.hac.core.util.DCWaterOpaque;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// @SortingIndex(1102)
@Mod(modid = ClimateCore.MOD_ID, name = ClimateCore.MOD_NAME, version = ClimateCore.MOD_MEJOR + "."
		+ ClimateCore.MOD_MINOR + "."
		+ ClimateCore.MOD_BUILD, dependencies = ClimateCore.MOD_DEPENDENCIES, acceptedMinecraftVersions = ClimateCore.MOD_ACCEPTED_MC_VERSIONS, useMetadata = true)
public class ClimateCore {
	public static final String MOD_ID = "dcs_climate|lib";
	public static final String MOD_NAME = "HeatAndClimateLib";
	public static final int MOD_MEJOR = 1;
	public static final int MOD_MINOR = 5;
	public static final int MOD_BUILD = 0;
	public static final String MOD_DEPENDENCIES = "required-after:Forge@[12.18.3.2185,)";
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.10,1.11]";

	@SidedProxy(clientSide = "defeatedcrow.hac.core.client.ClientProxyD", serverSide = "defeatedcrow.hac.core.CommonProxyD")
	public static CommonProxyD proxy;

	@Instance("dcs_climate|lib")
	public static ClimateCore instance;

	public static final String PACKAGE_BASE = "dcs";
	public static final String PACKAGE_ID = "dcs_climate";
	public static final CreativeTabs climate = new CreativeTabClimate(MOD_ID);

	public static boolean isDebug = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ClimateConfig.INSTANCE.load(event.getModConfigurationDirectory());
		isDebug = DCUtil.checkDebugModePass(CoreConfigDC.debugPass);
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
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (!CoreConfigDC.disableCustomRecipe) {
			CustomizeVanillaRecipe.initCustomize();
		}

		MobResistantRegister.pre();
		ArmorResistantRegister.pre();

		FluidIDRegisterDC.post();
		MobResistantRegister.post();
		ArmorResistantRegister.post();
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {}
}
