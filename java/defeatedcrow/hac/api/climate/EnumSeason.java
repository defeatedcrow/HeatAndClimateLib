package defeatedcrow.hac.api.climate;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;

public enum EnumSeason {

	SPRING(EnumDyeColor.PINK, 0, "dcs.tip.spring"),
	SUMMER(EnumDyeColor.LIME, 1, "dcs.tip.summer"),
	AUTUMN(EnumDyeColor.ORANGE, 2, "dcs.tip.autumn"),
	WINTER(EnumDyeColor.BLUE, 3, "dcs.tip.winter");

	public final EnumDyeColor color;
	public final int id;
	public final String name;

	private EnumSeason(EnumDyeColor c, int i, String n) {
		color = c;
		id = i;
		name = n;
	}

	public String getName() {
		return I18n.format(name);
	}

}
