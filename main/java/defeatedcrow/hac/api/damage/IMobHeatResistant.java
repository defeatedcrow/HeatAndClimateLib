package defeatedcrow.hac.api.damage;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Mobの気候耐性を登録するためのインターフェイス
 */
public interface IMobHeatResistant {

	float getHeatResistant(ResourceLocation name, DCHeatTier temp);

	float getHeatResistant(Entity entity, DCHeatTier temp);

	float getHumResistant(ResourceLocation name, DCHumidity hum);

	float getHumResistant(Entity entity, DCHumidity hum);

	float getAirResistant(ResourceLocation name, DCAirflow air);

	float getAirResistant(Entity entity, DCAirflow air);

	/**
	 * @param name "modID", "registryName"
	 * @param suitableTemp suitable heat tier (default:NORMAL)
	 * @param value1 resistance value (default:2.0F)
	 * @param suitableHum suitable humidity (default:NORMAL)
	 * @param value2 resistance value (default:2.0F)
	 * @param suitableAir suitable airflow (default:NORMAL)
	 * @param value3 resistance value (default:2.0F)
	 *        <br>
	 *        ResourceLocationからクラス名を特定して登録するメソッド
	 */
	void registerEntityResistant(ResourceLocation name, DCHeatTier suitableTemp, float value1, DCHumidity suitableHum,
			float value2, DCAirflow suitableAir, float value3);

	void registerEntityResistant(ResourceLocation name, DCHeatTier suitableTemp, float value1);

	/**
	 * @param entityClass register target class
	 * @param suitableTemp suitable heat tier (default:NORMAL)
	 * @param value1 resistance value (default:2.0F)
	 * @param suitableHum suitable humidity (default:NORMAL)
	 * @param value2 resistance value (default:2.0F)
	 * @param suitableAir suitable airflow (default:NORMAL)
	 * @param value3 resistance value (default:2.0F)
	 *        <br>
	 *        特定のEntityClassに対して気候耐性を登録する。<br>
	 *        EntityListに登録されたクラスのみ登録可能で、一致(優先)、または継承関係であるかを判定する。
	 * @return
	 */
	void registerEntityResistant(Class<? extends Entity> entityClass, DCHeatTier suitableTemp, float value1,
			DCHumidity suitableHum, float value2, DCAirflow suitableAir, float value3);

	void registerEntityResistant(Class<? extends Entity> entityClass, DCHeatTier suitableTemp, float value1);

	@Deprecated
	void registerEntityResistant(ResourceLocation name, float value1, float value2);

	@Deprecated
	void registerEntityResistant(Class<? extends Entity> entityClass, float value1, float value2);

}
