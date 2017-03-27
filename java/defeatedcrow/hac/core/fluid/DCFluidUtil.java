package defeatedcrow.hac.core.fluid;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class DCFluidUtil {

	private DCFluidUtil() {

	}

	/**
	 * DCTankをFluidContItemで右クリックする処理
	 */
	public static boolean onActivateDCTank(TileEntity tile, ItemStack item, World world, IBlockState state,
			EnumFacing side, EntityPlayer player) {
		if (!DCUtil.isEmpty(item) && tile != null
				&& item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)
				&& tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
			ItemStack copy = new ItemStack(item.getItem(), 1, item.getItemDamage());
			if (item.getTagCompound() != null)
				copy.setTagCompound(item.getTagCompound());
			IFluidHandler cont = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			IFluidHandler dummy = copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			IFluidHandler intank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
			IFluidHandler outtank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
					EnumFacing.DOWN);

			// dummyを使った検証
			if (dummy != null && dummy.getTankProperties() != null && intank instanceof DCTank
					&& outtank instanceof DCTank) {
				int max = dummy.getTankProperties()[0].getCapacity();
				FluidStack f1 = dummy.drain(max, false);
				DCTank dc_in = (DCTank) intank;
				DCTank dc_out = (DCTank) outtank;

				ItemStack ret = null;
				boolean success = false;
				// input
				if (f1 != null && dc_in.fill(f1, false) > 0) {
					int f2 = dc_in.fill(f1, false);
					FluidStack fill = dummy.drain(f2, true);
					ret = copy;
					if (ret.stackSize <= 0) {
						ret = null;
					}
					if (fill != null && fill.amount > 0) {
						dc_in.fill(fill, true);
						success = true;
					}
				}
				// output
				else if (f1 == null && dc_out.drain(max, false) != null) {
					int drain = dummy.fill(dc_out.drain(max, false), true);
					ret = copy;
					if (ret.stackSize <= 0) {
						ret = null;
					}
					if (drain > 0) {
						dc_out.drain(drain, true);
						success = true;
					}
				}

				if (success) {
					if (/* !player.capabilities.isCreativeMode && */ item.stackSize-- <= 0) {
						item = null;
					}
					tile.markDirty();
					player.inventory.markDirty();
					if (!DCUtil.isEmpty(ret)) {
						EntityItem drop = new EntityItem(world, player.posX, player.posY + 0.25D, player.posZ, ret);
						world.spawnEntityInWorld(drop);
					}
					return true;
				}
			}
		}
		return false;
	}

}