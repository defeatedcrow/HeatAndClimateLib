package defeatedcrow.hac.core.base;

/* 
 * ItemやBlockなどの多種のオブジェクトとテクスチャパスを紐付けるインターフェイス。
 * 主にJson自動生成用
 */
public interface ITexturePath {

	/**
	 * Json用
	 */
	String getTexPath(int meta, boolean isFull);

}
