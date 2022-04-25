package defeatedcrow.hac.core.base;

import java.util.List;
import java.util.Random;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.placeable.ISidedTexture;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * 方向用のメタを一つだけ持つ。
 * 他のメタは7種のタイプに使われる。
 */
public class DCSidedBlock extends ClimateBlock implements ISidedTexture, INameSuffix {

	protected Random rand = new Random();
	public static final String CL_TEX = "dcs_climate:blocks/clear";

	// Type上限
	public final int maxMeta;
	public final boolean forceUpdate;

	public DCSidedBlock(Material m, String s, int max, boolean force) {
		super(m, s, force);
		this.setHardness(0.5F);
		this.setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.FLAG, false)
				.withProperty(DCState.TYPE8, 0));
		if (max < 0 || max > 7)
			max = 7;
		this.maxMeta = max;
		forceUpdate = force;
	}

	@Override
	public int tickRate(World world) {
		return 100;
	}

	/*
	 * ItemのUnlocalizedNameとかTexture指定とかに使う、メタと名前末尾の照合用リスト。
	 * 各Blockで中身を入れる
	 */
	@Override
	public String[] getNameSuffix() {
		return null;
	}

	/* Json登録とかクリエタブ登録とか */
	public int getMaxMeta() {
		return maxMeta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = super.getSubItemList();
		for (int i = 0; i < maxMeta + 1; i++) {
			list.add(new ItemStack(this, 1, i));
		}
		return list;
	}

	// 設置・破壊処理
	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		boolean face = placer.getHorizontalFacing() == EnumFacing.NORTH || placer
				.getHorizontalFacing() == EnumFacing.SOUTH;
		state = state.withProperty(DCState.FLAG, face);
		return state;
	}

	@Override
	public int damageDropped(IBlockState state) {
		int i = DCState.getInt(state, DCState.TYPE8);
		if (i > maxMeta)
			i = maxMeta;
		return i;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(state.getBlock());
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int i = meta & 7;
		boolean f = (meta & 8) != 0;
		IBlockState state = this.getDefaultState().withProperty(DCState.FLAG, f).withProperty(DCState.TYPE8, i);
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = state.getValue(DCState.TYPE8);
		if (i > maxMeta)
			i = maxMeta;
		boolean f = state.getValue(DCState.FLAG);

		return f ? i : i | 8;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { DCState.FLAG, DCState.TYPE8 });
	}

	@Override
	public IProperty[] ignoreTarget() {
		return null;
	}

	@Override
	public EnumStateType getType() {
		return EnumStateType.CUSTOM;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		boolean f = state.getValue(DCState.FLAG);
		return state.withProperty(DCState.FLAG, !f);
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state;
	}

	/** T, B, N, S, W, E */
	@Override
	public String getTexture(int meta, int side, boolean face) {
		return CL_TEX;
	}

}
