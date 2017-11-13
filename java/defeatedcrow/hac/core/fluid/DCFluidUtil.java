package defeatedcrow.hac.core.fluid;

import defeatedcrow.hac.core.base.DCInventory;
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
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class DCFluidUtil {

	private DCFluidUtil() {

	}

	/**
	 * DCTankをFluidContItemで右クリックする処理
	 */
	public static boolean onActivateDCTank(TileEntity tile, ItemStack item, World world, IBlockState state,
			EnumFacing side, EntityPlayer player) {
		if (!DCUtil.isEmpty(item) && tile != null
				&& item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, side)
				&& tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
			ItemStack copy = item.copy();
			if (item.getCount() > 1)
				copy.setCount(1);
			IFluidHandlerItem dummy = copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
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

				ItemStack ret = ItemStack.EMPTY;
				boolean success = false;
				// input
				if (f1 != null && dc_in.fill(f1, false) > 0) {
					int f2 = dc_in.fill(f1, false);
					FluidStack fill = dummy.drain(f2, true);
					ret = dummy.getContainer();
					if (fill != null && fill.amount > 0) {
						dc_in.fill(fill, true);
						success = true;
					}
				}
				// output
				else if (f1 == null && dc_out.drain(max, false) != null) {
					int drain = dummy.fill(dc_out.drain(max, false), true);
					ret = dummy.getContainer();
					if (drain > 0) {
						dc_out.drain(drain, true);
						success = true;
					}
				}

				if (success) {
					if (!player.capabilities.isCreativeMode) {
						DCUtil.reduceStackSize(item, 1);
					}
					tile.markDirty();
					player.inventory.markDirty();
					if (!DCUtil.isEmpty(ret)) {
						EntityItem drop = new EntityItem(world, player.posX, player.posY + 0.25D, player.posZ, ret);
						world.spawnEntity(drop);
					}
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack getEmptyCont(ItemStack target) {
		if (!DCUtil.isEmpty(target)
				&& target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			ItemStack copy = target.copy();
			copy.setCount(1);
			IFluidHandlerItem handler = copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if (handler != null) {
				handler.drain(handler.getTankProperties()[0].getCapacity(), true);
				return copy;
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean onFillTank(DCInventory inv, DCTank tank, int slot1, int slot2) {
		ItemStack in = inv.getStackInSlot(slot1);
		ItemStack out = inv.getStackInSlot(slot2);
		if (DCUtil.isEmpty(in))
			return false;

		IFluidHandlerItem dummy = null;
		ItemStack in2 = in.copy();
		if (in.getCount() > 1)
			in2.setCount(1);
		if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			dummy = in2.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		} else if (in.getItem() instanceof IFluidHandlerItem) {
			dummy = (IFluidHandlerItem) in2.getItem();
		}

		if (dummy != null && dummy.getTankProperties() != null) {
			boolean loose = false;
			ItemStack ret = ItemStack.EMPTY;

			int max = dummy.getTankProperties()[0].getCapacity();
			FluidStack fc = dummy.drain(max, false);
			// 流入の場合
			if (fc != null && fc.amount > 0 && tank.canFillTarget(fc)) {
				ret = ItemStack.EMPTY;
				loose = false;
				boolean b = false;
				int rem = tank.getCapacity() - tank.getFluidAmount();
				fc = dummy.drain(rem, false);
				if (fc != null && fc.amount <= rem) {
					FluidStack fill = null;
					fill = dummy.drain(rem, true);
					ret = dummy.getContainer();

					if (fill != null
							&& (DCUtil.isEmpty(ret) || inv.isItemStackable(ret, inv.getStackInSlot(slot2)) > 0)) {
						loose = true;
						tank.fill(fill, true);
					}
				}
			}

			if (loose) {
				inv.decrStackSize(slot1, 1);
				inv.incrStackInSlot(slot2, ret);
				return true;
			}
		}
		return false;
	}

	public static boolean onDrainTank(DCInventory inv, DCTank tank, int slot1, int slot2) {
		ItemStack in = inv.getStackInSlot(slot1);
		ItemStack out = inv.getStackInSlot(slot2);
		if (DCUtil.isEmpty(in))
			return false;

		IFluidHandlerItem dummy = null;
		ItemStack in2 = in.copy();
		if (in.getCount() > 1)
			in2.setCount(1);
		if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			dummy = in2.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		} else if (in.getItem() instanceof IFluidHandlerItem) {
			dummy = (IFluidHandlerItem) in2.getItem();
		}

		if (tank.getFluidAmount() > 0 && dummy != null && dummy.getTankProperties() != null) {
			boolean loose = false;
			ItemStack ret = ItemStack.EMPTY;

			int max = dummy.getTankProperties()[0].getCapacity();
			FluidStack fc = dummy.drain(max, false);
			boolean b = false;
			int rem = max;
			if (fc == null || fc.amount == 0) {
				b = true;
			} else {
				rem = max - fc.amount;
				if (tank.getFluidAmount() <= rem) {
					b = true;
				}
			}
			// 排出の場合
			if (b) {
				FluidStack drain = tank.drain(rem, false);
				int fill = 0;
				fill = dummy.fill(drain, true);
				ret = dummy.getContainer();

				if (fill > 0 && (DCUtil.isEmpty(ret) || inv.isItemStackable(ret, inv.getStackInSlot(slot2)) > 0)) {
					loose = true;
					tank.drain(fill, true);
				}
			}

			if (loose) {
				inv.decrStackSize(slot1, 1);
				inv.incrStackInSlot(slot2, ret);
				return true;
			}
		}
		return false;
	}

}