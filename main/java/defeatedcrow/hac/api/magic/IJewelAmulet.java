package defeatedcrow.hac.api.magic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * アミュレット<br>
 * プレイヤー以外のMobのインベントリにあると効果を発揮する、宝石アクセサリの一種です。<br>
 * インベントリのどこにおいても効果があります。
 */
@Deprecated
public interface IJewelAmulet {

	/**
	 * DIFFENCE<br>
	 * ダメージを受けたときに呼ばれ、軽減量を返す
	 */
	float reduceDamage(DamageSource source, ItemStack charm);

	/**
	 * DIFFENCE<br>
	 * ダメージを受けたときに呼ばれ、被弾時のアクションを起こす。<br>
	 * trueの場合、チャームアイテムのダメージ処理を呼ぶ(消費、耐久値減少など)
	 */
	boolean onDiffence(DamageSource source, EntityLivingBase target, float damage, ItemStack charm);

	/**
	 * ATTACK<br>
	 * ダメージを与えたときに呼ばれ、ダメージ増加倍率を返す。
	 */
	float increaceDamage(EntityLivingBase target, ItemStack charm);

	/**
	 * ATTACK<br>
	 * ダメージを与えたときに呼ばれ、アクションを起こす。<br>
	 * trueの場合、チャームアイテムのダメージ処理を呼ぶ(消費、耐久値減少など)
	 */
	boolean onAttacking(EntityLivingBase owner, EntityLivingBase target, DamageSource source, float damage,
			ItemStack charm);

	// Constant charm
	/**
	 * CONSTANT<br>
	 * Tick更新ごとに呼ばれる常時効果<br>
	 */
	void constantEffect(EntityLivingBase owner, ItemStack charm);

	// active check
	/**
	 * チャームが使用可能かどうか。<br>
	 */
	boolean isActive(ItemStack charm);

	/**
	 * チャームが使用可能かを切り替える。<br>
	 */
	void setActive(ItemStack charm, boolean flag);

	// damage or consume
	/**
	 * 効果使用後のダメージor消費処理<br>
	 */
	ItemStack consumeCharmItem(ItemStack stack);

}
