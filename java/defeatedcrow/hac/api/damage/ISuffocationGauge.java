package defeatedcrow.hac.api.damage;

import net.minecraft.entity.Entity;

/**
 * InWaterでの意窒息を地上でも起こすためのもの。<br>
 * 別パラメータのため、バニラ要素でしか軽減できない。
 * <br>
 * 多分実装しないけど一応作った
 */
@Deprecated
public interface ISuffocationGauge {

	int getAmount(Entity entity);

	void setAmount(Entity entity, int amo);

}
