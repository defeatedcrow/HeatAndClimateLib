package defeatedcrow.hac.core.base;

import java.util.Random;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.placeable.ISidedTexture;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * TEなし16種のブロック
 */
public class DCSimpleBlock extends ClimateBlock implements ISidedTexture, INameSuffix {

	protected Random rand = new Random();
	public static final String CL_TEX = "dcs_climate:blocks/clear";

	// Type上限
	public final int maxMeta;
	public final boolean forceUpdate;

	public DCSimpleBlock(Material m, String s, int max, boolean force) {
		super(m, force);
		this.setUnlocalizedName(s);
		this.setHardness(0.5F);
		this.setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.TYPE16, 0));
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {}

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
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (DCUtil.machCreativeTab(tab, getCreativeTabToDisplayOn()))
			for (int i = 0; i < maxMeta + 1; i++) {
				list.add(new ItemStack(this, 1, i));
			}
	}

	// 設置・破壊処理
	@Override
	public int damageDropped(IBlockState state) {
		int i = state.getValue(DCState.TYPE16);
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
		int i = meta & 15;
		IBlockState state = this.getDefaultState().withProperty(DCState.TYPE16, i);
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		i = state.getValue(DCState.TYPE16);
		if (i > maxMeta)
			i = maxMeta;
		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.TYPE16
		});
	}

	/** T, B, N, S, W, E */
	@Override
	public String getTexture(int meta, int side, boolean face) {
		return CL_TEX;
	}

}
