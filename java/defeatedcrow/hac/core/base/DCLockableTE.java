package defeatedcrow.hac.core.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class DCLockableTE extends TileEntity implements IInteractionObject, ILockableContainer, ITagGetter, ITickable {

	private LockCode code = LockCode.EMPTY_CODE;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.code = LockCode.fromNBT(compound);
		this.coolTime = compound.getByte("CoolTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (this.code != null) {
			this.code.toNBT(compound);
		}
		compound.setByte("CoolTime", (byte) coolTime);
	}

	@Override
	public boolean isLocked() {
		return this.code != null && !this.code.isEmpty();
	}

	@Override
	public LockCode getLockCode() {
		return this.code;
	}

	@Override
	public void setLockCode(LockCode code) {
		this.code = code;
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(this.getName());
	}

	private IItemHandler itemHandler;

	protected IItemHandler createUnSidedHandler() {
		return new InvWrapper(this);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {

	}

	// update
	public int coolTime = 0;

	// 更新間隔
	protected int getMaxCool() {
		return 10;
	}

	@Override
	public void update() {
		if (coolTime < 0) {
			coolTime--;
		} else {
			updateTile();
			coolTime = getMaxCool();
		}

		onTickUpdate();

		if (!worldObj.isRemote) {
			onServerUpdate();
		}
	}

	public void onTickUpdate() {
	}

	protected void onServerUpdate() {
	}

	public void updateTile() {
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

}
