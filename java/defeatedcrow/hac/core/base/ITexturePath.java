package defeatedcrow.hac.core.base;

// ItemやBlockなどの多種のオブジェクトとテクスチャパスを紐付けるインターフェイス。
public interface ITexturePath {

	String getTexPath(int meta, boolean isFull);

}
