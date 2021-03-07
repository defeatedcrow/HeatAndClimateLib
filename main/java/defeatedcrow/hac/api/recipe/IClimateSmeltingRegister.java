package defeatedcrow.hac.api.recipe;

import java.util.List;
import java.util.function.Supplier;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import net.minecraft.item.ItemStack;

public interface IClimateSmeltingRegister {

	/**
	 * Recipeのリストを得る。HeatTierごとに別リストになっている。
	 */
	List<IClimateSmelting> getRecipeList();

	/**
	 * Recipe登録<br>
	 *
	 * @param output
	 *        : 完成品
	 * @param heat
	 *        : 要求HeatTier
	 * @param hum
	 *        : 要求Humidity
	 * @param air
	 *        : 要求Airflow
	 * @param input
	 *        : 材料
	 */
	void addRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air, Object input);

	void addRecipe(ItemStack output, List<DCHeatTier> heat, DCHumidity hum, DCAirflow air, Object input);

	void addRecipe(ItemStack output, DCHeatTier heat, Object input);

	void addRecipe(IClimateSmelting recipe);

	void addRecipe(ItemStack output, DCHeatTier heat, DCHumidity hum, DCAirflow air, boolean needCooling, Object input);

	/** 後方互換対策 */
	@Deprecated
	void addRecipe(ItemStack output, ItemStack secondary, DCHeatTier heat, DCHumidity hum, DCAirflow air,
			float secondaryChance, boolean needCooling, Object input);

	@Deprecated
	void addRecipe(ItemStack output, ItemStack secondary, IClimate climate, float secondaryChance, Object input);

	@Deprecated
	void addRecipe(IClimateSmelting recipe, DCHeatTier heat);

	/**
	 * input, Climateでレシピを判定
	 */
	IClimateSmelting getRecipe(Supplier<IClimate> clm, ItemStack input);

	IClimateSmelting getRecipe(IClimate clm, ItemStack input);

	IClimateSmelting getRecipe(int code, ItemStack input);

	boolean removeRecipe(IClimate clm, ItemStack input);

}
