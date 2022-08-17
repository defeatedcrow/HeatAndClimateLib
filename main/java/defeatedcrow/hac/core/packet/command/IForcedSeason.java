package defeatedcrow.hac.core.packet.command;

import defeatedcrow.hac.api.climate.EnumSeason;

public interface IForcedSeason {

	EnumSeason getSeason();

	void setForcedSeason(EnumSeason season);

	boolean isForced();

	void setForced(boolean b);
}
