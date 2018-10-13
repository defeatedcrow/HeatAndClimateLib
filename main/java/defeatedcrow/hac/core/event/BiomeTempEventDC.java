package defeatedcrow.hac.core.event;

import java.util.Random;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.api.recipe.DCBiomeTempEvent;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeTempEventDC {

	protected static final NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);

	@SubscribeEvent
	public void onTemp(DCBiomeTempEvent event) {
		if (event.biome != null && event.pos != null) {
			BlockPos pos = event.pos;
			float temp = event.defaultTemp;

			// season
			if (ClimateCore.proxy.getWorld() != null
					&& !ClimateAPI.register.getNoSeasonList().contains(Biome.getIdForBiome(event.biome))) {
				if (CoreConfigDC.enableWeatherEffect) {
					EnumSeason season = DCTimeHelper.getSeasonEnum(ClimateCore.proxy.getWorld());
					temp += season.temp;
				}
			}

			// attitude
			float h = (pos.getY() - 80) * 0.1F;
			if (h > 15) {
				h = 15F;
			}
			if (h < -10) {
				h = -10F;
			}
			temp -= (h * 0.05F);

			if (pos.getY() > 64) {
				float f = (float) (TEMPERATURE_NOISE.getValue(pos.getX() / 8.0F, pos.getZ() / 8.0F) * 4.0D);
				event.newTemp = temp - (f + pos.getY() - 64.0F) * 0.05F / 30.0F;
			} else {
				event.newTemp = temp;
			}

			if (event.newTemp != event.defaultTemp) {
				event.setResult(Result.ALLOW);
			}
		}
	}

}
