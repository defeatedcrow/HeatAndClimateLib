package defeatedcrow.hac.api.climate;

import net.minecraft.util.MathHelper;

/**
 * エアフロー。
 * 通常バイオームではNormalで、周辺ブロックの要因によって変化する。
 */
public enum DCAirflow {
	TIGHT(0), NORMAL(1), FLOW(2);

	private final int id;

	private DCAirflow(int i) {
		id = i;
	}

	public int getID() {
		return id;
	}

	public static DCAirflow getTypeByID(int i) {
		MathHelper.clamp_int(i, 0, 2);
		for (DCAirflow e : values()) {
			if (i == e.id)
				return e;
		}
		return NORMAL;
	}

}
