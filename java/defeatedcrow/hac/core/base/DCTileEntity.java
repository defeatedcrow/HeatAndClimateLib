package defeatedcrow.hac.core.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class DCTileEntity extends TileEntity implements ITickable {

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.coolTime = compound.getByte("CoolTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByte("CoolTime", (byte) coolTime);
		return compound;
	}

	public NBTTagCompound getNBT(NBTTagCompound tag) {
		return tag;
	}

	public void setNBT(NBTTagCompound tag) {

	}

	// update
	public int coolTime = 20;

	// 更新間隔
	protected int getMaxCool() {
		return 20;
	}

	@Override
	public void update() {
		if (coolTime > 0) {
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

	public void onTickUpdate() {}

	protected void onServerUpdate() {}

	public void updateTile() {}
}
