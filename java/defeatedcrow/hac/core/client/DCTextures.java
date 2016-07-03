package defeatedcrow.hac.core.client;

import net.minecraft.util.ResourceLocation;

public enum DCTextures {
	POTION("textures/blocks/gray_effect.png"), HUD("textures/gui/hud_climate.png"), GRAY(
			"textures/blocks/gray_effect.png");

	private final String name;

	private DCTextures(String nameIn) {
		name = nameIn;
	}

	public String getName() {
		return name;
	}

	public ResourceLocation getRocation() {
		return new ResourceLocation("dcs_climate", name);
	}

}
