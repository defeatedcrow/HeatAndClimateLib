package defeatedcrow.hac.core.util;

import net.minecraft.potion.Potion;

// vanilla potion instance getter
public class DCPotion {

	private DCPotion() {
	};

	public static Potion speed;
	public static Potion slownwss;
	public static Potion haste;
	public static Potion slowmine;
	public static Potion strength;
	public static Potion weekness;
	public static Potion heal;
	public static Potion damage;
	public static Potion jump;
	public static Potion nausea;
	public static Potion regeneration;
	public static Potion poison;
	public static Potion wither;
	public static Potion night_vision;
	public static Potion blindness;
	public static Potion hunger;
	public static Potion saturation;
	public static Potion registance;
	public static Potion fire_reg;
	public static Potion water_breath;
	public static Potion invisible;
	public static Potion health_boost;
	public static Potion absorption;
	public static Potion glowing;
	public static Potion levitation;
	public static Potion luck;
	public static Potion unluck;

	public static void init() {
		speed = getVanillaPotion("speed");
		slownwss = getVanillaPotion("slowness");
		haste = getVanillaPotion("haste");
		slowmine = getVanillaPotion("mining_fatigue");
		strength = getVanillaPotion("strength");
		weekness = getVanillaPotion("weakness");
		heal = getVanillaPotion("instant_health");
		damage = getVanillaPotion("instant_damage");
		jump = getVanillaPotion("jump_boost");
		nausea = getVanillaPotion("nausea");
		regeneration = getVanillaPotion("regeneration");
		poison = getVanillaPotion("poison");
		wither = getVanillaPotion("wither");
		night_vision = getVanillaPotion("night_vision");
		blindness = getVanillaPotion("blindness");
		hunger = getVanillaPotion("hunger");
		saturation = getVanillaPotion("saturation");
		registance = getVanillaPotion("resistance");
		fire_reg = getVanillaPotion("fire_resistance");
		water_breath = getVanillaPotion("water_breathing");
		invisible = getVanillaPotion("invisibility");
		health_boost = getVanillaPotion("health_boost");
		absorption = getVanillaPotion("absorption");
		glowing = getVanillaPotion("glowing");
		levitation = getVanillaPotion("levitation");
		luck = getVanillaPotion("luck");
		unluck = getVanillaPotion("unluck");
	}

	private static Potion getVanillaPotion(String s) {
		if (s == null)
			return null;
		return Potion.getPotionFromResourceLocation(s);
	}

}
