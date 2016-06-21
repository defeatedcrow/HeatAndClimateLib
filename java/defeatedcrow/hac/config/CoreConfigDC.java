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
	public static int charmWarpKey = 0x2D;

	// render
	public static boolean showAltTips = true;

	// difficulty
	public static boolean climateDam = true;
	public static boolean peacefulDam = false;
	public static int damageDifficulty = 1; // 0-2

	// ore
	public static int[] depositGen = new int[] {
			35,
			50,
			15,
			100,
			30 };

	// recipe
	public static boolean enableVanilla = true;
	public static int updateFrequency = 5;

	public void load(Configuration cfg) {

		try {
			cfg.load();

			cfg.addCustomCategoryComment("debug setting", "It only for the authors of this mod.");
			cfg.addCustomCategoryComment("difficulty setting", "This setting is for changing difficulty of this mod.");
			cfg.addCustomCategoryComment("render setting", "This setting is for such as display and model.");
			cfg.addCustomCategoryComment("world setting", "This setting is for world gen.");
			cfg.addCustomCategoryComment("ore gen setting", "This setting is for ore gen. " + BR
					+ "Please set probability as parsentage (0 - 100)." + BR
					+ "If you set 0, those ore deposits will not be generated.");
			cfg.addCustomCategoryComment("key setting",
					"This mod is not using the Forge KeyHandler. Please setting it in here.");

			Property debug = cfg.get("debug setting", "Debug Mode Pass", debugPass,
					"Input the password for starting in debug mode. This is only for authers.");

			Property climate_dam = cfg.get("difficulty setting", "Enable Climate Damage", climateDam,
					"Enable damage from hot or cold climate.");

			Property peace_dam = cfg.get("difficulty setting", "Enable Peaceful Damage", peacefulDam,
					"Enable climate damage at peaceful setting.");

			Property diff_dam = cfg.get("difficulty setting", "Difficulty of Climate Damage", damageDifficulty,
					"Set difficulty of climate damage. 0:sweet 1:normal 2:bitter");

			Property vanilla_block = cfg.get("world setting", "Enable Vanilla Block Recipe", enableVanilla,
					"Enable climate change of vanilla blocks.");

			Property update_block = cfg.get("world setting", "Set Update Frequency", updateFrequency,
					"Set the number of the update times per sec.");

			Property alt_tips = cfg.get("render setting", "Enable Alt Tooltip", showAltTips,
					"Enable additional tooltips for harvest level, and climate registance of items with F3+H.");

			Property warp_key = cfg.get("key setting", "Charm Use Key", charmWarpKey,
					"Set key number for using jewel charm effects. Default key is X(45)." + BR
							+ "If you don't want this effect, set 0.");

			Property sed_ore = cfg.get("ore gen setting", "Sedimentary Gen Probability", depositGen[0],
					"Generate in High-altitude of mountain.");

			Property char_ore = cfg.get("ore gen setting", "Chalcopyrite Gen Probability", depositGen[1],
					"Generate in underground of mountain.");

			Property vein_ore = cfg.get("ore gen setting", "Quartz Vein Gen Probability", depositGen[2],
					"Generate in underground of plane.");

			Property lava_ore = cfg.get("ore gen setting", "Magnetite Gen Probability", depositGen[3],
					"Generate in deep-underground.");

			Property geode_ore = cfg.get("ore gen setting", "Geode Gen Probability", depositGen[4],
					"Generate in deep-underground.");

			debugPass = debug.getString();
			climateDam = climate_dam.getBoolean();
			peacefulDam = peace_dam.getBoolean();
			showAltTips = alt_tips.getBoolean();
			charmWarpKey = warp_key.getInt();
			enableVanilla = vanilla_block.getBoolean();

			int s = sed_ore.getInt();
			if (s < 0 || s > 100)
				s = 0;
			int c = char_ore.getInt();
			if (c < 0 || c > 100)
				c = 0;
			int v = vein_ore.getInt();
			if (v < 0 || v > 100)
				v = 0;
			int l = lava_ore.getInt();
			if (l < 0 || l > 100)
				l = 0;
			int g = geode_ore.getInt();
			if (g < 0 || g > 100)
				g = 0;
			int d = diff_dam.getInt();
			if (d < 0 || d > 2)
				d = 2;

			depositGen[0] = s;
			depositGen[1] = c;
			depositGen[2] = v;
			depositGen[3] = l;
			depositGen[4] = g;

			damageDifficulty = d;

			int h = update_block.getInt();
			if (h < 0 || h > 20)
				h = 0;
			updateFrequency = h;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}

}
