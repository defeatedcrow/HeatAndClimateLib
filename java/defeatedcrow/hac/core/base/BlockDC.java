package defeatedcrow.hac.core.base;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 移植時に苦労するので代替レシピへの置き換えを作成
 */
public class BlockDC extends Block {

	public BlockDC(Material m, String s) {
		super(m);
		this.setUnlocalizedName(s);
	}

	/**
	 * -> onRightClick
	 */
	@Deprecated
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			@Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		return onRightClick(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(world, pos, state, player, hand, null, side, hitX, hitY, hitZ);
	}

	/**
	 * -> getPlaceState
	 */
	@Deprecated
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		IBlockState state = this.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer,
				placer.getActiveHand());
		return state;
	}

	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		return state;
	}

	/**
	 * -> onNeighborChange
	 */
	@Deprecated
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		this.onNeighborChange(state, world, pos, block, null);
	}

	public void onNeighborChange(IBlockState state, World world, BlockPos pos, Block block, @Nullable BlockPos from) {}

	/**
	 * -> getSubItemList
	 */
	@Deprecated
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		if (DCUtil.machCreativeTab(tab, getCreativeTabToDisplayOn())) {
			List<ItemStack> itms = getSubItemList();
			list.addAll(itms);
		}
	}

	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		return list;
	}

	/**
	 * -> getCollisionBoxList
	 */
	@Deprecated
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
		this.getCollisionBoxList(state, world, pos, entityBox, collidingBoxes, entity, false);
	}

	public void getCollisionBoxList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean b) {
		super.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));
	}

	/**
	 * -> getCollisionBox
	 */
	@Deprecated
	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World world, BlockPos pos) {
		return this.getCollisionBox(blockState, world, pos);
	}

	public AxisAlignedBB getCollisionBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return blockState.getBoundingBox(worldIn, pos);
	}

}
