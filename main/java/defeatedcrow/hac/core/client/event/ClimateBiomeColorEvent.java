package defeatedcrow.hac.core.client.event;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.core.ClimateCore;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClimateBiomeColorEvent {

	@SubscribeEvent
	public void waterColor(BiomeEvent.GetWaterColor event) {
		if (ClimateCore.proxy.getPlayer() != null) {
			Biome current = event.getBiome();
			float biometemp = current.getTemperature(ClimateCore.proxy.getPlayer().getPosition());
			DCHeatTier tier = DCHeatTier.getTypeByBiomeTemp(biometemp);

			if (tier == DCHeatTier.ABSOLUTE) {
				event.setNewColor(0xFFFFFF);

			} else if (tier == DCHeatTier.CRYOGENIC) {
				event.setNewColor(0xE0E0FF);

			} else if (tier == DCHeatTier.INFERNO) {
				event.setNewColor(0xFF5000);

			} else if (tier.getTier() > DCHeatTier.BOIL.getTier()) {
				int r = (event.getOriginalColor() >> 16) & 255;
				int g = (event.getOriginalColor() >> 8) & 255;
				int b = event.getOriginalColor() & 255;

				int nr = r + tier.getTier() * 30;
				if (nr > 255) {
					nr = 255;
				}

				int result = (nr << 16) + (g << 8) + b;
				event.setNewColor(result);
			}
		}
	}

	@SubscribeEvent
	public void grassColor(BiomeEvent.GetGrassColor event) {
		if (ClimateCore.proxy.getPlayer() != null) {
			Biome current = event.getBiome();
			float biometemp = current.getTemperature(ClimateCore.proxy.getPlayer().getPosition());
			DCHeatTier tier = DCHeatTier.getTypeByBiomeTemp(biometemp);

			if (tier == DCHeatTier.ABSOLUTE) {
				event.setNewColor(0xFFFFFF);

			} else if (tier == DCHeatTier.CRYOGENIC) {
				event.setNewColor(0xE0E0FF);

			} else if (tier == DCHeatTier.INFERNO) {
				event.setNewColor(0xFF5000);

			} else if (tier.getTier() > DCHeatTier.BOIL.getTier()) {
				int r = (event.getOriginalColor() >> 16) & 255;
				int g = (event.getOriginalColor() >> 8) & 255;
				int b = event.getOriginalColor() & 255;

				int nr = r + tier.getTier() * 30;
				if (nr > 255) {
					nr = 255;
				}

				int result = (nr << 16) + (g << 8) + b;
				event.setNewColor(result);
			}
		}
	}

	@SubscribeEvent
	public void foliageColor(BiomeEvent.GetFoliageColor event) {
		if (ClimateCore.proxy.getPlayer() != null) {
			Biome current = event.getBiome();
			float biometemp = current.getTemperature(ClimateCore.proxy.getPlayer().getPosition());
			DCHeatTier tier = DCHeatTier.getTypeByBiomeTemp(biometemp);

			if (tier == DCHeatTier.ABSOLUTE) {
				event.setNewColor(0xFFFFFF);

			} else if (tier == DCHeatTier.CRYOGENIC) {
				event.setNewColor(0xE0E0FF);

			} else if (tier == DCHeatTier.INFERNO) {
				event.setNewColor(0xFF5000);

			} else if (tier.getTier() > DCHeatTier.BOIL.getTier()) {
				int r = (event.getOriginalColor() >> 16) & 255;
				int g = (event.getOriginalColor() >> 8) & 255;
				int b = event.getOriginalColor() & 255;

				int nr = r + tier.getTier() * 30;
				if (nr > 255) {
					nr = 255;
				}

				int result = (nr << 16) + (g << 8) + b;
				event.setNewColor(result);
			}
		}
	}

}
