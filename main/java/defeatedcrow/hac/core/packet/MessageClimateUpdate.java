package defeatedcrow.hac.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

// ClimateTileの同期用
public class MessageClimateUpdate implements IMessage {

	public int x;
	public int y;
	public int z;
	public int climate;

	public MessageClimateUpdate() {}

	public MessageClimateUpdate(BlockPos pos, int par1) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.climate = par1;
	}

	// read
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.climate = buf.readInt();
	}

	// write
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(this.climate);
	}
}
