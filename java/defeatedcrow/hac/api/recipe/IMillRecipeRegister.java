package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IMillRecipeRegister {

	/**
	 * Recipeのリストを得る。
	 */
	List<? extends IMillRecipe> getRecipeList();

	/**
	 * Recipe登録<br>
	 * 
	 * @param output
	 *            : 完成品
	 * @param secondary
	 *            : 副生物 null可
	 * @param secondaryChance
	 *            : 副生成物の生成率 0.0F-1.0F
	 * @param input
	 *            : 材料
	 */
	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, Object input);

	void addRecipe(ItemStack output, ItemStack secondary, Object input);

	void addRecipe(ItemStack output, Object input);

	/**
	 * MillRecipe.class以外受け付けないのでご注意を(要本体)
	 */
	void addRecipe(IMillRecipe recipe);

	/**
	 * inputs, Climateでレシピを判定
	 */
	IMillRecipe getRecipe(ItemStack items);

}
