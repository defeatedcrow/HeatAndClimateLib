package defeatedcrow.hac.api.module;

public enum HaCModule {

	DISABLED("disabled", false, false),
	CORE("core", false, true),
	MACHINE("machine", false, true),
	MACHINE_ADVANCED("machine_advanced", true, true),
	MAGIC("magic", false, true),
	MAGIC_ADVANCED("magic_advanced", true, true),
	FOOD("food", false, true),
	FOOD_ADVANCED("food_advanced", true, true),
	TOOL("tool", false, true),
	WEAPON_ADVANCED("weapon_advanced", true, true),
	CLOTH_ADVANCED("cloth_advanced", true, true),
	BUILD_ADVANCED("build_advanced", true, true),
	PLUGIN("plugin", false, false);

	public final String id;
	public final boolean isAdvanced;
	public boolean enabled;

	public String modid = "dcs_climate"; // plugin only

	private HaCModule(String s, boolean adv, boolean e) {
		id = s;
		isAdvanced = adv;
	}

	public boolean enabled() {
		return enabled;
	}

	public static HaCModule getModule(String s) {
		HaCModule ret = CORE;
		for (HaCModule m : HaCModule.values()) {
			if (s != null && m.id.equalsIgnoreCase(s)) {
				ret = m;
				break;
			}
		}
		return ret;
	}

	public static HaCModule getPlugin(String s) {
		HaCModule ret = PLUGIN;
		ret.modid = s;
		return ret;
	}

}
