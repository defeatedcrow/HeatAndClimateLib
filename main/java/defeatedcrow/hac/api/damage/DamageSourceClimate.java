package defeatedcrow.hac.api.damage;

import net.minecraft.util.DamageSource;

/**
 * 気候によるダメージのDamageSource<br>
 * 専用死亡メッセージあり
 */
public class DamageSourceClimate extends DamageSource {

	public static DamageSourceClimate climateHeatDamage = new DamageSourceClimate("dcs_heat").setHeatDamage();
	public static DamageSourceClimate climateColdDamage = new DamageSourceClimate("dcs_cold");
	public static DamageSource machineDamage = new DamageSource("dcs_machine");

	public DamageSourceClimate(String damageTypeIn) {
		super(damageTypeIn);
		this.setDamageBypassesArmor();
	}

	public boolean isHeat;

	private DamageSourceClimate setHeatDamage() {
		this.isHeat = true;
		return this;
	}

	public boolean isHeatDamage() {
		return isHeat;
	}

}
