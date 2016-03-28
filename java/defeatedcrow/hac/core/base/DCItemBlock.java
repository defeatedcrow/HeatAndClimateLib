package defeatedcrow.hac.core.base;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class DCItemBlock extends ItemBlock {

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
		int j = Math.min(stack.getItemDamage(), getMaxMeta());
		return j > 0 ? super.getUnlocalizedName() + "_" + j : super.getUnlocalizedName();
	}

	public int getMaxMeta() {
		return ((DCTileBlock) this.block).getMaxMeta();
	}

}
