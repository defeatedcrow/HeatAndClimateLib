package defeatedcrow.hac.api.recipe;

import java.util.List;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.climate.DCHeatTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * 電解反応槽による多対多のレシピ。<br>
 * 材料は辞書対応。<br>
 * 実験的に、液体にも辞書を適用。
 */
public interface IReactorRecipe {

	/**
	 * Input登録内容
	 */
	Object[] getInput();

	@Nullable
	ItemStack getSecondary();

	@Nullable
	ItemStack getOutput();

	@Nullable
	List<ItemStack> getCatalyst();

	@Nullable
	FluidStack getInputFluid();

	@Nullable
	FluidStack getSubInputFluid();

	@Nullable
	FluidStack getOutputFluid();

	@Nullable
	FluidStack getSubOutputFluid();

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
	boolean matches(List<ItemStack> items, FluidStack fluid1, FluidStack fluid2);

	/**
	 * Output条件判定
	 */
	boolean matchOutput(List<ItemStack> target, FluidStack fluid1, FluidStack fluid2, int slotsize);

	/**
	 * 触媒条件判定
	 */
	boolean matchCatalyst(ItemStack catalyst);

	boolean matchHeatTier(int id);

	/**
	 * Climate条件判定
	 */
	boolean matchHeatTier(DCHeatTier tier);

	/**
	 * 追加条件
	 * trueで条件クリア
	 */
	boolean additionalRequire(World world, BlockPos pos);

	/**
	 * おもにレシピ条件表示機能用
	 * HeatTierのみ、Tier+1まで対応範囲になる
	 */
	List<DCHeatTier> requiredHeat();

	/**
	 * レシピ表示機能用
	 */
	String additionalString();

	/**
	 * レシピ一致度<br>
	 * レシピ検索時、より一致する材料の多いレシピを優先的に選択するためのもの
	 */
	int recipeCoincidence();

	/**
	 * シンプルレシピ<br>
	 * 簡易反応槽で扱えるサイズであるかどうか
	 */
	boolean isSimpleRecipe();

}
