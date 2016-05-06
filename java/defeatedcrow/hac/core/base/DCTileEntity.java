package defeatedcrow.hac.core.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class DCTileEntity extends TileEntity implements ITickable {

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.getPos(), 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public NBTTagCompound getNBT(NBTTagCompound tag) {
		return tag;
	}

	public void setNBT(NBTTagCompound tag) {

	}

	// update
	public static int coolTime = 0;

	// 更新間隔
	protected int getMaxCool() {
		return 20;
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
}
