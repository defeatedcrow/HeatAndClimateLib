package defeatedcrow.hac.api.recipe;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Climateを条件に進む、液体加工を含む多対多のレシピ。<br>
 * 材料は辞書対応。<br>
 * 甕での発酵、蒸留器での蒸留の両方で扱う。
 */
public interface ICrusherRecipe {

	/**
	 * Input登録内容
	 */
	Object getInput();

	@Nullable
	ItemStack getOutput();

	@Nullable
	ItemStack getSecondary();

	@Nullable
	ItemStack getTertialy();

	@Nullable
	ItemStack getCatalyst();

	@Nullable
	FluidStack getOutputFluid();

	float getChance();

	float getSecondaryChance();

	float getTertialyChance();

	/**
	 * macth条件判定用、鉱石辞書変換後のInputリスト
	 */
	List<ItemStack> getProcessedInput();

	/**
	 * Input条件判定
	 */
	boolean matches(ItemStack in);

	/**
	 * Output条件判定
	 */
	boolean matchOutput(List<ItemStack> target, FluidStack fluid, int slotsize);

	/**
	 * 触媒条件判定
	 */
	boolean matchCatalyst(ItemStack catalyst);
}
