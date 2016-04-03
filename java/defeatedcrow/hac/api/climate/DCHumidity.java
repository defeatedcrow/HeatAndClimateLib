package defeatedcrow.hac.api.climate;

import net.minecraft.util.MathHelper;

/**
 * 湿度。
 * 基本的にバイオームによって変動する概念だが、水を置いてレベルを上げられる。
 */
public enum DCHumidity {
	DRY(0), NORMAL(1), WET(2), UNDERWATER(3);

	private final int id;

	private DCHumidity(int i) {
		id = i;
	}

	public int getID() {
		return id;
	}

	public static DCHumidity getTypeByID(int i) {
		MathHelper.clamp_int(i, 0, 3);
		for (DCHumidity e : values()) {
			if (i == e.id)
				return e;
		}
		return NORMAL;
	}

}
