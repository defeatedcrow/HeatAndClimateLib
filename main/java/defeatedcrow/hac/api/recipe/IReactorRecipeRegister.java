package defeatedcrow.hac.api.recipe;

import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IReactorRecipeRegister {

	/**
	 * Recipeのリストを得る。HeatTierごとに別リストになっている。
	 */
	List<? extends IReactorRecipe> getRecipeList();

	/**
	 * Recipe登録<br>
	 * Humidity、Airはnullでも良い。その場合、そのパラメータを問わないレシピになる。
	 *
	 * @param output
	 *        : 完成品
	 * @param secondary
	 *        : 副生物 null可
	 * @param heat
	 *        : 要求HeatTier
	 * @param secondaryChance
	 *        : 副生成物の生成率 0.0F-1.0F
	 * @param catalyst
	 *        : 触媒アイテム 消費しない
	 * @param input
	 *        : 材料
	 */
	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid1,
			FluidStack outFluid2, DCHeatTier heat, ItemStack catalyst, FluidStack inFluid1, FluidStack inFluid2,
			Object... input);

	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid1,
			FluidStack outFluid2, DCHeatTier heat, String catalystOredic, FluidStack inFluid1, FluidStack inFluid2,
			Object... input);

	/**
	 * ReactorRecipe.class以外受け付けないのでご注意を(要Lib本体)
	 */
	void addRecipe(IReactorRecipe recipe);

	@Deprecated
	void addRecipe(IReactorRecipe recipe, DCHeatTier heat);

	/**
	 * inputs, Climateでレシピを判定
	 */
	@Deprecated
	IReactorRecipe getRecipe(DCHeatTier tier, List<ItemStack> items, FluidStack inFluid1, FluidStack inFluid2);

	@Deprecated
	IReactorRecipe getRecipe(int id, List<ItemStack> items, FluidStack inFluid1, FluidStack inFluid2);

	IReactorRecipe getRecipe(DCHeatTier tier, List<ItemStack> items, FluidStack inFluid1, FluidStack inFluid2,
			ItemStack catalyst);

}
