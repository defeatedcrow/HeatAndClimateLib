package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ISpinningRecipeRegister {

	/**
	 * Recipeのリストを得る。
	 */
	List<ISpinningRecipe> getRecipeList();

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

	void addRecipe(ISpinningRecipe recipe);

	/**
	 * inputs, Climateでレシピを判定
	 */
	ISpinningRecipe getRecipe(ItemStack input);

	boolean removeRecipe(ItemStack input);

}
