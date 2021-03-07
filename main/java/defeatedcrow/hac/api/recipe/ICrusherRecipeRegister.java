package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICrusherRecipeRegister {

	/**
	 * Recipeのリストを得る。
	 */
	List<ICrusherRecipe> getRecipeList();

	/**
	 * Recipe登録<br>
	 *
	 * @param output
	 *        : 完成品
	 * @param secondary
	 *        : 副生物 null可
	 * @param tertialyChance
	 *        : 副生成物の生成率 0.0F-1.0F
	 * @param secondary
	 *        : 副生物2 null可
	 * @param tertialyChance
	 *        : 副生成物2の生成率 0.0F-1.0F
	 * @param outputFluid
	 *        : 生成液体
	 * @param catalyst
	 *        : 触媒アイテム 消費しない
	 * @param input
	 *        : 材料
	 */
	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack tertialy,
			float tertialyChance, FluidStack outputFluid, ItemStack catalyst, Object input);

	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack tertialy,
			float tertialyChance, ItemStack catalyst, Object input);

	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outputFluid,
			ItemStack catalyst, Object input);

	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, ItemStack catalyst, Object input);

	void addRecipe(ItemStack output, ItemStack catalyst, Object input);

	void addRecipe(ICrusherRecipe recipe);

	/**
	 * inputs, Climateでレシピを判定
	 */
	ICrusherRecipe getRecipe(ItemStack input, ItemStack catalyst);

	@Deprecated
	ICrusherRecipe getRecipe(ItemStack input);

	boolean removeRecipe(ItemStack input, ItemStack catalyst);

}
