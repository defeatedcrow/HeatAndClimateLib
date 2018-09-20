package defeatedcrow.hac.api.climate;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.MathHelper;

/**
 * エアフロー。
 * 通常バイオームではNormalで、周辺ブロックの要因によって変化する。<br>
 * TIGHT: 範囲内に通気がない状態。ブロックに囲まれて隙間がない状態など。<br>
 * NORMAL: 空が見えない状態。屋内判定。<br>
 * FLOW: 範囲内のいずれかのブロックで空が見えている状態。通気のある屋外。<br>
 * WIND: 風を起こすブロックなどが範囲内にある状態。空気を消費したり、換気が必要なレシピに要求される。
 */
public enum DCAirflow {
	TIGHT(0, 0x202020),
	NORMAL(1, 0xEEFFFF),
	FLOW(2, 0x00E115),
	WIND(3, 0x00AEFF);

	private final int id;
	private final int color;

	private DCAirflow(int i, int c) {
		id = i;
		color = c;
	}

	public int getID() {
		return id;
	}

	public int[] getColor() {
		int r = (color >> 8) & 255;
		int g = (color >> 4) & 255;
		int b = color & 255;
		return new int[] {
				r, g, b
		};
	}

	public int getColorInt() {
		return color;
	}

	public static DCAirflow getTypeByID(int i) {
		MathHelper.clamp(i, 0, 3);
		for (DCAirflow e : values()) {
			if (i == e.id)
				return e;
		}
		return NORMAL;
	}

	public static List<DCAirflow> createList() {
		List<DCAirflow> tiers = new ArrayList<DCAirflow>();

		for (DCAirflow t : DCAirflow.values()) {
			tiers.add(t);
		}

		return tiers;
	}

	public static DCAirflow getFromName(String name) {
		if (name != null)
			for (DCAirflow t : DCAirflow.values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
		return TIGHT;
	}

}
