package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import mezz.jei.api.IModRegistry;

public final class ClimateEffectiveMaker {

	private ClimateEffectiveMaker() {}

	public static void register(IModRegistry registry) {
		registry.addRecipes(DCsJEIPluginLists.climate, DCsJEIPlugin.CLIMATE_UID);
	}

}
