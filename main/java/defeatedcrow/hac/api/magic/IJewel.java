package defeatedcrow.hac.api.magic;

public abstract interface IJewel {

	CharmType getCharmType(int meta);

	MagicType getType(int meta);

	MagicColor getColor(int meta);

}
