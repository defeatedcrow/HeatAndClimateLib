package defeatedcrow.hac.core.packet.command;

import defeatedcrow.hac.api.climate.EnumSeason;

public class ForcedSeason implements IForcedSeason {

	public EnumSeason forced = EnumSeason.SPRING;
	public boolean isForced = false;

	@Override
	public EnumSeason getSeason() {
		return forced;
	}

	@Override
	public void setForcedSeason(EnumSeason season) {
		forced = season;
	}

	@Override
	public boolean isForced() {
		return isForced;
	}

	@Override
	public void setForced(boolean b) {
		isForced = b;
	}

}
