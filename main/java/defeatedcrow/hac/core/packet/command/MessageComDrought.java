package defeatedcrow.hac.core.packet.command;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

// season
public class MessageComDrought implements IMessage {

	public byte data;

	public MessageComDrought() {}

	public MessageComDrought(byte par1) {
		this.data = par1;
	}

	// read
	@Override
	public void fromBytes(ByteBuf buf) {
		this.data = buf.readByte();
	}

	// write
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(this.data);
	}
}
