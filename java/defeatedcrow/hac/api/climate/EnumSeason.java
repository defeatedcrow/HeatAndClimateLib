package defeatedcrow.hac.api.climate;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;

public enum EnumSeason {

	SPRING(EnumDyeColor.PINK, 0, 0.05F, "dcs.tip.spring"),
	SUMMER(EnumDyeColor.LIME, 1, 0.4F, "dcs.tip.summer"),
	AUTUMN(EnumDyeColor.ORANGE, 2, 0F, "dcs.tip.autumn"),
	WINTER(EnumDyeColor.LIGHT_BLUE, 3, -0.4F, "dcs.tip.winter");

	public final EnumDyeColor color;
	public final int id;
	public final float temp;
	public final String name;

	private EnumSeason(EnumDyeColor c, int i, float f, String n) {
		color = c;
		id = i;
		name = n;
		temp = f;
	}

	public String getName() {
		return I18n.format(name);
	}

}
