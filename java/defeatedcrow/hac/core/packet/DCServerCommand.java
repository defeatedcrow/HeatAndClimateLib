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
			if (args[0].equalsIgnoreCase("season") && args.length > 1) {
				EnumSeason season = null;
				if (args[1].equalsIgnoreCase("spring") || args[1].equalsIgnoreCase("spr")) {
					season = EnumSeason.SPRING;
				} else if (args[1].equalsIgnoreCase("summer") || args[1].equalsIgnoreCase("smr")) {
					season = EnumSeason.SUMMER;
				} else if (args[1].equalsIgnoreCase("autumn") || args[1].equalsIgnoreCase("aut")) {
					season = EnumSeason.AUTUMN;
				} else if (args[1].equalsIgnoreCase("winter") || args[1].equalsIgnoreCase("wtr")) {
					season = EnumSeason.WINTER;
				}
				DCTimeHelper.forcedSeason = season;
				if (season == null) {
					sender.sendMessage(new TextComponentTranslation("\u00a7cCleared HaC forced season"));
				} else {
					sender.sendMessage(new TextComponentTranslation("\u00a7cSet HaC forced season: " + season));
				}
			} else if (args[0].equalsIgnoreCase("drought")) {
				WeatherChecker.INSTANCE.sunCountMap.put(0, CoreConfigDC.droughtFrequency * 24 + 1);
				sender.sendMessage(new TextComponentTranslation("\u00a7cSet HaC forced drought"));
			}
		}

	}

}
