package defeatedcrow.hac.core.packet.command;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.config.ForcedSeasonJson;
import defeatedcrow.hac.config.ForcedSeasonJson.SeasonData;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ForcedSeasonHandler implements IForcedSeason, ICapabilityProvider {

	private final World world;

	public ForcedSeasonHandler(World worldIn) {
		world = worldIn;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityForcedSeason.FORCED_SEASON_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityForcedSeason.FORCED_SEASON_CAPABILITY ? (T) this : null;
	}

	@Override
	public EnumSeason getSeason() {
		SeasonData data = ForcedSeasonJson.DATA;
		if (data != null) {
			return data.getSeason();
		}
		return EnumSeason.SPRING;
	}

	@Override
	public void setForcedSeason(EnumSeason season) {
		SeasonData data = ForcedSeasonJson.DATA;
		if (data != null) {
			ForcedSeasonJson.DATA.setSeason(season);
			ForcedSeasonJson.write();
		}
	}

	@Override
	public boolean isForced() {
		SeasonData data = ForcedSeasonJson.DATA;
		if (data != null) {
			return data.getForced();
		}
		return false;
	}

	@Override
	public void setForced(boolean b) {
		SeasonData data = ForcedSeasonJson.DATA;
		if (data != null) {
			ForcedSeasonJson.DATA.setForced(b);
			ForcedSeasonJson.write();
		}
	}

}
