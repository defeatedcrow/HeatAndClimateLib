package defeatedcrow.hac.api.magic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

public interface IJewelCharm extends IJewel {

	// Defense charm
	/**
	 * DIFFENCE<br>
	 * プレイヤーがダメージを受けたときに呼ばれ、軽減量を返す
	 */
	float reduceDamage(DamageSource source, ItemStack charm);

	/**
	 * DIFFENCE<br>
	 * プレイヤーがダメージを受けたときに呼ばれ、被弾時のアクションを起こす。<br>
	 * trueの場合、チャームアイテムのダメージ処理を呼ぶ(消費、耐久値減少など)
	 */
	boolean onDiffence(DamageSource source, EntityLivingBase target, float damage, ItemStack charm);

	// Attack charm
	/**
	 * ATTACK<br>
	 * プレイヤーがダメージを与えたときに呼ばれ、ダメージ増加倍率を返す。
	 */
	float increaceDamage(EntityLivingBase target, ItemStack charm);

	/**
	 * ATTACK<br>
	 * プレイヤーがダメージを与えたときに呼ばれ、アクションを起こす。<br>
	 * trueの場合、チャームアイテムのダメージ処理を呼ぶ(消費、耐久値減少など)
	 */
	boolean onAttacking(EntityPlayer player, EntityLivingBase target, DamageSource source, float damage, ItemStack charm);

	// Tool charm
	/**
	 * TOOL<br>
	 * プレイヤーがブロックを破壊した時に呼ばれる。<br>
	 * trueの場合、チャームアイテムのダメージ処理を呼ぶ(消費、耐久値減少など)
	 */
	boolean onToolUsing(EntityPlayer player, BlockPos pos, IBlockState state, ItemStack charm);

	// Constant charm
	/**
	 * CONSTANT<br>
	 * プレイヤーのTick更新ごとに呼ばれる常時効果<br>
	 */
	void constantEffect(EntityPlayer player, ItemStack charm);

	// X key using
	/**
	 * KEY<br>
	 * プレイヤーがコンフィグで設定したUseキーを押すと呼ばれ、アクションを起こす。<br>
	 * 
	 * @return
	 */
	boolean onUsing(EntityPlayer player, ItemStack charm);

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
