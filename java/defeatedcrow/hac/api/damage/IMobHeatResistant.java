package defeatedcrow.hac.api.damage;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Mobの気候耐性を登録するためのインターフェイス
 */
public interface IMobHeatResistant {

	/**
	 * @param name "modID", "registryName"
	 */
	float getHeatResistant(ResourceLocation name);

	/**
	 * @param name "modID", "registryName"
	 */
	float getColdResistant(ResourceLocation name);

	/**
	 * @param name "modID", "registryName"
	 */
	float getHeatResistant(Entity entity);

	/**
	 * @param name "modID", "registryName"
	 */
	float getColdResistant(Entity entity);

	/**
	 * @param name "modID", "registryName"
	 * @param heat heat resistant (default:0.0F)
	 * @param cold cold resistant (default:0.0F)
	 *        <br>
	 * 		ResourceLocationからクラス名を特定して登録するメソッド
	 */
	void registerEntityResistant(ResourceLocation name, float heat, float cold);

	/**
	 * @param entityClass register target class
	 * @param heat heat resistant (default:0.0F)
	 * @param cold cold resistant (default:0.0F)
	 *        <br>
	 *        特定のEntityClassに対して熱・冷耐性を登録する。<br>
	 *        EntityListに登録されたクラスのみ登録可能で、一致(優先)、または継承関係であるかを判定する。
	 */
	void registerEntityResistant(Class<? extends Entity> entityClass, float heat, float cold);

}
