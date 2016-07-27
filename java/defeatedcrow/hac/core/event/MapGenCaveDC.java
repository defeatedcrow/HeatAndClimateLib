package defeatedcrow.hac.core.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

/**
 * MapGenCavesと同様の内容だが、一部置き換えブロックが異なるようになっている
 */
public class MapGenCaveDC extends MapGenCaves {
	protected static final IBlockState BLK_WATER = Blocks.WATER.getDefaultState();

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop,
			IBlockState state, IBlockState up) {
		Biome biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
		IBlockState top = biome.topBlock;
		IBlockState filler = biome.fillerBlock;
		boolean flag = false;

		if (this.canReplaceBlock(state, up) || state.getBlock() == top.getBlock()
				|| state.getBlock() == filler.getBlock()) {
			if (y - 1 < 10 && biome.getRainfall() >= 0.9F) {
				data.setBlockState(x, y, z, BLK_WATER);
				flag = true;
			}
		}

		if (!flag) {
			super.digBlock(data, x, y, z, chunkX, chunkZ, foundTop, state, up);
		}
	}
}
