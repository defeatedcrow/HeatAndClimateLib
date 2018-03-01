package defeatedcrow.hac.core.base;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DCItemBlock extends ItemBlock implements ITexturePath {

	public DCItemBlock(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setRegistryName(block.getRegistryName());
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int j = Math.min(stack.getMetadata(), 15);
		return getNameSuffix() != null && j < getNameSuffix().length
				? super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super.getUnlocalizedName() + "_" + j;
	}

	/* Blockから引っ張ってくる */
	protected String[] getNameSuffix() {
		if (this.block instanceof INameSuffix)
			return ((INameSuffix) this.block).getNameSuffix();
		else
			return null;
	}

	/* ItemModelJson自動生成用 */
	@Override
	public String getTexPath(int meta, boolean f) {
		return "blocks/bedrock";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		this.addInformation2(stack, world, tooltip);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {
		block.addInformation(stack, world, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
	}

	/**
	 * 移植補助
	 */

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		return this.onItemUse2(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	public EnumActionResult onItemUse2(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		return this.onItemRightClick2(world, player, hand);
	}

	public ActionResult<ItemStack> onItemRightClick2(World world, EntityPlayer player, EnumHand hand) {
		return super.onItemRightClick(world, player, hand);
	}

}
