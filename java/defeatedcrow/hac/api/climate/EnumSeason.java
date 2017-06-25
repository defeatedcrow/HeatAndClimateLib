package defeatedcrow.hac.api.climate;

import net.minecraft.item.EnumDyeColor;

public enum EnumSeason {

	SPRING(EnumDyeColor.PINK, 0),
	SUMMER(EnumDyeColor.GREEN, 1),
	AUTUMN(EnumDyeColor.ORANGE, 2),
	WINTER(EnumDyeColor.BLUE, 3);

	public final EnumDyeColor color;
	public final int id;

	private EnumSeason(EnumDyeColor c, int i) {
		color = c;
		id = i;
	}

}
