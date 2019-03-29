package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ISpinningRecipeRegister {

	/**
	 * Recipeのリストを得る。
	 */
	List<? extends ISpinningRecipe> getRecipeList();

	/**
	 * Recipe登録<br>
	 *
	 * @param output
	 *        : 完成品
	 * @param count
	 *        : 必要個数
	 * @param input
	 *        : 材料
	 */
	void addRecipe(ItemStack output, int count, Object input);

	void addRecipe(ItemStack output, Object input);

	/**
	 * SpinningRecipe.class以外受け付けないのでご注意を(要本体)
	 */
	void addRecipe(ISpinningRecipe recipe);

	/**
	 * inputs, Climateでレシピを判定
	 */
	ISpinningRecipe getRecipe(ItemStack input);

	boolean removeRecipe(ItemStack input);

}
