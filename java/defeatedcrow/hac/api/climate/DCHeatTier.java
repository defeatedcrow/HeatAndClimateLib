package defeatedcrow.hac.api.climate;

import net.minecraft.util.MathHelper;

/**
 * 熱源の概念。
 */
public enum DCHeatTier {
	ABSOLUTE(-273, -2, 0), COLD(-50, -1, 1), NORMAL(20, 0, 2), HOT(50, 1, 3), OVEN(250, 2, 4), KILN(800, 3, 5), SMELTING(
			1500, 4, 6), UHT(3000, 5, 7);

	private final int temp;
	private final int tier;
	private final int id;

	private DCHeatTier(int t, int i, int n) {
		temp = t;
		tier = i;
		id = n;
	}

	public static DCHeatTier getHeatEnum(int tier) {
		switch (tier) {
		case -2:
			return ABSOLUTE;
		case -1:
			return COLD;
		case 1:
			return HOT;
		case 2:
			return OVEN;
		case 3:
			return KILN;
		case 4:
			return SMELTING;
		case 5:
			return UHT;
		default:
			return NORMAL;
		}
	}

	public DCHeatTier addTier(int i) {
		int ret = tier + i;
		MathHelper.clamp_int(id, -2, 5);
		return getHeatEnum(ret);
	}

	public int getTemp() {
		return temp;
	}

	public int getTier() {
		return tier;
	}

	public int getID() {
		return id;
	}

	public static DCHeatTier getTypeByID(int id) {
		MathHelper.clamp_int(id, 0, 7);
		for (DCHeatTier e : values()) {
			if (id == e.id)
				return e;
		}
		return NORMAL;
	}

}
