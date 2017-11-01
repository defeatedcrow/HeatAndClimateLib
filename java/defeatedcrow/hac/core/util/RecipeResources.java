package defeatedcrow.hac.core.util;

import defeatedcrow.hac.core.ClimateCore;
import net.minecraft.util.ResourceLocation;

public class RecipeResources {

	private final String domein;
	private int count = 0;

	public RecipeResources(String d) {
		domein = d;
	}

	public static final RecipeResources CORE = new RecipeResources("core");

	public ResourceLocation getRecipeName() {
		count++;
		return new ResourceLocation(ClimateCore.MOD_ID, domein + "_" + count);
	}

}
