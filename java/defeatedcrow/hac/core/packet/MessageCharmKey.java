package defeatedcrow.hac.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCharmKey implements IMessage {

	public byte data;

	public MessageCharmKey() {
	}

	public MessageCharmKey(byte par1) {
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
