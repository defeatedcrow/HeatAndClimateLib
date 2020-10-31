package defeatedcrow.hac.core.energy;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.blockstate.EnumSide;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.base.BlockContainerDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/*
 * トルク系装置のBlockクラス。
 * メタデータは基本的に持たない。
 */
public abstract class BlockTorqueBase extends BlockContainerDC {

	protected boolean isHorizontal = false;

	public BlockTorqueBase(Material m, String s, int max) {
		super(m, s);
		this.setHardness(1.5F);
		this.setResistance(30.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.SIDE, EnumSide.DOWN)
				.withProperty(DCState.POWERED, false));
		this.fullBlock = false;
		this.lightOpacity = 0;
	}

	public BlockTorqueBase setHorizontal() {
		isHorizontal = true;
		return this;
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}

	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (DCUtil.isHeldWrench(player, hand)) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileTorqueBase && ((TileTorqueBase) tile).hasFaceSide()) {
				((TileTorqueBase) tile).rotateFace();
			} else {
				EnumSide current = DCState.getSide(state, DCState.SIDE);
				EnumSide next = EnumSide.NORTH;
				if (isHorizontal()) {
					next = current.rotatedHorizontalSide();
				} else {
					next = current.rotatSide();
				}
				world.setBlockState(pos, state.withProperty(DCState.SIDE, next));
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	public static void changeState(World world, BlockPos pos, boolean b) {
		IBlockState state = world.getBlockState(pos);
		boolean m = DCState.getBool(state, DCState.POWERED);
		if (m != b) {
			world.setBlockState(pos, state.withProperty(DCState.POWERED, b), 3);
			world.notifyNeighborsOfStateChange(pos, state.getBlock(), true);
		}
	}

	// 設置・破壊処理
	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		if (placer != null && isHorizontal()) {
			EnumFacing face = placer.getHorizontalFacing();
			state = state.withProperty(DCState.SIDE, EnumSide.fromFacing(face.getOpposite()));
		} else {
			if (isHorizontal()) {
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
					facing = EnumFacing.SOUTH;
				}
			}
			state = state.withProperty(DCState.SIDE, EnumSide.fromFacing(facing.getOpposite()));
		}
		return state;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int m = meta & 7;
		IBlockState state = this.getDefaultState().withProperty(DCState.SIDE, EnumSide.fromIndex(m))
				.withProperty(DCState.POWERED, Boolean.valueOf((meta & 8) > 0));
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int f = 0;
		int i = 0;

		f = state.getValue(DCState.SIDE).index;
		i = state.getValue(DCState.POWERED) ? 8 : 0;
		return i + f;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.SIDE,
				DCState.POWERED
		});
	}

	/* HardMode */

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && CoreConfigDC.harderMachine && entity instanceof EntityLivingBase) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileTorqueBase) {
				TileTorqueBase machine = (TileTorqueBase) tile;
				float t = machine.getCurrentTorque();
				float m = machine.maxTorque();
				float g = machine.getGearTier();
				float d = (t / m) * g;
				float d2 = d * 0.01F;

				EntityLivingBase living = (EntityLivingBase) entity;
				if (d > 2F && living.attackEntityFrom(DamageSourceClimate.machineDamage, d * 0.25F)) {
					double x = pos.getX() + 0.5D - living.posX;
					double z = pos.getZ() + 0.5D - living.posZ;
					x *= d2;
					z *= d2;
					living.knockBack(living, 0.5F, x, z);
				}
			}
		}
	}
}
