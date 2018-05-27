package defeatedcrow.hac.api.damage;

import defeatedcrow.hac.api.climate.DCHeatTier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * ClimateDamage発生直前に発火するEvent<br>
 * キャンセルした場合はDamage処理自体がなくなる。<br>
 * 2.0F未満では瀕死時にダメージが止まる。また、1.0Fを下回ると自動的にキャンセルされる。
 */
@Cancelable
public class ClimateDamageEvent extends LivingEvent {

	private final DamageSourceClimate source;
	private float amount;
	private final DCHeatTier heatTier;

	public ClimateDamageEvent(EntityLivingBase livingIn, DamageSourceClimate sourceIn, DCHeatTier tierIn,
			float amountIn) {
		super(livingIn);
		source = sourceIn;
		amount = amountIn;
		heatTier = tierIn;
	}

	public float result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (isCanceled()) {
			return 0;
		}

		return amount;
	}

	public DamageSource getSource() {
		return source;
	}

	public float getAmount() {
		return amount;
	}

	public DCHeatTier getHeatTier() {
		return heatTier;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
}
