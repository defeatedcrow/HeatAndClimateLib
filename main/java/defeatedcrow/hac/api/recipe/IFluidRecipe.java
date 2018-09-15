package defeatedcrow.hac.api.recipe;

import java.util.List;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * Climateを条件に進む、液体加工を含む多対多のレシピ。<br>
 * 材料は辞書対応。<br>
 * 甕での発酵、蒸留器での蒸留の両方で扱う。
 */
public interface IFluidRecipe {

	/**
	 * Input登録内容
	 */
	Object[] getInput();

	@Nullable
	ItemStack getOutput();

	@Nullable
	ItemStack getSecondary();

	@Nullable
	FluidStack getInputFluid();

	@Nullable
	FluidStack getOutputFluid();

	/**
	 * Inputのコンテナアイテムのリスト
	 */
	List<ItemStack> getContainerItems(List<Object> items);

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
	boolean matches(List<ItemStack> items, FluidStack fluid);

	/**
	 * Output条件判定
	 */
	boolean matchOutput(List<ItemStack> target, FluidStack fluid, int slotsize);

	boolean matchClimate(int code);

	/**
	 * 冷却が必要か<br>
	 * 冷却レシピの場合、エリア内の最も低いTierを冷媒としてカウントし、<br>
	 * 冷媒がNORMAL以下であればレシピが進行する。
	 */
	boolean isNeedCooling();

	/**
	 * Climate条件判定
	 */
	boolean matchClimate(IClimate climate);

	/**
	 * 追加条件
	 * trueで条件クリア
	 */
	boolean additionalRequire(World world, BlockPos pos);

	/**
	 * 0: 設置不可, 1: Block, 2: Entity
	 */
	int hasPlaceableOutput();

	/**
	 * おもにレシピ条件表示機能用
	 * HeatTierのみ、Tier+1まで対応範囲になる
	 */
	List<DCHeatTier> requiredHeat();

	List<DCHumidity> requiredHum();

	List<DCAirflow> requiredAir();

	/**
	 * レシピ表示機能用
	 */
	String additionalString();

	/**
	 * レシピ一致度<br>
	 * レシピ検索時、より一致する材料の多いレシピを優先的に選択するためのもの
	 */
	int recipeCoincidence();

}
