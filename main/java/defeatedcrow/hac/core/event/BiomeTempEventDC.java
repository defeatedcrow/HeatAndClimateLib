package defeatedcrow.hac.core.event;

import java.util.Random;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.api.hook.DCBiomeTempEvent;
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
			int id = Biome.getIdForBiome(event.biome);
			float temp = event.defaultTemp;

			if (ClimateAPI.register.getClimateList().containsKey(id)) {
				temp = ClimateAPI.register.getHeatTier(id).getBiomeTemp();
			}

			// season
			if (ClimateCore.proxy.getWorld() != null && !ClimateAPI.register.getNoSeasonList().contains(id)) {
				if (CoreConfigDC.enableSeasonEffect) {
					EnumSeason season = DCTimeHelper.getSeasonEnum(ClimateCore.proxy.getWorld());
					temp += CoreConfigDC.getSeasonTempOffset(season);
				}
			}

			// attitude

			if (pos.getY() > 80) {
				float h = (pos.getY() - 80) * 0.1F;
				temp -= (h * 0.05F);
			} else if (pos.getY() < 60) {
				float h = (60 - pos.getY()) * 0.1F;
				temp += (h * 0.05F);
			}

			if (temp < -10) {
				temp = -10;
			}
			if (temp > 200) {
				temp = 200;
			}

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
