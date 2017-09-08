package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.energy.TileTorqueBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerTorqueTile implements IMessageHandler<MessageTorqueTile, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageTorqueTile message, MessageContext ctx) {
		EntityPlayer player = ClimateCore.proxy.getPlayer();
		if (player != null) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			float torque = message.torque;
			float accel = message.accel;
			float accel2 = message.accel2;
			TileEntity tile = player.worldObj.getTileEntity(new BlockPos(x, y, z));
			if (tile != null && tile instanceof TileTorqueBase) {
				((TileTorqueBase) tile).currentTorque = torque;
				((TileTorqueBase) tile).effectiveAccel = accel;
				((TileTorqueBase) tile).prevAccel = accel2;
			}
		}
		return null;
	}
}
