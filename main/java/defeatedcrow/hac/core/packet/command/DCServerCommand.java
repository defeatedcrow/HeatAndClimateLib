package defeatedcrow.hac.core.packet.command;

import java.io.File;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.config.ClimateConfig;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.plugin.main.MainComHelper;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;

public class DCServerCommand extends CommandBase {

	@Override
	public String getName() {
		return "climate";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/climate <action> <params>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(new TextComponentTranslation("\u00a7c*** HeatAndClimate command help ***"));
				sender.sendMessage(
						new TextComponentTranslation("\u00a7c1./climate season <params>: you can forced the season"));
				sender.sendMessage(
						new TextComponentTranslation("\u00a7c -The forced season will be cleared by restarting."));
				sender.sendMessage(new TextComponentTranslation(
						"\u00a7c -The following strings can be used for <params>: spring, summer, autumn, winter"));
				sender.sendMessage(new TextComponentTranslation("\u00a7c -Other strings: Cancel the forced season."));
				sender.sendMessage(new TextComponentTranslation(
						"\u00a7c2./climate drought: you can forced the drought in the overworld"));
				sender.sendMessage(new TextComponentTranslation(
						"\u00a7c3./climate drought cancel: you can cancel the drought in the overworld"));
			} else if (args[0].equalsIgnoreCase("season") && args.length > 1) {
				EnumSeason season = null;
				if (args[1].equalsIgnoreCase("spring") || args[1].equalsIgnoreCase("spr")
						|| args[1].equalsIgnoreCase("0")) {
					season = EnumSeason.SPRING;
				} else if (args[1].equalsIgnoreCase("summer") || args[1].equalsIgnoreCase("smr")
						|| args[1].equalsIgnoreCase("1")) {
					season = EnumSeason.SUMMER;
				} else if (args[1].equalsIgnoreCase("autumn") || args[1].equalsIgnoreCase("aut")
						|| args[1].equalsIgnoreCase("2")) {
					season = EnumSeason.AUTUMN;
				} else if (args[1].equalsIgnoreCase("winter") || args[1].equalsIgnoreCase("wtr")
						|| args[1].equalsIgnoreCase("3")) {
					season = EnumSeason.WINTER;
				}
				DCTimeHelper.forcedSeason = season;
				byte num = season == null ? 4 : (byte) season.id;
				HaCPacket.INSTANCE.sendToAll(new MessageComSeason(num));
				if (season == null) {
					notifyCommandListener(sender, this, "\u00a7bCleared HaC forced season");
				} else {
					notifyCommandListener(sender, this, "\u00a7bSet HaC forced season: " + season.toString());
				}
			} else if (args[0].equalsIgnoreCase("drought")) {
				if (args.length > 1 && args[1].equalsIgnoreCase("cancel")) {
					WeatherChecker.INSTANCE.sunCountMap.put(0, 0);
					notifyCommandListener(sender, this, "\u00a7bSet HaC canceled the drought", new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComDrought((byte) 0));
				} else {
					WeatherChecker.INSTANCE.sunCountMap.put(0, CoreConfigDC.droughtFrequency * 24 + 1);
					notifyCommandListener(sender, this, "\u00a7bSet HaC forced drought", new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComDrought((byte) 1));
				}

			} else if (args[0].equalsIgnoreCase("config") && args.length > 1) {
				if (args[1].contains("core")) {
					File dir = new File(ClimateConfig.configDir, "core.cfg");
					CoreConfigDC.INSTANCE.load(new Configuration(dir));
					CoreConfigDC.leadBlockNames();
					notifyCommandListener(sender, this, "\u00a7b core.cfg has been reloaded.", new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComConfig((byte) 0));
				} else if (args[1].contains("armor")) {
					ArmorResistantRegister.pre();
					notifyCommandListener(sender, this, "\u00a7b armor_item_resistant.json has been reloaded.",
							new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComConfig((byte) 1));
				} else if (args[1].contains("block")) {
					HeatBlockRegister.pre();
					notifyCommandListener(sender, this, "\u00a7b block_climate_parameter.json has been reloaded.",
							new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComConfig((byte) 2));
				} else if (args[1].contains("mob")) {
					MobResistantRegister.pre();
					notifyCommandListener(sender, this, "\u00a7b mob_resistant.json has been reloaded.",
							new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComConfig((byte) 3));
				} else if (args[1].contains("main") && ClimateCore.loadedMain) {
					MainComHelper.reloadMainConfig();
					notifyCommandListener(sender, this, "\u00a7b main.cfg has been reloaded.", new Object[] {});
					HaCPacket.INSTANCE.sendToAll(new MessageComConfig((byte) 4));
				} else {
					sender.sendMessage(new TextComponentTranslation("\u00a7c This file name can not be used."));
				}
			} else {
				sender.sendMessage(new TextComponentTranslation(
						"\u00a7c/climate help: You can see the help of HeatAndClimate command."));
			}
		} else {
			sender.sendMessage(new TextComponentTranslation(
					"\u00a7c/climate help: You can see the help of HeatAndClimate command."));
		}

	}

}
