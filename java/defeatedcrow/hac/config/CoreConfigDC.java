package defeatedcrow.hac.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CoreConfigDC {

	private CoreConfigDC() {
	}

	public static final CoreConfigDC INSTANCE = new CoreConfigDC();

	public static String debugPass = "Input the password here";
	private final String BR = System.getProperty("line.separator");

	// key
	public static int charmWarpKey = 0x2D; // X
	public static int sitKey = 0x0F; // Tab

	// climate checking range
	public static int heatRange = 2;
	public static int humRange = 1;
	public static int airRange = 1;

	public static int[] ranges = new int[3];

	// render
	public static boolean showAltTips = true;
	public static boolean showDamageIcon = true;
	public static int iconX = 0;
	public static int iconY = 0;

	// difficulty
	public static boolean climateDam = true;
	public static boolean peacefulDam = false;
	public static int damageDifficulty = 1; // 0-2
	public static boolean burntFood = false;

	// recipe
	public static boolean enableVanilla = true;
	public static int updateFrequency = 5;

	// world
	public static boolean enableFreezeDrop = true;
	public static boolean enableDeepWater = true;

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

			Property freeze_drop = cfg.get("world setting", "Enable Freeze EntityItem", enableFreezeDrop,
					"EntityItems avoids to despawn in cold temp than the FROSTBITE tier.");

			Property alt_tips = cfg.get("render setting", "Enable Alt Tooltip", showAltTips,
					"Enable additional tooltips for harvest level, and climate registance of items with F3+H.");

			Property hud_icon = cfg.get("render setting", "Enable Thermal Damage Icon on HUD", showDamageIcon,
					"Enable the heart-shaped icon on HUD for display of thermal damage.");

			Property hud_x = cfg.get("render setting", "Thermal Damage Icon Offset X", iconX,
					"Set the amount of Xoffset of the thermal damage icon.");

			Property hud_y = cfg.get("render setting", "Thermal Damage Icon Offset Y", iconY,
					"Set the amount of Yoffset of the thermal damage icon.");

			Property warp_key = cfg.get("key setting", "Charm Use Key", charmWarpKey,
					"Set key number for using jewel charm effects. Default key is X(45)." + BR
							+ "If you don't want this effect, set 0.");

			Property sit_key = cfg.get("key setting", "Sit Cushion Key", sitKey,
					"Set key number for sitting on cushion. Default key is TAB(15)." + BR
							+ "If you don't want this effect, set 0.");

			Property range_t = cfg.get("setting", "HeatTier Cheking Rnage", heatRange,
					"Set the range of cheking the climate. 1-16");

			Property range_h = cfg.get("setting", "Humidity Cheking Rnage", humRange,
					"Set the range of cheking the climate. 1-16");

			Property range_a = cfg.get("setting", "Airflow Cheking Rnage", airRange,
					"Set the range of cheking the climate. 1-16");

			debugPass = debug.getString();
			climateDam = climate_dam.getBoolean();
			peacefulDam = peace_dam.getBoolean();
			showAltTips = alt_tips.getBoolean();
			charmWarpKey = warp_key.getInt();
			enableVanilla = vanilla_block.getBoolean();
			burntFood = burnt_food.getBoolean();
			showDamageIcon = hud_icon.getBoolean();
			enableDeepWater = water_cave.getBoolean();
			enableFreezeDrop = freeze_drop.getBoolean();

			int d = diff_dam.getInt();
			if (d < 0 || d > 2)
				d = 2;
			damageDifficulty = d;

			int h = update_block.getInt();
			if (h < 0 || h > 20)
				h = 20;
			updateFrequency = h;

			iconX = hud_x.getInt();
			iconY = hud_y.getInt();

			charmWarpKey = warp_key.getInt();
			sitKey = sit_key.getInt();

			int tr = range_t.getInt();
			if (tr < 0 || tr > 16)
				tr = 16;
			heatRange = tr;
			int th = range_t.getInt();
			if (th < 0 || th > 16)
				th = 16;
			humRange = th;
			int ta = range_t.getInt();
			if (ta < 0 || ta > 16)
				ta = 16;
			airRange = ta;

			ranges[0] = tr;
			ranges[1] = th;
			ranges[2] = ta;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}

}
