package defeatedcrow.hac.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DCTimeHelper {

	private DCTimeHelper() {}

	public static EnumSeason forcedSeason = null;

	public static long time(World world) {
		return world.getWorldInfo().getWorldTime() % 24000L;
	}

	public static long totalTime(World world) {
		return world.getWorldInfo().getWorldTime();
	}

	public static boolean isDayTime(World world) {
		int t = currentTime(world);
		return t >= CoreConfigDC.dayTime[0] && t <= CoreConfigDC.dayTime[1];
	}

	public static int currentTime(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			int hour = cal.get(cal.HOUR_OF_DAY);
			return hour;
		}
		long time = time(world);
		time += 6000;
		if (time > 24000)
			time -= 24000;
		return (int) (time / 1000);
	}

	public static int realMinute() {
		Calendar cal = Calendar.getInstance();
		int min = cal.get(cal.MINUTE);
		return min;
	}

	public static int currentMinute(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			int min = cal.get(cal.MINUTE);
			return min;
		}
		long time = time(world);
		return (int) (time % 1000) * 60 / 1000;
	}

	public static int getCount(World world) {
		long i = (totalTime(world) % CoreConfigDC.entityInterval);
		return (int) i;
	}

	public static int getCount2(World world) {
		long f = 1200L / CoreConfigDC.updateFrequency;
		long i = (totalTime(world) % f);
		return (int) i;
	}

	// 表示用
	public static String getDate(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			SimpleDateFormat format = new SimpleDateFormat(CoreConfigDC.dateFormat);
			String s = format.format(date);
			return s;
		} else {
			int displayDay = getDisplayDay(world);
			int day = getDay(world);
			if (day > CoreConfigDC.yearLength) {
				day %= CoreConfigDC.yearLength;
			}
			int year = getYear(world);
			return "Year " + year + " / Day " + day;
		}
	}

	public static int getDay(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			return cal.get(cal.DAY_OF_YEAR);
		}
		long day = totalTime(world) / 24000L;
		return DCDay.getDay(day);
	}

	public static int getDisplayDay(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			return cal.get(cal.DAY_OF_YEAR);
		}
		long day = totalTime(world) / 24000L;
		return DCDay.getDisplayDay(day);
	}

	public static int getWeek(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			return cal.get(cal.DAY_OF_WEEK);
		}
		int day = getDay(world);
		return DCDay.getWeek(day);
	}

	public static int getYear(World world) {
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			return cal.get(cal.YEAR);
		}
		int day = getDay(world);
		return DCDay.getYear(day);
	}

	public static int getSeason(World world) {
		int season = 0;
		int d = 0;
		if (CoreConfigDC.enableRealTime) {
			Calendar cal = Calendar.getInstance();
			d = cal.get(cal.DAY_OF_YEAR);
			return DCDay.getSeason(d, true);
		} else {
			d = getDay(world);
			return DCDay.getSeason(d, false);
		}
	}

	public static EnumSeason getSeasonEnum(World world) {
		if (forcedSeason != null) {
			return forcedSeason;
		}
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

	public static float getTimeOffset(World world, Biome b) {
		if (world.provider.isNether()) {
			return 0F;
		}
		float offset = 0F;
		int t = DCTimeHelper.currentTime(world);
		int sD = CoreConfigDC.dayTime[0];
		int eD = CoreConfigDC.dayTime[1];
		if (t < sD - 2 || t > eD + 2) {
			offset = (float) CoreConfigDC.nightEffect;
		} else if (t < sD || t > eD) {
			offset = (float) CoreConfigDC.nightEffect * 0.5F;
		}
		if (BiomeDictionary.hasType(b, Type.WET) || BiomeDictionary.hasType(b, Type.WATER) || BiomeDictionary
				.hasType(b, Type.DENSE)) {
			offset *= 0.5F;
		} else if (BiomeDictionary.hasType(b, Type.DRY) || BiomeDictionary.hasType(b, Type.SANDY) || BiomeDictionary
				.hasType(b, Type.WASTELAND)) {
			if (BiomeDictionary.hasType(b, Type.DRY) && (BiomeDictionary.hasType(b, Type.SANDY) || BiomeDictionary
					.hasType(b, Type.WASTELAND)) && BiomeDictionary.hasType(b, Type.HOT))
				offset *= 4F;
			else
				offset *= 2F;
		}
		return offset;
	}

	public static int[] seasonPeriod(EnumSeason season) {
		switch (season) {
		case AUTUMN:
			return CoreConfigDC.autumnDate;
		case SPRING:
			return CoreConfigDC.springDate;
		case SUMMER:
			return CoreConfigDC.summerDate;
		case WINTER:
			return CoreConfigDC.winterDate;
		default:
			return CoreConfigDC.springDate;
		}
	}

}
