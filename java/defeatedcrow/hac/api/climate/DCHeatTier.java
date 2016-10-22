package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.MathHelper;

/**
 * 熱源の概念。<br>
 * バイオームの持つ温度と、範囲内のブロックのもつ温度のうち、最も高い温度を範囲内のHeatTierとする。<br>
 * (温度がマイナスのブロックがあると、上記によって算出したHeatTierを相殺したり、冷媒が必要なレシピに要求される。)<br>
 * <br>
 * ABSOLUTE: 絶対零度。自然には発生しない。<br>
 * FROSTBITE: 凍傷。氷が硬い氷になる。スティーブがダメージを受ける。<br>
 * COLD: 寒冷バイオームの温度。氷が自然発生する。<br>
 * COOL: 冷涼バイオームの温度。氷や雪が溶けなくなる。<br>
 * NORMAL: 常温。熱源として利用は出来ないが、力学的エネルギーは"常温の熱量"として他MOD装置と互換性を持つ。<br>
 * WARM: 温暖バイオームの温度。植物の成長が促進される。発酵のレシピで使われる。<br>
 * HOT: 高温バイオームの温度。乾燥のレシピで使われる。<br>
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
	ABSOLUTE(
			-273,
			-4,
			0,
			0x0000FF),
	// icecream making and cooling
	FROSTBITE(
			-70,
			-3,
			1,
			0x0070FF),
	// cold climate biome
	COLD(
			-20,
			-2,
			2,
			0x00FFFF),
	// cool climate biome
	COOL(
			0,
			-1,
			3,
			0x70FFFF),
	// electric or mechanical energy require
	NORMAL(
			20,
			0,
			4,
			0x00FF30),
	// warm climate biome
	WARM(
			35,
			1,
			5,
			0xA0FF00),
	// drying or brewing
	HOT(
			50,
			2,
			6,
			0xFFE000),
	// cooking
	OVEN(
			220,
			3,
			7,
			0xFFA000),
	// making charcoal, bronze, burn dust
	KILN(
			800,
			4,
			8,
			0xFF5000),
	// making iron or another metal
	SMELTING(
			1500,
			5,
			9,
			0xFF0000),
	// special alloy
	UHT(
			3000,
			6,
			10,
			0xFF00FF),
	// only on data
	INFERNO(
			8000,
			7,
			11,
			0x600020);

	private final int temp;
	private final int tier;
	private final int id;
	private final int color;

	private DCHeatTier(int t, int i, int n, int c) {
		temp = t;
		tier = i;
		id = n;
		color = c;
	}

	public static DCHeatTier getHeatEnum(int tier) {
		if (tier < -4)
			tier = -4;
		if (tier > 7)
			tier = 7;
		switch (tier) {
		case -4:
			return ABSOLUTE;
		case -3:
			return FROSTBITE;
		case -2:
			return COLD;
		case -1:
			return COOL;
		case 1:
			return WARM;
		case 2:
			return HOT;
		case 3:
			return OVEN;
		case 4:
			return KILN;
		case 5:
			return SMELTING;
		case 6:
			return UHT;
		case 7:
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
		MathHelper.clamp_int(id, 0, 11);
		for (DCHeatTier e : values()) {
			if (id == e.id)
				return e;
		}
		return NORMAL;
	}

	public int[] getColor() {
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		return new int[] {
				r,
				g,
				b };
	}

	public int getColorInt() {
		return color;
	}
}
