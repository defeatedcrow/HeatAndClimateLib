package defeatedcrow.hac.core.climate;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.climate.IThermalInsulationBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermalInsulationUtil {

	public static int getInsulation(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state != null && state.isOpaqueCube()) {
			if (state.getBlock() instanceof IThermalInsulationBlock) {
				return ((IThermalInsulationBlock) state.getBlock()).getReductionAmount(world, pos, state);
			} else {
				if (!BLOCK_MAP.isEmpty() && BLOCK_MAP.containsKey(state.getBlock())) {
					int i = BLOCK_MAP.get(state.getBlock());
					return i;
				} else if (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.CLOTH) {
					return 1;
				}
			}
		}
		return 0;
	}

	public static final Map<Block, Integer> BLOCK_MAP = new HashMap<Block, Integer>();

	public static void load() {
		BLOCK_MAP.put(Blocks.BRICK_BLOCK, 2);
		BLOCK_MAP.put(Blocks.NETHER_BRICK, 2);
		BLOCK_MAP.put(Blocks.STAINED_HARDENED_CLAY, 2);
		BLOCK_MAP.put(Blocks.HARDENED_CLAY, 2);
		BLOCK_MAP.put(Blocks.SPONGE, 2);
	}

}
