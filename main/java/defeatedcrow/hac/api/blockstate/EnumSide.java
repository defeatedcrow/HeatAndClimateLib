package defeatedcrow.hac.api.blockstate;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumSide
		implements
		IStringSerializable {
	DOWN(
			0,
			"down",
			EnumFacing.DOWN),
	UP(
			1,
			"up",
			EnumFacing.UP),
	NORTH(
			2,
			"north",
			EnumFacing.NORTH),
	SOUTH(
			3,
			"south",
			EnumFacing.SOUTH),
	WEST(
			4,
			"west",
			EnumFacing.WEST),
	EAST(
			5,
			"east",
			EnumFacing.EAST);

	public final String name;
	public final int index;
	public final EnumFacing face;

	private EnumSide(int i, String n, EnumFacing f) {
		this.name = n;
		this.index = i;
		this.face = f;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static EnumSide fromFacing(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return DOWN;
		case UP:
			return UP;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case EAST:
			return EAST;
		case WEST:
			return WEST;
		default:
			return DOWN;
		}
	}

	public static EnumSide fromIndex(int i) {
		switch (i) {
		case 0:
			return DOWN;
		case 1:
			return UP;
		case 2:
			return NORTH;
		case 3:
			return SOUTH;
		case 4:
			return WEST;
		case 5:
			return EAST;
		default:
			return DOWN;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	public EnumFacing getFacing() {
		return face;
	}
}
