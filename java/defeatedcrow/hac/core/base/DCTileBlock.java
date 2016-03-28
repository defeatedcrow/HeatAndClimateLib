package defeatedcrow.hac.core.base;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.core.ClimateCore;

// TESR持ちブロックのベース
public class DCTileBlock extends BlockContainer {

	protected Random rand = new Random();

	// Type上限
	public final int maxMeta;

	// 同系ブロック共通ﾌﾟﾛﾊﾟﾁｰ
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 3);

	public DCTileBlock(Material m, String s, int max) {
		super(m);
		this.setCreativeTab(ClimateCore.climate);
		this.setUnlocalizedName(s);
		this.setHardness(0.5F);
		this.setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH)
				.withProperty(TYPE, 0));
		this.maxMeta = max;
	}

	public int getMaxMeta() {
		return maxMeta;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new DCTileEntity();
	}

	@Override
	public boolean isFullBlock() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos,
			net.minecraft.client.particle.EffectRenderer effectRenderer) {
		effectRenderer.addBlockDestroyEffects(pos, Blocks.bedrock.getStateFromMeta(0));
		return true;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < maxMeta + 1; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	// 設置・破壊処理
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		state = state.withProperty(FACING, placer.getHorizontalFacing());
		return state;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		DCTileEntity te = null;
		if (tile != null && tile instanceof DCTileEntity) {
			te = (DCTileEntity) tile;
		}

		if (te != null) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				te.setNBT(tag);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		DCTileEntity te = null;
		if (tile instanceof DCTileEntity) {
			te = (DCTileEntity) tile;
		}
		int i = this.damageDropped(state);
		if (te != null) {
			ItemStack drop = new ItemStack(this, 1, i);
			NBTTagCompound tag = new NBTTagCompound();
			tag = te.getNBT(tag);
			if (tag != null)
				drop.setTagCompound(tag);
			EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop);
			float f3 = 0.05F;
			entityitem.motionX = (float) this.rand.nextGaussian() * f3;
			entityitem.motionY = (float) this.rand.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) this.rand.nextGaussian() * f3;
			world.spawnEntityInWorld(entityitem);
		}
		world.updateComparatorOutputLevel(pos, state.getBlock());
	}

	@Override
	public int damageDropped(IBlockState state) {
		int i = state.getValue(TYPE);
		if (i > maxMeta)
			i = maxMeta;
		return i;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int i = meta & 3;
		if (i > maxMeta)
			i = maxMeta;
		int f = 5 - (meta >> 2);
		IBlockState state = this.getDefaultState().withProperty(TYPE, i);
		state = state.withProperty(FACING, EnumFacing.getFront(f));
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		int f = 0;

		i = state.getValue(TYPE);
		if (i > maxMeta)
			i = maxMeta;

		f = 5 - state.getValue(FACING).getIndex();
		f = f << 2;
		return i + f;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] {
				FACING,
				TYPE });
	}

}
