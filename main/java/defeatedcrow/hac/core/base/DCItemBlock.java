package defeatedcrow.hac.core.base;

import java.util.List;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.placeable.IRapidCollectables;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
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
		return getNameSuffix() != null && j < getNameSuffix().length ?
				super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super.getUnlocalizedName() + "_" + j;
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
		super.addInformation(stack, world, tooltip, TooltipFlags.NORMAL);
		this.addInformation2(stack, world, tooltip);
		if (block instanceof IRapidCollectables) {
			IRapidCollectables col = (IRapidCollectables) block;
			if (col.getCollectableTool() != null) {
				int a = col.getCollectArea(ItemStack.EMPTY) * 2 + 1;
				tooltip.add(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "=== Tips ===");
				tooltip.add("Right-click with " + col.getCollectableTool() + " : " + I18n
						.format("dcs.tip.rapid_collect") + " " + a + "x" + a + "x" + a);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {}

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
