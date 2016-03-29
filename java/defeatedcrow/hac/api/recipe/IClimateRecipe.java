package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;

/**
 * Climateを条件に進む、複数スロットを材料にするレシピ。<br>
 * 材料は辞書対応。
 */
public interface IClimateRecipe {

	/**
	 * Input登録内容
	 */
	Object[] getInput();

	ItemStack getOutput();

	ItemStack getSecondary();

	/**
	 * Inputのコンテナアイテムのリスト
	 */
	List<ItemStack> getContaierItems(List<ItemStack> items);

	float getSecondaryChance();

	/**
	 * macth条件判定用、鉱石辞書変換後のInputリスト
	 */
	List<Object> getProcessedInput();

	/**
	 * inputの大きさ
	 */
	int getRecipeSize();

	/**
	 * Input条件判定
	 */
	boolean matches(List<ItemStack> items);

	boolean matchClimate(int code);

	/**
	 * Climate条件判定
	 */
	boolean matchClimate(IClimate climate);

	/**
	 * おもにレシピ条件表示機能用
	 */
	List<DCHeatTier> requiredHeat();

	List<DCHumidity> requiredHum();

	List<DCAirflow> requiredAir();

}
