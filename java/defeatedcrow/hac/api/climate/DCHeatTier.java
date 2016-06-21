package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.MathHelper;

/**
 * 熱源の概念。<br>
 * バイオームの持つ温度と、範囲内のブロックのもつ温度のうち、最も高い温度を範囲内のHeatTierとする。<br>
 * (温度がマイナスのブロックがあると、上記によって算出したHeatTierを相殺したり、冷媒が必要なレシピに要求される。)<br>
 * <br>
 * ABSOLUTE: 絶対零度。自然には発生しない。<br>
 * FROSTBITE: 凍傷。凍結レシピに向いた温度。氷が自然発生する。スティーブがダメージを受ける。<br>
 * COLD: 寒冷バイオームの温度。氷や雪が溶けなくなる。<br>
 * NORMAL: 常温。熱源として利用は出来ないが、力学的エネルギーは"常温の熱量"として他MOD装置と互換性を持つ。<br>
 * HOT: 温暖バイオームの温度。植物の成長が促進される。乾燥、発酵のレシピで使われる。<br>
 * OVEN: 調理に適した温度帯。この温度帯から、防具なしではスティーブがダメージを受ける。<br>
 * KILN: 火ブロックや溶岩から得られる温度。草木の焼却、低い温度で出来る精錬のレシピに要求される。<br>
 * SMELT: 多くの金属精錬に要求される。得るためにはMOD装置が必要。<br>
 * UHT: 超高温炉。特殊なレシピにのみ要求される。<br>
 * INFERNO: 焦熱地獄。通常は発生しない。<br>
 * <br>
 * レシピに適合する温度は+1の幅がある。例えば、環境温度がKILNの場合、KILN要求レシピと、OVEN要求レシピの両方が稼働する。<br>
 * HOT以下のレシピはKILNでは高すぎて適応できない。また、SMELT以上のレシピは加熱不足で適応できない。
 */
public enum DCHeatTier {
	// absolute
	ABSOLUTE(-273, -3, 0),
	// icecream making and cooling
	FROSTBITE(-70, -2, 1),
	// cold climate biome
	COLD(-10, -1, 2),
	// electric or mechanical energy require
	NORMAL(20, 0, 3),
	// drying or brewing
	HOT(50, 1, 4),
	// cooking
	OVEN(220, 2, 5),
	// making charcoal, bronze, burn dust
	KILN(800, 3, 6),
	// making iron or another metal
	SMELTING(1500, 4, 7),
	// special alloy
	UHT(3000, 5, 8),
	// only on data
	INFERNO(8000, 6, 9);

	private final int temp;
	private final int tier;
	private final int id;

	private DCHeatTier(int t, int i, int n) {
		temp = t;
		tier = i;
		id = n;
	}

	public static DCHeatTier getHeatEnum(int tier) {
		if (tier < -3)
			tier = -3;
		if (tier > 6)
			tier = 6;
		switch (tier) {
		case -3:
			return ABSOLUTE;
		case -2:
			return FROSTBITE;
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
		case 6:
			return INFERNO;
		default:
			return NORMAL;
		}
	}

	public DCHeatTier addTier(int i) {
		int ret = tier + i;
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
		MathHelper.clamp_int(id, 0, 9);
		for (DCHeatTier e : values()) {
			if (id == e.id)
				return e;
		}
		return NORMAL;
	}

}
