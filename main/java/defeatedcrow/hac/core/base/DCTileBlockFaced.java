package defeatedcrow.hac.core.base;

import defeatedcrow.hac.api.blockstate.DCState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// TESR持ちブロックのベース
public abstract class DCTileBlockFaced extends DCTileBlock {

	public DCTileBlockFaced(Material m, String s, int max) {
		super(m, s, max);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.FACING, EnumFacing.SOUTH)
				.withProperty(DCState.TYPE4, 0));
	}

	public static void changeState(World world, BlockPos pos, int i) {
		IBlockState state = world.getBlockState(pos);
		int m = DCState.getInt(state, DCState.TYPE4);
		if (m >= 0) {
			if (m != i) {
				world.setBlockState(pos, state.withProperty(DCState.TYPE4, i), 3);
				world.notifyNeighborsOfStateChange(pos, state.getBlock(), true);
			}
		}
	}

	// 設置・破壊処理
	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		state = state.withProperty(DCState.FACING, placer.getHorizontalFacing());
		return state;
	}

	@Override
	public int damageDropped(IBlockState state) {
		int i = state.getValue(DCState.TYPE4);
		if (i > maxMeta)
			i = maxMeta;
		return i;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int i = meta & 3;
		if (i > maxMeta)
			i = maxMeta;
		int f = 5 - (meta >> 2);
		IBlockState state = this.getDefaultState().withProperty(DCState.TYPE4, i);
		state = state.withProperty(DCState.FACING, EnumFacing.getFront(f));
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		int f = 0;

		i = state.getValue(DCState.TYPE4);
		if (i > maxMeta)
			i = maxMeta;

		f = 5 - state.getValue(DCState.FACING).getIndex();
		f = f << 2;
		return i + f;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { DCState.FACING, DCState.TYPE4 });
	}

	@Override
	public IProperty[] ignoreTarget() {
		return new IProperty[] { DCState.TYPE4 };
	}

	@Override
	public EnumStateType getType() {
		return EnumStateType.FACING;
	}

}
