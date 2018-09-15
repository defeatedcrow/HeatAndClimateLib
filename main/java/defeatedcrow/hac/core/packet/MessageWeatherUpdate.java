package defeatedcrow.hac.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

// 天候変更時の通知
public class MessageWeatherUpdate implements IMessage {

	public int dim;
	public float rain;
	public int rainCount;
	public int sunCount;

	public MessageWeatherUpdate() {}

	public MessageWeatherUpdate(int dimention, float rainF, int countR, int countS) {
		this.dim = dimention;
		this.rain = rainF;
		this.rainCount = countR;
		this.sunCount = countS;
	}

	// read
	@Override
	public void fromBytes(ByteBuf buf) {
		this.dim = buf.readInt();
		this.rain = buf.readFloat();
		this.rainCount = buf.readInt();
		this.sunCount = buf.readInt();
	}

	// write
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dim);
		buf.writeFloat(rain);
		buf.writeInt(rainCount);
		buf.writeInt(sunCount);
	}
}
