package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 任意範囲のHeat、Air、Humの計算。<br>
 * あまり広範囲だと重いと思うので、getClimateのrangeは0~16の制限がかかる。<br>
 * また、現時点ではIHeatSourceはカウントせず、自然環境要素の確認のみである。<br>
 * IHeatSourceについては後ほど実装する。
 */
public interface IClimateCalculator {

	/**
	 * 範囲内のClimateを求める。<br>
	 * 範囲指定が不要(configで指定したレンジで計算する)のメソッド。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	IClimate getClimate(World world, BlockPos pos);

	/**
	 * 範囲内のHeatTierを求める。<br>
	 * 範囲指定が不要(configで指定したレンジで計算する)のメソッド。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHeatTier getAverageTemp(World world, BlockPos pos);

	/**
	 * 範囲内のHeatTierを求める。<br>
	 * 範囲指定が不要(configで指定したレンジで計算する)のメソッド。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHumidity getHumidity(World world, BlockPos pos);

	/**
	 * 範囲内のHeatTierを求める。<br>
	 * 範囲指定が不要(configで指定したレンジで計算する)のメソッド。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCAirflow getAirflow(World world, BlockPos pos);

	/**
	 * 範囲内のClimateを求める。<br>
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	IClimate getClimate(World world, BlockPos pos, int[] range);

	/**
	 * 範囲内の最も高いHeatTierを求める。<br>
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHeatTier getHeat(World world, BlockPos pos, int range, boolean horizontal);

	/**
	 * 範囲内の最も低いHeatTierを求める。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHeatTier getCold(World world, BlockPos pos, int range, boolean horizontal);

	/**
	 * 範囲内のHeatTier(Heat・Coldの合成後の値)を求める。
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHeatTier getAverageTemp(World world, BlockPos pos, int range, boolean horizontal);

	/**
	 * 範囲内の湿りを計算する。<br>
	 * WET Biome: +1<br>
	 * Material.water +1<br>
	 * Raining +1<br>
	 * IHumidityTile -1 ~ +1<br>
	 * ~ -1: DRY<br>
	 * 0: NORMAL<br>
	 * +1 ~: WET
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCHumidity getHumidity(World world, BlockPos pos, int range, boolean horizontal);

	/**
	 * 範囲内の通気の有無を求める。<br>
	 * AirBlockが2個以下: TIGHT<br>
	 * AirBlockが3個以上: 屋内ならNORMAL、屋外はBiome標準<br>
	 * IAirflowTile(WIND)あり: WIND<br>
	 * 雷雨時の屋外: WIND
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 * @param range
	 *        : 走査半径
	 * @param horizontal
	 *        : posと同高度の平面範囲を調べる
	 */
	DCAirflow getAirflow(World world, BlockPos pos, int range, boolean horizontal);

	/**
	 * 対象のブロックに登録された温度を得る。<br>
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 */
	DCHeatTier getBlockHeatTier(World world, BlockPos target, BlockPos source);

	/**
	 * 対象のブロックに登録された湿度を得る。<br>
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 */
	DCHumidity getBlockHumidity(World world, BlockPos target, BlockPos source);

	/**
	 * 対象のブロックに登録された通気を得る。<br>
	 *
	 * @param world
	 *        : 対象のWorld
	 * @param pos
	 *        : 対象のBlockPos
	 */
	DCAirflow getBlockAirflow(World world, BlockPos target, BlockPos source);

}
