package defeatedcrow.hac.core.util;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.world.World;

public class DCTimeHelper {

	private DCTimeHelper() {}

	public static long time(World world) {
		return world.getWorldInfo().getWorldTime() % 24000L;
	}

	public static long totalTime(World world) {
		return world.getWorldInfo().getWorldTime();
	}

	public static boolean isDayTime(World world) {
		int t = currentTime(world);
		return t > 5 && t < 18;
	}

	public static int currentTime(World world) {
		long time = time(world);
		time += 6000;
		if (time > 24000)
			time -= 24000;
		return (int) (time / 1000);
	}

	/*
	 * SextiarySector2の季節と互換性を持たせるよう、同じ内容のメソッドを作成
	 */
	public static int getSeason(World world) {
		int day = getDay(world);
		int season = ((day - 1) / CoreConfigDC.seasonFrequency) & 3;
		return season;
	}

	/* int上限でカンスト */
	public static int getDay(World world) {
		long day = totalTime(world) / 24000L;
		day++;
		if (day > Integer.MAX_VALUE)
			day -= Integer.MAX_VALUE;
		return (int) day;
	}

	public static int getWeek(World world) {
		int day = getDay(world);
		int week = (((day - 1) / 7));
		return week;
	}

	public static int getYear(World world) {
		int day = getDay(world);
		int year = (((day - 1) / 120));
		return year;
	}

	public static int getCount(World world) {
		long i = (totalTime(world) % 20L);
		return (int) i;
	}

	public static int getCount2(World world) {
		long f = 1200L / CoreConfigDC.updateFrequency;
		long i = (totalTime(world) % f);
		return (int) i;
	}

	public static EnumSeason getSeasonEnum(World world) {
		int s = getSeason(world);
		if (s == 1) {
			return EnumSeason.SUMMER;
		} else if (s == 2) {
			return EnumSeason.AUTUMN;
		} else if (s == 3) {
			return EnumSeason.WINTER;
		} else {
			return EnumSeason.SPRING;
		}
	}

}
