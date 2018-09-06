package defeatedcrow.hac.core.packet;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

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
						"\u00a7c3./climate drought cansel: you can cancel the drought in the overworld"));
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
				if (season == null) {
					sender.sendMessage(new TextComponentTranslation("\u00a7bCleared HaC forced season"));
				} else {
					sender.sendMessage(new TextComponentTranslation("\u00a7bSet HaC forced season: " + season));
				}
			} else if (args[0].equalsIgnoreCase("drought")) {
				if (args.length > 1 && args[1].equalsIgnoreCase("cancel")) {
					WeatherChecker.INSTANCE.sunCountMap.put(0, 0);
					sender.sendMessage(new TextComponentTranslation("\u00a7bSet HaC canceled the drought"));
				} else {
					WeatherChecker.INSTANCE.sunCountMap.put(0, CoreConfigDC.droughtFrequency * 24 + 1);
					sender.sendMessage(new TextComponentTranslation("\u00a7bSet HaC forced drought"));
				}

			} else {
				sender.sendMessage(new TextComponentTranslation(
						"\u00a7c/climate help: you can see the help of HeatAndClimate command."));
			}
		} else {
			sender.sendMessage(new TextComponentTranslation(
					"\u00a7c/climate help: you can see the help of HeatAndClimate command."));
		}

	}

}
