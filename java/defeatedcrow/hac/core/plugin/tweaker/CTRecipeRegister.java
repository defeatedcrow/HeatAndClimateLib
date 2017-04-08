package defeatedcrow.hac.core.plugin.tweaker;

import minetweaker.MineTweakerAPI;

/**
 * 必要な中継クラス大杉、まじなんなの
 */
public class CTRecipeRegister {
	public static final String MODID = "dcs_climate|lib";

	public static void load() {
		MineTweakerAPI.registerClass(CTClimateSmelting.class);
	}

}
