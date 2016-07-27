package defeatedcrow.hac.api.blockstate;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * BlockStateが使いづらかったのでアクセスポイント作った
 */
public class DCState {
	// int系
	public static final PropertyInteger TYPE16 = PropertyInteger.create("type", 0, 15);
	public static final PropertyInteger TYPE8 = PropertyInteger.create("type", 0, 7);
	public static final PropertyInteger TYPE4 = PropertyInteger.create("type", 0, 3);

	// bool
	public static final PropertyBool FLAG = PropertyBool.create("flag");
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	// facing
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	// side
	public static final PropertyEnum<EnumSide> SIDE = PropertyEnum.<EnumSide> create("side", EnumSide.class);

	// crop
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 3);
	public static final PropertyBool GROWN = PropertyBool.create("grown");
	public static final PropertyBool FLOWER = PropertyBool.create("flower");

	public static int getInt(IBlockState state, PropertyInteger prop) {
		if (state != null && hasProperty(state, prop)) {
			return state.getValue(prop);
		} else {
			return -1;
		}
	}

	public static boolean getBool(IBlockState state, PropertyBool prop) {
		if (state != null && hasProperty(state, prop)) {
			return state.getValue(prop);
		} else {
			return false;
		}
	}

	public static EnumFacing getFace(IBlockState state, PropertyDirection prop) {
		if (state != null && hasProperty(state, prop)) {
			return state.getValue(prop);
		} else {
			return null;
		}
	}

	public static EnumSide getSide(IBlockState state, PropertyEnum<EnumSide> prop) {
		if (state != null && hasProperty(state, prop)) {
			return state.getValue(prop);
		} else {
			return null;
		}
	}

	public static IBlockState setInt(IBlockState state, PropertyInteger prop, int i) {
		if (state != null && hasProperty(state, prop)) {
			return state.withProperty(prop, i);
		} else {
			return null;
		}
	}

	public static IBlockState setBool(IBlockState state, PropertyBool prop, boolean i) {
		if (state != null && hasProperty(state, prop)) {
			return state.withProperty(prop, i);
		} else {
			return null;
		}
	}

	public static IBlockState setFace(IBlockState state, PropertyDirection prop, EnumFacing i) {
		if (state != null && hasProperty(state, prop)) {
			return state.withProperty(prop, i);
		} else {
			return null;
		}
	}

	public static IBlockState setSide(IBlockState state, PropertyEnum<EnumSide> prop, EnumSide i) {
		if (state != null && hasProperty(state, prop)) {
			return state.withProperty(prop, i);
		} else {
			return null;
		}
	}

	public static boolean hasProperty(IBlockState state, IProperty prop) {
		return state.getProperties().containsKey(prop);
	}
}
