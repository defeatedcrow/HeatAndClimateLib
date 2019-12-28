package defeatedcrow.hac.core.event;

import com.google.common.base.MoreObjects;

import defeatedcrow.hac.api.hook.DCCaveOceanBlock;
import defeatedcrow.hac.api.hook.DCCaveWaterEvent;
import defeatedcrow.hac.api.hook.DCRavineWaterEvent;
import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CaveDigEventDC {

	protected static final IBlockState BLK_WATER = Blocks.FLOWING_WATER.getDefaultState();
	protected static final IBlockState BLK_LAVA = Blocks.LAVA.getDefaultState();
	protected static final IBlockState BLK_AIR = Blocks.AIR.getDefaultState();

	@SubscribeEvent
	public void onOceanCheck(DCCaveOceanBlock event) {
		if (event.primer != null) {
			IBlockState state = event.primer.getBlockState(event.x, event.y, event.z);
			int h = CoreConfigDC.enableSubmergedCave ? 45 : 30;
			if (event.y > h && state.getMaterial() == Material.WATER) {
				event.setResult(Result.ALLOW);
			}
		}
	}

	@SubscribeEvent
	public void onCaveDig(DCCaveWaterEvent e) {
		if (e.event != null && e.data != null) {
			try {
				IBlockState state = e.data.getBlockState(e.x, e.y, e.z);
				World world = ReflectionHelper.getPrivateValue(MapGenBase.class, e.event, "world", "field_75039_c");
				Biome biome = world.getBiomeForCoordsBody(new BlockPos(e.x + e.cx * 16, 0, e.z + e.cz * 16));
				IBlockState top = biome.topBlock;
				IBlockState filler = biome.fillerBlock;

				if (this.canReplaceBlock(state, e.up) || state.getBlock() == top.getBlock() || state
						.getBlock() == filler.getBlock()) {
					if (e.y - 1 < 10) {
						if (biome.getRainfall() >= 0.85F || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
							e.data.setBlockState(e.x, e.y, e.z, BLK_WATER);
							e.setResult(Result.ALLOW);
							return;
						}
					}
					if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) {
						if (e.y < 40 && CoreConfigDC.enableSubmergedCave) {
							e.data.setBlockState(e.x, e.y, e.z, BLK_WATER);
							e.setResult(Result.ALLOW);
							return;
						}
					}
				}
			} catch (Exception exc) {

			}
		}
	}

	@SubscribeEvent
	public void onRavineDig(DCRavineWaterEvent e) {
		if (e.event != null && e.data != null) {
			try {
				IBlockState state = e.data.getBlockState(e.x, e.y, e.z);
				IBlockState up = MoreObjects.firstNonNull(e.data.getBlockState(e.x, e.y + 1, e.z), BLK_AIR);
				World world = ReflectionHelper.getPrivateValue(MapGenBase.class, e.event, "world", "field_75039_c");
				Biome biome = world.getBiomeForCoordsBody(new BlockPos(e.x + e.cx * 16, 0, e.z + e.cz * 16));
				IBlockState top = biome.topBlock;
				IBlockState filler = biome.fillerBlock;

				if (this.canReplaceBlock(state, up) || state.getBlock() == top.getBlock() || state.getBlock() == filler
						.getBlock()) {
					if (e.y - 1 < 10) {
						if (biome.getRainfall() >= 0.85F || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
							e.data.setBlockState(e.x, e.y, e.z, BLK_WATER);
							e.setResult(Result.ALLOW);
							return;
						}
					}
					if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) {
						if (e.y < 40 && CoreConfigDC.enableSubmergedCave) {
							e.data.setBlockState(e.x, e.y, e.z, BLK_WATER);
							e.setResult(Result.ALLOW);
							return;
						}
					}
				}
			} catch (Exception exc) {

			}
		}
	}

	private boolean canReplaceBlock(IBlockState state, IBlockState up) {
		if (state.getBlock() instanceof BlockFalling) {
			return up.getMaterial() != Material.WATER;
		} else {
			if (state.getMaterial() == Material.GROUND || state.getMaterial() == Material.GRASS || state
					.getMaterial() == Material.SAND) {
				return true;
			} else {
				return state.getMaterial() == Material.ROCK && state.isNormalCube() && !(state
						.getBlock() instanceof BlockEmptyDrops);
			}
		}

	}
}
