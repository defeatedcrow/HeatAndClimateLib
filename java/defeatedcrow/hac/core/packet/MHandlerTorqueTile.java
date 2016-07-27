package defeatedcrow.hac.core.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.energy.TileTorqueBase;

public class MHandlerTorqueTile implements IMessageHandler<MessageTorqueTile, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageTorqueTile message, MessageContext ctx) {
		EntityPlayer player = ClimateCore.proxy.getPlayer();
		if (player != null) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			float prev = message.prev;
			float accel = message.accel;
			float speed = message.speed;
			TileEntity tile = player.worldObj.getTileEntity(new BlockPos(x, y, z));
			if (tile != null && tile instanceof TileTorqueBase) {
				((TileTorqueBase) tile).prevAccel = prev;
				((TileTorqueBase) tile).prevSpeed = speed;
				((TileTorqueBase) tile).effectiveAccel = accel;
			}
		}
		return null;
	}
}
