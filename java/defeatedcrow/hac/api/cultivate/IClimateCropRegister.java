package defeatedcrow.hac.api.cultivate;

import java.util.Map;

/**
 * JEI対応用の登録インターフェイス。<br>
 * ここに登録しなくても、動作はする。
 */
public interface IClimateCropRegister {

	/**
	 * Recipeのリストを得る。
	 */
	Map<String, IClimateCrop> getList();

	void addCropData(IClimateCrop block);

}
