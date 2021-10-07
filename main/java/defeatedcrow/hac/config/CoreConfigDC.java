package defeatedcrow.hac.config;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CoreConfigDC {

	private CoreConfigDC() {}

	public static final CoreConfigDC INSTANCE = new CoreConfigDC();

	public static String debugPass = "Input the password here";
	private final String BR = System.getProperty("line.separator");

	// key
	public static int charmWarpKey = 0x2D; // X
	public static int gauntletKey = 0x2E; // C
	public static int sitKey = 0x0F; // Tab

	public static int altJumpKey = -1;
	public static int altSneakKey = -1;

	public static int[] ranges = new int[] { 2, 1, 1 };
	public static double[] seasonEffects = new double[] { 0.05D, 0.4D, 0.0D, -0.4D };
	public static double[] weatherEffects = new double[] { -0.2D, 0.2D };
	public static double nightEffect = -0.2D;

	public static boolean wall = true;

	// render
	public static boolean showAltTips = true;
	public static boolean showDamageIcon = true;
	public static int iconX = 0;
	public static int iconY = 0;
	public static double waterFix = 0.01D;
	public static double lavaFix = 0.01D;
	public static boolean hudEffect = true;

	public static boolean enableAdvHUD = true;
	public static boolean useAnalogueHUD = true;
	// public static int iconHX = 0;
	// public static int iconHY = -48;
	public static int[] offsetHUD = { 0, 300 };
	public static String tex1 = "textures/gui/hud_climate_normal.png";
	public static String tex2 = "textures/gui/hud_climate_drought.png";
	public static String tex3 = "textures/gui/hud_climate_rain.png";
	public static String tex4 = "textures/gui/hud_climate_thermometer.png";
	public static boolean showBiome = true;
	public static boolean showSeason = true;
	public static boolean showDay = true;
	public static boolean showClimate = true;
	public static int[] offsetBiome = { 15, 5 };
	public static int[] offsetSeason = { 5, -8 };
	public static int[] offsetClimate = { 10, 15 };

	// difficulty
	public static boolean climateDam = true;
	public static boolean peacefulDam = false;
	public static int damageDifficulty = 1; // 0-2
	public static boolean burntFood = false;
	public static double food_amount = 1.0D;
	public static boolean heldItem = true;

	// entity
	public static boolean mobClimateDamage = true;
	public static boolean sharePotionWithRidingMob = true;
	public static int entityInterval = 60;
	public static String[] entityBlackList = new String[] {
		"minecraft:squid",
		"minecraft:bat",
		"minecraft:villager",
		"ModID:entityRegistryName" };
	public static final List<Class<? extends Entity>> blackListEntity = Lists.newArrayList();

	// recipe
	public static boolean enableVanilla = false;
	public static boolean enableVanillaCrop = true;
	public static boolean enableFarmland = true;
	public static boolean enableIce = true;
	public static boolean enableSnow = true;
	public static int updateFrequency = 5;
	public static boolean disableCustomRecipe = false;
	public static boolean enableDropItemSmelting = false;

	// world
	public static boolean enableFreezeDrop = true;
	public static boolean enableDeepWater = true;
	public static boolean enableUnderLake = true;
	public static boolean enableForestLake = true;
	public static int droughtFrequency = 60;
	public static boolean enableSeasonEffect = true;
	public static boolean enableSeasonTemp = true;
	public static boolean enableWeatherEffect = true;
	public static boolean enableTimeEffect = true;
	public static boolean enableSubmergedCave = false;
	public static int cropUpdateInterval = 80;

	// time
	public static int yearLength = 240;
	// public static boolean enableRealSeason = false;
	public static boolean enableRealTime = false;
	public static boolean enableSouthernHemisphere = false;
	public static int[] springDate = { 59, 150 };
	public static int[] summerDate = { 151, 242 };
	public static int[] autumnDate = { 243, 303 };
	public static int[] winterDate = { 304, 58 };
	public static int[] dayTime = { 6, 17 };
	public static EnumSeason overYear = EnumSeason.WINTER;
	public static int startDate = 40;
	public static String dateFormat = "yyyy/MM/dd";

	// hardmode
	public static boolean harderVanilla = false;
	public static boolean infernalInferno = false;
	public static boolean enableSuffocation = false;
	public static boolean enableHumidity = false;
	public static boolean tightUnderworld = false;
	public static boolean harderMachine = false;
	public static boolean harderCrop = false;
	public static boolean harderMagic = false;
	public static int harderMagicCost = 0;
	public static double harderMagicCostAmount = 1.0D;

	public static String[] updateBlackList = new String[] {
		"minecraft:leaves:32767",
		"minecraft:leaves2:32767",
		"minecraft:tallgrass:32767",
		"minecraft:snow_layer:32767",
		"ModID:sampleBlock:sampleMeta" };
	public static final List<BlockSet> blackListBlock = Lists.newArrayList();

	public void load(Configuration cfg) {

		try {
			cfg.load();

			cfg.addCustomCategoryComment("debug setting", "It only for the authors of this mod.");
			cfg.addCustomCategoryComment("difficulty setting", "This setting is for changing difficulty of this mod.");
			cfg.addCustomCategoryComment("render setting", "This setting is for such as display and model.");
			cfg.addCustomCategoryComment("world setting", "This setting is for world gen.");
			cfg.addCustomCategoryComment("key setting", "This mod is not using the Forge KeyHandler. Please setting it in here.");
			cfg.addCustomCategoryComment("entity setting", "This setting is for entities.");
			cfg.addCustomCategoryComment("setting", "This setting is for game play.");
			cfg.addCustomCategoryComment("hardmode setting", "This may destroy your game play. Be careful!");
			cfg.addCustomCategoryComment("time setting", "This setting is for time and seasons.");

			Property debug = cfg
					.get("debug setting", "Debug Mode Pass", debugPass, "Input the password for starting in debug mode. This is only for authors.");

			Property climate_dam = cfg
					.get("difficulty setting", "Enable Climate Damage", climateDam, "Enable damage from hot or cold climate.");

			Property peace_dam = cfg
					.get("difficulty setting", "Enable Peaceful Damage", peacefulDam, "Enable climate damage at peaceful setting.");

			Property diff_dam = cfg
					.get("difficulty setting", "Difficulty of Climate Damage", damageDifficulty, "Set difficulty of climate damage. 0:sweet 1:normal 2:bitter");

			Property burnt_food = cfg
					.get("difficulty setting", "Enable Burnt Food", burntFood, "Enable burnt food by high tier heat.");

			Property helditem_dam = cfg
					.get("difficulty setting", "Enable Helditem Climate Effect", heldItem, "The held item (ItemBlock) affect the climate damage calculation.");

			Property vanilla_block = cfg
					.get("world setting", "Enable Vanilla Block Recipe", enableVanilla, "Enable climate change of vanilla blocks.");

			Property vanilla_crop = cfg
					.get("world setting", "Enable Vanilla Crop Growth", enableVanillaCrop, "Enable HaC growth of vanilla crops.");

			Property vanilla_farmland = cfg
					.get("world setting", "Enable Moisture Farmland", enableFarmland, "Enable to moisture farmland in WET humidity.");

			Property vanilla_ice = cfg
					.get("world setting", "Enable Ice Effect", enableIce, "Enables the climate to affect the ice.");

			Property vanilla_snow = cfg
					.get("world setting", "Enable Snow Effect", enableSnow, "Enables the climate to affect the snow layer.");

			Property update_block = cfg
					.get("world setting", "Set Update Frequency", updateFrequency, "Set the number of the update times per sec.");

			Property water_cave = cfg
					.get("world setting", "Enable Water Caves", enableDeepWater, "Enable generating water blocks instead of lava blocks in the deep caves.");

			Property under_lake = cfg
					.get("world setting", "Enable Modificated Lake", enableUnderLake, "Enable modification the underground lakes.");

			Property forest_lake = cfg
					.get("world setting", "Disable Forest Lava Lake", enableForestLake, "Disable generation the lava lakes on ground of forest biomes.");

			Property freeze_drop = cfg
					.get("world setting", "Enable Freeze EntityItem", enableFreezeDrop, "EntityItems avoids to despawn in cold temp than the FROSTBITE tier.");

			Property alt_tips = cfg
					.get("render setting", "Enable Alt Tooltip", showAltTips, "Enable additional tooltips for harvest level, and climate registance of items with F3+H.");

			Property hud_icon = cfg
					.get("render setting", "Enable Thermal Damage Icon on HUD", showDamageIcon, "Enable the heart-shaped icon on HUD for display of thermal damage.");

			Property hud_x = cfg
					.get("render setting", "Thermal Damage Icon Offset X", iconX, "Set the amount of Xoffset of the thermal damage icon.");

			Property hud_y = cfg
					.get("render setting", "Thermal Damage Icon Offset Y", iconY, "Set the amount of Yoffset of the thermal damage icon.");

			Property hud_icon2 = cfg
					.get("render setting", "Climate HUD Info", enableAdvHUD, "Enable display the climate info on HUD.");

			Property hud_icon3 = cfg
					.get("render setting", "Climate HUD Analogue Thermometer", useAnalogueHUD, "Enable display the analogue thermometer on HUD.");

			// Property hud_x2 = cfg
			// .get("render setting", "Climate HUD Info Offset X", iconHX, "Set the amount of Xoffset of the climate
			// info.");
			//
			// Property hud_y2 = cfg
			// .get("render setting", "Climate HUD Info Offset Y", iconHY, "Set the amount of Yoffset of the climate
			// info.");

			Property hud_biome = cfg
					.get("render setting", "Enable HUD Biome Name", showBiome, "Enable display the biome name on HUD.");

			Property hud_season = cfg
					.get("render setting", "Enable Season Name", showSeason, "Enable display the season name on HUD.");

			Property hud_day = cfg.get("render setting", "Enable HUD Date", showDay, "Enable display the date on HUD.");

			Property hud_climate = cfg
					.get("render setting", "Enable HUD Climate Name", showClimate, "Enable display the climate parameter on HUD.");

			Property off_hud = cfg
					.get("render setting", "Offset Climate Info HUD", offsetHUD, "Set the amount of offset of the climate info HUD. 0,0 is the upper left.");

			Property off_biome = cfg
					.get("render setting", "Offset HUD Biome Name", offsetBiome, "Set the amount of offset of the biome name in HUD.");

			Property off_season = cfg
					.get("render setting", "Offset HUD Season Name", offsetSeason, "Set the amount of offset of the season name in HUD.");

			Property off_climate = cfg
					.get("render setting", "Offset HUD Climate Name", offsetClimate, "Set the amount of offset of the climate parameter in HUD.");

			Property hud_effect = cfg
					.get("render setting", "Enable Display Effect of Climate", hudEffect, "Enable the display effect of high or low temperature.");

			Property water = cfg
					.get("render setting", "Density of Water Fog Fix", waterFix, "Set fog density in water");

			Property warp_key = cfg
					.get("key setting", "Charm Use Key", charmWarpKey, "Set key number for using jewel charm effects. Default key is X(45)." + BR + "If you don't want this effect, set 0.");

			Property gauntlet_key = cfg
					.get("key setting", "Gauntlet Use Key", gauntletKey, "Set key number for using jewel gauntlet effects. Default key is C(46)." + BR + "If you don't want this effect, set 0.");

			Property jump_key = cfg
					.get("key setting", "Jump Key", altJumpKey, "Set key number for jumping. Default key is same as the vanilla setting." + BR + "If you want to use the default setting, set -1.");

			Property sneak_key = cfg
					.get("key setting", "Sneak Key", altSneakKey, "Set key number for sneaking. Default key is same as the vanilla setting." + BR + "If you want to use the default setting, set -1.");

			Property enableWall = cfg
					.get("setting", "Thermal Insulation Wall", wall, "Some of stone blocks enable to have a thermal insulation property.");

			Property disableCustom = cfg
					.get("setting", "Disable Recipe Customize", disableCustomRecipe, "Disable replacing the recipe with the ore dictionary.");

			Property vanilla_harder = cfg
					.get("hardmode setting", "Enable Harder Vanilla Block Recipe", harderVanilla, "Enable harder climate recipe of vanilla blocks.");

			Property inferno = cfg
					.get("hardmode setting", "Infernal Nether world", infernalInferno, "Set the temperature of Nether to maximum.");

			Property suffocation = cfg
					.get("hardmode setting", "Enable Suffocation Damage", enableSuffocation, "Enable the suffocation effect when creatures or players in tight space.");

			Property humidity = cfg
					.get("hardmode setting", "Enable Humidity Damage", enableHumidity, "Enable the humidity damage to creatures or players.");

			Property harderM = cfg
					.get("hardmode setting", "Occupational Accident Machine", harderMachine, "Adds contact damage to the torque machine.");

			Property harderC = cfg
					.get("hardmode setting", "Harder Crop Mode", harderCrop, "The growing conditions of the crop become very narrow.");

			Property harderM2 = cfg
					.get("hardmode setting", "Harder Magic", harderMagic, "Some magic accessories require a consumption the player resource to use.");

			Property harderMCostT = cfg
					.get("hardmode setting", "Harder Magic Cost Type", harderMagicCost, "Select player resources that hardmode magic accessories consume." + BR + "0: Exp, 1: Hunger, 2: Health");

			Property harderMCostA = cfg
					.get("hardmode setting", "Harder Magic Cost Amount", harderMagicCostAmount, "Set the herdmode magic consumption. 0.0-100.0" + BR + "The static ability: x0, the triggered ability: x1/2, the X-key ability: x1, all: xTier.");

			Property drought = cfg
					.get("world setting", "Drought Frequency", droughtFrequency, "Set the number of days of fine weather required for drought.");

			Property tight = cfg
					.get("hardmode setting", "Anaerobic Underworld", tightUnderworld, "Set the indoor underground (<Y30) airflow to tight.");

			Property weather = cfg
					.get("world setting", "Enable Weather Effect", enableWeatherEffect, "Enable temperature change by the weather.");

			Property time = cfg
					.get("world setting", "Enable Night Effect", enableTimeEffect, "Enable nighttime temperature drop.");

			Property season = cfg
					.get("world setting", "Enable Season Effect", enableSeasonEffect, "Enable temperature change by the season.");

			Property seasonT = cfg
					.get("world setting", "Enable Vanilla Temperature Effect", enableSeasonTemp, "Enable vanilla temperature change by the season and altitude." + BR + "It affects the vanilla system. ex. the biome color, the world generation");

			Property lava = cfg.get("render setting", "Density of Lava Fog Fix", lavaFix, "Set fog density in lava");

			Property submerged = cfg
					.get("world setting", "Enable Submerged Ocean Cave", enableSubmergedCave, "Enable the submerged cave in ocean biomes.");

			Property cropInterval = cfg
					.get("world setting", "Update Interval of Crop Growing", cropUpdateInterval, "Set interval tick of HaC crop growing. 20-1200");

			Property dropSmelting = cfg
					.get("setting", "Enable DropItem Recipe", enableDropItemSmelting, "Enable all climate smelting and vanilla smelting in drop item state.");

			Property mobDamage = cfg
					.get("entity setting", "Enable Mob Climate Damage", mobClimateDamage, "Enable damage from hot or cold climate to mobs (excluding player).");

			Property sharePotion = cfg
					.get("entity setting", "Enable Sharing Potion", sharePotionWithRidingMob, "Enable sharing potion effects with riding mob.");

			Property entityRate = cfg
					.get("entity setting", "Entity Update Interval", entityInterval, "Set the number of tick of entity update interval. 20-1200");

			Property b_list = cfg
					.get("world setting", "Tick Update Blacklist", updateBlackList, "Please add block registry names you want exclude from climate tick update for reducing lag.");

			Property e_list = cfg
					.get("entity setting", "Climate Damage Blacklist", entityBlackList, "Please add entity registry names you want exclude from climate tick update for reducing lag.");

			Property season_d = cfg
					.get("world setting", "Seasonal Influence of Temperature", seasonEffects, "Setting of the amount of temperature change by each season." + BR + "default: spr +0.05 / smr +0.4 / aut 0 / wtr -0.4");

			Property weather_d = cfg
					.get("world setting", "Weather Influence of Temperature", weatherEffects, "Setting of the amount of temperature change by weather." + BR + "default: rain -0.2 / drought +0.2");

			Property night_d = cfg
					.get("world setting", "Nighttime Influence of Temperature", nightEffect, "Setting of the amount of temperature change by nighttime." + BR + "default: -0.2");

			Property food_a = cfg
					.get("setting", "Default Food Saturation Amount", food_amount, "Set the magnification of the food effect amount. (0.1-2.0)");

			Property yearL = cfg
					.get("time setting", "Year Length", yearLength, "Set the number of days in the year." + BR + "When it is not 365, the season beginning date is converted to 365 days / year.");

			Property realT = cfg
					.get("time setting", "Enable Real Time", enableRealTime, "Use the real time for the season of HaC.");

			// Property realS = cfg
			// .get("time setting", "Enable Real Season", enableRealSeason, "Use the real season for the season of
			// HaC.");

			Property startD = cfg
					.get("time setting", "Start Date", startDate, "Set the date of the world beginning." + BR + "Default: first day of spring.");

			Property sprPeriod = cfg
					.get("time setting", "Period of Spring", springDate, "Set the dates for the beginning and end of spring.");

			Property smrPeriod = cfg
					.get("time setting", "Period of Summer", summerDate, "Set the dates for the beginning and end of summer.");

			Property autPeriod = cfg
					.get("time setting", "Period of Autumn", autumnDate, "Set the dates for the beginning and end of autumn.");

			Property wtrPeriod = cfg
					.get("time setting", "Period of Winter", winterDate, "Set the dates for the beginning and end of winter.");

			Property dateF = cfg
					.get("time setting", "Realtime Date Format", dateFormat, "Set the date format used in  real-time settings.");

			debugPass = debug.getString();
			climateDam = climate_dam.getBoolean();
			peacefulDam = peace_dam.getBoolean();
			heldItem = helditem_dam.getBoolean();
			showAltTips = alt_tips.getBoolean();
			charmWarpKey = warp_key.getInt();
			enableVanilla = vanilla_block.getBoolean();
			enableVanillaCrop = vanilla_crop.getBoolean();
			enableFarmland = vanilla_farmland.getBoolean();
			enableIce = vanilla_ice.getBoolean();
			enableSnow = vanilla_snow.getBoolean();
			burntFood = burnt_food.getBoolean();
			showDamageIcon = hud_icon.getBoolean();
			enableDeepWater = water_cave.getBoolean();
			enableUnderLake = under_lake.getBoolean();
			enableForestLake = forest_lake.getBoolean();
			enableFreezeDrop = freeze_drop.getBoolean();
			wall = enableWall.getBoolean();
			disableCustomRecipe = disableCustom.getBoolean();
			harderVanilla = vanilla_harder.getBoolean();
			infernalInferno = inferno.getBoolean();
			enableSuffocation = suffocation.getBoolean();
			enableHumidity = humidity.getBoolean();
			tightUnderworld = tight.getBoolean();
			harderMachine = harderM.getBoolean();
			harderCrop = harderC.getBoolean();
			harderMagic = harderM2.getBoolean();
			enableWeatherEffect = weather.getBoolean();
			enableTimeEffect = time.getBoolean();
			enableSeasonEffect = season.getBoolean();
			enableSeasonTemp = seasonT.getBoolean();
			hudEffect = hud_effect.getBoolean();
			enableSubmergedCave = submerged.getBoolean();
			enableDropItemSmelting = dropSmelting.getBoolean();
			mobClimateDamage = mobDamage.getBoolean();
			sharePotionWithRidingMob = sharePotion.getBoolean();

			updateBlackList = b_list.getStringList();
			entityBlackList = e_list.getStringList();

			int d = diff_dam.getInt();
			if (d < 0 || d > 2)
				d = 2;
			damageDifficulty = d;

			int mc = harderMCostT.getInt();
			if (mc < 0 || mc > 2)
				mc = 0;
			harderMagicCost = mc;

			double dm = harderMCostA.getDouble();
			if (dm < 0D || dm > 100D) {
				dm = 1D;
			}
			harderMagicCostAmount = dm;

			int h = update_block.getInt();
			if (h < 0 || h > 20)
				h = 1;
			updateFrequency = h;

			int dr = drought.getInt();
			if (dr < 2 || dr > 1000)
				dr = 120;
			droughtFrequency = dr;

			int sf = yearL.getInt();
			if (sf < 3 || sf > 3650)
				sf = 120;
			yearLength = sf;

			iconX = hud_x.getInt();
			iconY = hud_y.getInt();

			charmWarpKey = warp_key.getInt();
			gauntletKey = gauntlet_key.getInt();

			altJumpKey = jump_key.getInt();
			altSneakKey = sneak_key.getInt();

			double d1 = water.getDouble();
			if (d1 < 0D || d1 > 10D) {
				d1 = 0.0D;
			}
			waterFix = d1;

			double d2 = lava.getDouble();
			if (d2 < 0D || d2 > 10D) {
				d2 = 0.0D;
			}
			lavaFix = d2;

			int ei = entityRate.getInt();
			if (ei < 20 || ei > 1200)
				ei = 100;
			entityInterval = ei;

			int ci = cropInterval.getInt();
			if (ci < 20 || ci > 1200)
				ci = 100;
			entityInterval = ci;

			food_amount = food_a.getDouble();
			if (food_amount < 0) {
				food_amount = 0.1D;
			}
			if (food_amount > 2.0) {
				food_amount = 2.0D;
			}

			if (season_d.isDoubleList() && season_d.getDoubleList().length >= 4) {
				for (int i = 0; i < 4; i++) {
					seasonEffects[i] = season_d.getDoubleList()[i];
				}
			}

			if (weather_d.isDoubleList() && weather_d.getDoubleList().length >= 2) {
				for (int i = 0; i < 2; i++) {
					weatherEffects[i] = weather_d.getDoubleList()[i];
				}
			}

			double d3 = night_d.getDouble();
			nightEffect = d3;

			enableAdvHUD = hud_icon2.getBoolean();
			useAnalogueHUD = hud_icon3.getBoolean();
			showBiome = hud_biome.getBoolean();
			showSeason = hud_season.getBoolean();
			showDay = hud_day.getBoolean();
			showClimate = hud_climate.getBoolean();
			if (off_hud.isIntList() && off_hud.getIntList().length > 1) {
				for (int i = 0; i < 2; i++) {
					offsetHUD[i] = off_hud.getIntList()[i];
				}
			} else {
				offsetHUD[0] = 0;
				offsetHUD[1] = 300;
			}
			offsetBiome = off_biome.getIntList();
			offsetSeason = off_season.getIntList();
			offsetClimate = off_climate.getIntList();

			enableRealTime = realT.getBoolean();
			// enableRealSeason = realS.getBoolean();

			if (sprPeriod.isIntList() && sprPeriod.getIntList().length == 2) {
				for (int i = 0; i < 2; i++) {
					springDate[i] = sprPeriod.getIntList()[i];
				}
				if (springDate[0] > springDate[1]) {
					overYear = EnumSeason.SPRING;
				}
			}

			if (smrPeriod.isIntList() && smrPeriod.getIntList().length == 2) {
				for (int i = 0; i < 2; i++) {
					summerDate[i] = smrPeriod.getIntList()[i];
				}
				if (summerDate[0] > summerDate[1]) {
					overYear = EnumSeason.SUMMER;
				}
			}

			if (autPeriod.isIntList() && autPeriod.getIntList().length == 2) {
				for (int i = 0; i < 2; i++) {
					autumnDate[i] = autPeriod.getIntList()[i];
				}
				if (autumnDate[0] > autumnDate[1]) {
					overYear = EnumSeason.AUTUMN;
				}
			}

			if (wtrPeriod.isIntList() && wtrPeriod.getIntList().length == 2) {
				for (int i = 0; i < 2; i++) {
					winterDate[i] = wtrPeriod.getIntList()[i];
				}
				if (winterDate[0] > winterDate[1]) {
					overYear = EnumSeason.WINTER;
				}
			}

			int sd = startD.getInt();
			if (sd < 0 || sd > yearLength) {
				sd = springDate[0];
			}
			startDate = sd;

			String df = dateF.getString();
			dateFormat = getFormat(df);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}

	public static double getSeasonTempOffset(EnumSeason season) {
		switch (season) {
		case AUTUMN:
			return seasonEffects[2];
		case SPRING:
			return seasonEffects[0];
		case SUMMER:
			return seasonEffects[1];
		case WINTER:
			return seasonEffects[3];
		default:
			return seasonEffects[0];

		}
	}

	public static void leadBlockNames() {
		blackListBlock.clear();
		blackListEntity.clear();
		blackListBlock.addAll(DCUtil.getListFromStrings(updateBlackList, "Tick Update Invalid List"));
		blackListEntity.addAll(DCUtil.getEntityListFromStrings(entityBlackList, "Climate Damage Invalid List"));
	}

	static String getFormat(String s) {
		if (s == null) {
			return "yyyy/MM/dd";
		} else {
			if (s.contains("yyyy") && s.contains("MM") && s.contains("dd")) {
				return s;
			} else {
				return "yyyy/MM/dd";
			}
		}
	}

}
