package defeatedcrow.hac.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * トルクを扱うTileEntityに実装するもの
 */
public abstract interface ITorqueDC {

	/* 向きの制御 */

	// Tileの基底部分の向き。設置後に変更できない。
	EnumFacing getBaseSide();

	// Tileの上部の向き。設置後に回せる。
	EnumFacing getFaceSide();

	// 動力を受け入れられるか
	boolean isInputSide(EnumFacing side);

	// 動力を出力できるか
	boolean isOutputSide(EnumFacing side);

	// 設置時の向き設定
	void setBaseSide(EnumFacing side);

	// 上部の向きを持つかどうか
	boolean hasFaceSide();

	// 上部の向き設定
	void setFaceSide(EnumFacing side);

	// 上部だけを回したいときに
	void rotateFace();

	/* 速度の管理 */

	// 有効な角加速度。実際に速度に影響する。
	float getEffectiveAcceleration();

	// 与えられている角加速度
	float getCurrentAcceleration();

	// 摩擦力
	float getFrictionalForce();

	// ギアサイズ。トルクに影響する。
	float getGearTier();

	// トルク
	float getCurrentTorque();

	// 回転速度
	float getRotationalSpeed();

	/*
	 * エネルギーは、"トルク"の形でやりとりされる｡
	 * よって、ブロック間で送受信されるデータはトルクの大きさと向きのみであり、スピードは基本的に装置内のみで利用される。
	 * 次の装置にエネルギーを伝える場合はEffectiveAccelをTorqueに変換して伝える。
	 * よって、次の装置に伝わる際には遅延が生じる。
	 */

	/*
	 * トルクは受け取り用のfloatに一時的にプールされるが、Tick処理時にすべて消費されて0になる。
	 * トルクをタンクのように溜めたり、長時間保持することは出来ない。
	 */

	/*
	 * 前の装置のトルク -> GearTier * Accel
	 * EffectiveAccel <- Accel - prevAccel
	 * 次の装置へのトルク <- prevAccel * GearTier (負の場合は0になる)
	 * トルクは装置がレシピなどに消費した場合、その分だけ減少して次の装置に伝わる。
	 * ただしほとんどの仕事装置はインプットのみで次の装置には発展しないが…
	 */

	/*
	 * 回転速度は角加速度によって増減する。
	 * Speed <- (prevSpeed * Frict) + Accel
	 */

}
