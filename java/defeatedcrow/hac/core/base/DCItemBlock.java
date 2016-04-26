package defeatedcrow.hac.core.base;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class DCItemBlock extends ItemBlock implements ITexturePath {

	public DCItemBlock(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int j = Math.min(stack.getItemDamage(), 15);
		return getNameSuffix() != null && j < getNameSuffix().length ? super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super
				.getUnlocalizedName() + "_" + j;
	}

	/* Blockから引っ張ってくる */
	protected String[] getNameSuffix() {
		if (this.block instanceof INameSuffix)
			return ((INameSuffix) this.block).getNameSuffix();
		else
			return null;
	}

	/* ItemModelJson自動生成用 */
	@Override
	public String getTexPath(int meta, boolean f) {
		return "blocks/bedrock";
	}

}
