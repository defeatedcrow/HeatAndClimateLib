package defeatedcrow.hac.core.util;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeCatchDC {

	private static Biome[] biomeList;
	private static int lastChunk = -1;

	// public static Biome getBiome(int cx, int cz, World world) {
	// int x = 8 + cx * 16;
	// int z = 8 + cz * 16;
	// BlockPos pos = new BlockPos(x, 1, z);
	// return world.getBiome(pos);
	// }
	//
	// public static Biome getBiomeForCoords(int cx, int cz, int px, int pz, World world) {
	// int x = px + cx * 16;
	// int z = pz + cz * 16;
	// BlockPos pos = new BlockPos(x, 1, z);
	// return world.getBiome(pos);
	// }

	public static Biome getBiome(int cx, int cz, World world) {
		int x = 8 + cx * 16;
		int z = 8 + cz * 16;
		BlockPos pos = new BlockPos(x, 1, z);
		return world.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
	}

	public static Biome getBiomeForCoords(int cx, int cz, int px, int pz, World world) {
		int x = px + cx * 16;
		int z = pz + cz * 16;
		BlockPos pos = new BlockPos(x, 1, z);
		return world.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
	}

	public static Biome getBiome(BlockPos pos, World world) {
		return world.getBiomeProvider().getBiome(pos, Biomes.PLAINS);
	}

}
