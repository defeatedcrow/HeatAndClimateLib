package defeatedcrow.hac.api.climate;

import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;

public enum EnumSeason {

	SPRING(EnumDyeColor.PINK, 0, CoreConfigDC.seasonEffects[0], "dcs.tip.spring"),
	SUMMER(EnumDyeColor.LIME, 1, CoreConfigDC.seasonEffects[1], "dcs.tip.summer"),
	AUTUMN(EnumDyeColor.ORANGE, 2, CoreConfigDC.seasonEffects[2], "dcs.tip.autumn"),
	WINTER(EnumDyeColor.LIGHT_BLUE, 3, CoreConfigDC.seasonEffects[3], "dcs.tip.winter");

	public final EnumDyeColor color;
	public final int id;
	public final double temp;
	public final String name;

	private EnumSeason(EnumDyeColor c, int i, double f, String n) {
		color = c;
		id = i;
		name = n;
		temp = f;
	}

	public String getName() {
		return I18n.format(name);
	}

}
