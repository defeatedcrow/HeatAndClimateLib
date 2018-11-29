package defeatedcrow.hac.core.packet.command;

import java.io.File;

import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.plugin.main.MainComHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MHandlerComConfig implements IMessageHandler<MessageComConfig, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageComConfig message, MessageContext ctx) {
		if (ctx != null && ctx.side == Side.CLIENT) {
			int data = message.data;
			if (data == 0) {
				File dir = new File(ClimateConfig.configDir, "core.cfg");
				CoreConfigDC.INSTANCE.load(new Configuration(dir));
				CoreConfigDC.leadBlockNames();
			} else if (data == 1) {
				ArmorResistantRegister.pre();
			} else if (data == 2) {
				HeatBlockRegister.pre();
			} else if (data == 3) {
				MobResistantRegister.pre();
			} else if (data == 4 && ClimateCore.loadedMain) {
				MainComHelper.reloadMainConfig();
			} else if (data == 5 && ClimateCore.isDebug) {
				FluidDictionaryDC.post();
			}
		}
		return null;
	}
}
