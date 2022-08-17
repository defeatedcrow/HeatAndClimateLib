package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.core.ClimateCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerClimateTile implements IMessageHandler<MessageClimateUpdate, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageClimateUpdate message, MessageContext ctx) {
		EntityPlayer player = ClimateCore.proxy.getPlayer();
		if (player != null) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			int clm = message.climate;
			TileEntity tile = player.world.getTileEntity(new BlockPos(x, y, z));
			if (tile instanceof IClimateUpdateTile) {
				((IClimateUpdateTile) tile).setClimate(clm);
			}
		}
		return null;
	}
}
