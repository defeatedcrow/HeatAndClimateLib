package defeatedcrow.hac.core.block.smelting;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.core.base.DCSimpleBlock;

public class BlockOres extends DCSimpleBlock {

	public BlockOres(Material m, String s, int max) {
		super(m, s, max);
		this.setTickRandomly(true);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
	}

	@Override
	public void onUpdateClimate(World world, BlockPos pos) {
	}
}
