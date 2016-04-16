package defeatedcrow.hac.core.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockSet {

	public final Block block;
	public final int meta;

	public BlockSet(Block i, int j) {
		block = i;
		meta = j;
	}

	public IBlockState getState() {
		return block.getStateFromMeta(meta);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BlockSet) {
			BlockSet p = (BlockSet) obj;
			return p.block == block && p.meta == meta;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int i = block.getIdFromBlock(block) + meta * 31;
		return i;
	}
}
