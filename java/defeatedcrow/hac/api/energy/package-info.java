/**
 * Copyright (c) defeatedcrow, 2016
 * URL:http://defeatedcrow.jp/modwiki/Mainpage
 * Please check the License.txt included in the package file of this Mod.
 */

/**
 * このAPIは、このmodで扱われるエネルギーに関するAPIです｡<br>
 * 現在は他のmodのエネルギーとの互換性はありません。<br>
 * 基本はトルク(Torque)の形でエネルギーを伝えます。必要に応じて回転力やピストン動力に変換されます。<br>
 * インターフェイスでは加速度(acceleration)にしか干渉できません。スピードは、加速度に応じて常に変動します。
 */
@API(apiVersion = "2.0.1", owner = "dcs_lib", provides = "DCsHaCAPI|energy")
package defeatedcrow.hac.api.energy;

import net.minecraftforge.fml.common.API;
