package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;

public interface IFluidRecipeRegister {

	/**
	 * Recipeのリストを得る。HeatTierごとに別リストになっている。
	 */
	List<? extends IFluidRecipe> getRecipeList(DCHeatTier tier);

	/**
	 * Recipe登録<br>
	 * Humidity、Airはnullでも良い。その場合、そのパラメータを問わないレシピになる。
	 * 
	 * @param output
	 *            : 完成品
	 * @param secondary
	 *            : 副生物 null可
	 * @param heat
	 *            : 要求HeatTier
	 * @param hum
	 *            : 要求Humidity null可
	 * @param air
	 *            : 要求Airflow null可
	 * @param secondaryChance
	 *            : 副生成物の生成率 0.0F-1.0F
	 * @param input
	 *            : 材料
	 */
	void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, FluidStack outFluid, DCHeatTier heat,
			DCHumidity hum, DCAirflow air, boolean needCooling, FluidStack inFluid, Object... input);

	/**
	 * FluidRecipe.class以外受け付けないのでご注意を(要本体)
	 */
	void addRecipe(IFluidRecipe recipe, DCHeatTier heat);

	/**
	 * inputs, Climateでレシピを判定
	 */
	IFluidRecipe getRecipe(IClimate clm, List<ItemStack> items, FluidStack inFluid);

	IFluidRecipe getRecipe(int code, List<ItemStack> items, FluidStack inFluid);

}
