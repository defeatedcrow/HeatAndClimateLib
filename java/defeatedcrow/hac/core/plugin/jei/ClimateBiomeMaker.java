package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IModRegistry;
import net.minecraft.world.biome.Biome;

public final class ClimateBiomeMaker {

	private ClimateBiomeMaker() {}

	public static void register(IModRegistry registry) {
		List<Biome> biomes = Lists.newArrayList(Biome.REGISTRY.iterator());
		registry.addRecipes(biomes);
	}

}
