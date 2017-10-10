package defeatedcrow.hac.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CoreConfigDC {

	private CoreConfigDC() {}

	public static final CoreConfigDC INSTANCE = new CoreConfigDC();

	public static String debugPass = "Input the password here";
	private final String BR = System.getProperty("line.separator");

	// key
	public static int charmWarpKey = 0x2D; // X
	public static int sitKey = 0x0F; // Tab

	public static int altJumpKey = -1;
	public static int altSneakKey = -1;

	public static int[] ranges = new int[] {
			2, 1, 1
	};

	public static boolean wall = true;

	// render
	public static boolean showAltTips = true;
	public static boolean showDamageIcon = true;
	public static int iconX = 0;
	public static int iconY = 0;
	public static boolean waterFix = true;
	public static boolean hudEffect = true;

	// difficulty
	public static boolean climateDam = true;
	public static boolean peacefulDam = false;
	public static int damageDifficulty = 1; // 0-2
	public static boolean burntFood = false;

	// recipe
	public static boolean enableVanilla = true;
	public static int updateFrequency = 5;
	public static boolean disableCustomRecipe = false;

	// world
	public static boolean enableFreezeDrop = true;
	public static boolean enableDeepWater = true;
	public static boolean enableUnderLake = true;
	public static boolean customizedSpawn = false;
	public static boolean enableForestLake = true;
	public static int droughtFrequency = 60;
	public static boolean enableSeasonEffect = true;
	public static boolean enableWeatherEffect = true;

	// hardmode
	public static boolean harderVanilla = false;
	public static boolean infernalInferno = false;
	public static boolean enableSuffocation = false;
	public static boolean tightUnderworld = false;

	public void load(Configuration cfg) {

		try {
			cfg.load();

			cfg.addCustomCategoryComment("debug setting", "It only for the authors of this mod.");
			cfg.addCustomCategoryComment("difficulty setting", "This setting is for changing difficulty of this mod.");
			cfg.addCustomCategoryComment("render setting", "This setting is for such as display and model.");
			cfg.addCustomCategoryComment("world setting", "This setting is for world gen.");
			cfg.addCustomCategoryComment("key setting",
					"This mod is not using the Forge KeyHandler. Please setting it in here.");
			cfg.addCustomCategoryComment("entity setting", "This setting is for entities.");
			cfg.addCustomCategoryComment("setting", "This setting is for game play.");
			cfg.addCustomCategoryComment("hardmode setting", "This may destroy your game play. Be careful!");

			Property debug = cfg.get("debug setting", "Debug Mode Pass", debugPass,
					"Input the password for starting in debug mode. This is only for authers.");

			Property climate_dam = cfg.get("difficulty setting", "Enable Climate Damage", climateDam,
					"Enable damage from hot or cold climate.");

			Property peace_dam = cfg.get("difficulty setting", "Enable Peaceful Damage", peacefulDam,
					"Enable climate damage at peaceful setting.");

			Property diff_dam = cfg.get("difficulty setting", "Difficulty of Climate Damage", damageDifficulty,
					"Set difficulty of climate damage. 0:sweet 1:normal 2:bitter");

			Property burnt_food = cfg.get("difficulty setting", "Enable Burnt Food", burntFood,
					"Enable burnt food by high tier heat.");

			Property vanilla_block = cfg.get("world setting", "Enable Vanilla Block Recipe", enableVanilla,
					"Enable climate change of vanilla blocks.");

			Property update_block = cfg.get("world setting", "Set Update Frequency", updateFrequency,
					"Set the number of the update times per sec.");

			Property water_cave = cfg.get("world setting", "Enable Water Caves", enableDeepWater,
					"Enable generating water blocks instead of lava blocks in the deep caves.");

			Property under_lake = cfg.get("world setting", "Enable Modificated Lake", enableUnderLake,
					"Enable modification the underground lakes.");

			Property forest_lake = cfg.get("world setting", "Disable Forest Lava Lake", enableForestLake,
					"Disable generation the lava lakes on ground of forest biomes.");

			Property freeze_drop = cfg.get("world setting", "Enable Freeze EntityItem", enableFreezeDrop,
					"EntityItems avoids to despawn in cold temp than the FROSTBITE tier.");

			Property alt_tips = cfg.get("render setting", "Enable Alt Tooltip", showAltTips,
					"Enable additional tooltips for harvest level, and climate registance of items with F3+H.");

			Property hud_icon = cfg.get("render setting", "Enable Thermal Damage Icon on HUD", showDamageIcon,
					"Enable the heart-shaped icon on HUD for display of thermal damage.");

			Property hud_effect = cfg.get("render setting", "Enable Display Effect of Climate", hudEffect,
					"Enable the display effect of high or low temperature.");

			Property hud_x = cfg.get("render setting", "Thermal Damage Icon Offset X", iconX,
					"Set the amount of Xoffset of the thermal damage icon.");

			Property hud_y = cfg.get("render setting", "Thermal Damage Icon Offset Y", iconY,
					"Set the amount of Yoffset of the thermal damage icon.");

			Property water = cfg.get("render setting", "Enable Water Fix", waterFix,
					"Enable fix the vanilla light-opacity and fog density in water");

			Property warp_key = cfg.get("key setting", "Charm Use Key", charmWarpKey,
					"Set key number for using jewel charm effects. Default key is X(45)." + BR
							+ "If you don't want this effect, set 0.");

			Property sit_key = cfg.get("key setting", "Sit Cushion Key", sitKey,
					"Set key number for sitting on cushion. Default key is TAB(15)." + BR
							+ "If you don't want this effect, set 0.");

			Property jump_key = cfg.get("key setting", "Jump Key", altJumpKey,
					"Set key number for jumping. Default key is same as the vanilla setting." + BR
							+ "If you want to use the default setting, set -1.");

			Property sneak_key = cfg.get("key setting", "Sneak Key", altSneakKey,
					"Set key number for sneaking. Default key is same as the vanilla setting." + BR
							+ "If you want to use the default setting, set -1.");

			Property enableWall = cfg.get("setting", "Thermal Insulation Wall", wall,
					"Some of stone blocks enable to have a thermal insulation property.");

			Property disableCustom = cfg.get("setting", "Disable Recipe Customize", disableCustomRecipe,
					"Disable replacing the recipe with the ore dictionary.");

			Property vanilla_harder = cfg.get("hardmode setting", "Enable Harder Vanilla Block Recipe", harderVanilla,
					"Enable harder climate recipe of vanilla blocks.");

			Property inferno = cfg.get("hardmode setting", "Infernal Nether world", infernalInferno,
					"Set the temperature of Nether to maximum.");

			Property suffocation = cfg.get("hardmode setting", "Enable Suffocation Damage", enableSuffocation,
					"Enable the suffocation effect when creatures or players in tight space.");

			Property spawn = cfg.get("hardmode setting", "Customized Enemy Spawn Rate", customizedSpawn,
					"Enemy increases at low altitude and decreases at high altitude.");

			Property drought = cfg.get("world setting", "Drought Frequency", droughtFrequency,
					"Set the number of days of fine weather required for drought.");

			Property tight = cfg.get("hardmode setting", "Anaerobic Underworld", tightUnderworld,
					"Set the indoor underground (<Y30) airflow to tight.");

			Property weather = cfg.get("world setting", "Enable Weather Effect", enableWeatherEffect,
					"Enable temperature change due to the weather.");

			Property season = cfg.get("world setting", "Enable Season Effect", enableSeasonEffect,
					"Enable temperature change due to the season.");

			debugPass = debug.getString();
			climateDam = climate_dam.getBoolean();
			peacefulDam = peace_dam.getBoolean();
			showAltTips = alt_tips.getBoolean();
			charmWarpKey = warp_key.getInt();
			enableVanilla = vanilla_block.getBoolean();
			burntFood = burnt_food.getBoolean();
			showDamageIcon = hud_icon.getBoolean();
			enableDeepWater = water_cave.getBoolean();
			enableUnderLake = under_lake.getBoolean();
			enableForestLake = forest_lake.getBoolean();
			enableFreezeDrop = freeze_drop.getBoolean();
			waterFix = water.getBoolean();
			wall = enableWall.getBoolean();
			disableCustomRecipe = disableCustom.getBoolean();
			harderVanilla = vanilla_harder.getBoolean();
			infernalInferno = inferno.getBoolean();
			enableSuffocation = suffocation.getBoolean();
			customizedSpawn = spawn.getBoolean();
			tightUnderworld = tight.getBoolean();
			enableWeatherEffect = weather.getBoolean();
			enableSeasonEffect = season.getBoolean();
			hudEffect = hud_effect.getBoolean();

			int d = diff_dam.getInt();
			if (d < 0 || d > 2)
				d = 2;
			damageDifficulty = d;

			int h = update_block.getInt();
			if (h < 0 || h > 20)
				h = 20;
			updateFrequency = h;

			int dr = drought.getInt();
			if (dr < 2 || dr > 1000)
				dr = 120;
			droughtFrequency = dr;

			iconX = hud_x.getInt();
			iconY = hud_y.getInt();

			charmWarpKey = warp_key.getInt();
			sitKey = sit_key.getInt();

			altJumpKey = jump_key.getInt();
			altSneakKey = sneak_key.getInt();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}

}
