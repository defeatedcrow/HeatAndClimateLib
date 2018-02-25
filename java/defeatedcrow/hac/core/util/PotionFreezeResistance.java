package defeatedcrow.hac.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionFreezeResistance extends Potion {

	public PotionFreezeResistance() {
		super(false, 0x5050FF);
		this.setPotionName("dcs.potion.freeze_res");
		this.setIconIndex(1, 1);
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return false;
	}

	@Override
	public int getStatusIconIndex() {
		Minecraft.getMinecraft().getTextureManager()
				.bindTexture(new ResourceLocation("dcs_climate:textures/gui/icons_potion_2.png"));
		return super.getStatusIconIndex();
	}

	@Override
	public boolean hasStatusIcon() {
		return true;
	}

}
