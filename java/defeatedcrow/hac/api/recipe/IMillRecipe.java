package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Climate不要の臼引きの1:1交換レシピ。<br>
 * 材料は辞書対応。
 */
public interface IMillRecipe {

	/**
	 * Input登録内容
	 */
	Object getInput();

	ItemStack getOutput();

	ItemStack getSecondary();

	float getSecondaryChance();

	/**
	 * Inputのコンテナアイテムのリスト
	 */
	ItemStack getContainerItem(ItemStack item);

	/**
	 * macth条件判定用、鉱石辞書変換後のInputリスト
	 */
	List<ItemStack> getProcessedInput();

	/**
	 * Input条件判定
	 */
	boolean matchInput(ItemStack item);

	/**
	 * Input条件判定
	 */
	boolean matchOutput(List<ItemStack> target, ItemStack input, int slotsize);
}
