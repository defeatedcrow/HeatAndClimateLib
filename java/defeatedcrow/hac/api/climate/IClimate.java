package defeatedcrow.hac.api.climate;

/**
 * Biome用の登録情報。
 * 利用側で、現在Biomeの固有情報をチェックするのに使う。
 */
public interface IClimate {

	DCHeatTier getHeat();

	DCHumidity getHumidity();

	DCAirflow getAirflow();

	int getClimateInt();

}
