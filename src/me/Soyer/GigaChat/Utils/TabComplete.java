package me.Soyer.GigaChat.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.Soyer.GigaChat.Main;

public class TabComplete implements TabCompleter, Listener {
	
	private Main main;
	
	public TabComplete(Main main) {
		main.getServer().getPluginManager().registerEvents(this,  main);
		this.main = main;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		Utils utils = new Utils(main);
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("gigachat")) {
				List<String> firstArg = new ArrayList<String>();
				if (args.length == 1) {
					if ("help".startsWith(args[0].toLowerCase())) { firstArg.add("help"); }
					if ("reload".startsWith(args[0].toLowerCase())) { firstArg.add("reload"); }
					if ("getchats".startsWith(args[0].toLowerCase())) { firstArg.add("getchats"); }
					if ("info".startsWith(args[0].toLowerCase())) { firstArg.add("info"); }
					if ("getworlds".startsWith(args[0].toLowerCase())) { firstArg.add("getworlds"); }
					if ("getchatname".startsWith(args[0].toLowerCase())) { firstArg.add("getchatname"); }
					if ("getstart".startsWith(args[0].toLowerCase())) { firstArg.add("getstart"); }
					return firstArg;
				} else if (args.length == 2) {
					List<String> secondArg = new ArrayList<String>();
					if (args[0].equalsIgnoreCase("getworlds") || args[0].equalsIgnoreCase("getchatname") || args[0].equalsIgnoreCase("getstart")) {
						for (String chat : main.active_chats.keySet()) {
							secondArg.add(chat);
						}
					}
					return secondArg;
				} else {
					return null;
				}
			}
			if (cmd.getName().equalsIgnoreCase("chatcolor") || cmd.getName().equalsIgnoreCase("nickcolor")) {
				List<String> firstArg = new ArrayList<String>();
				switch (args.length) {
				case 1:
					if ("help".startsWith(args[0].toLowerCase())) { firstArg.add("help"); }
					if ("list".startsWith(args[0].toLowerCase())) { firstArg.add("list"); }
					if ("set".startsWith(args[0].toLowerCase())) { firstArg.add("set"); }
					return firstArg;
				case 2:
					List<String> secondArg = new ArrayList<String>();
					if (Arrays.asList(utils.colors).contains(args[0].toUpperCase()) || args[0].equalsIgnoreCase("default")) {
						if (args[1].length() == 0) { secondArg.add("*"); }
						if ("ALL".startsWith(args[1].toUpperCase())) { secondArg.add("ALL"); }
						if ("DEFAULT".startsWith(args[1].toUpperCase())) { secondArg.add("DEFAULT"); }
						if ("CLEAR".startsWith(args[1].toUpperCase())) { secondArg.add("CLEAR"); }
						for (String format : utils.formats) {
							if (format.startsWith(args[1].toUpperCase())) { secondArg.add(format); }
						}
					} else if (args[0].equalsIgnoreCase("list")) {
						if ("colors".startsWith(args[1].toLowerCase())) { secondArg.add("colors"); }
						if ("formats".startsWith(args[1].toLowerCase())) { secondArg.add("formats"); }
					} else if (args[0].equalsIgnoreCase("set")) {
						if ("color".startsWith(args[1].toLowerCase())) { secondArg.add("color"); }
						if ("formatting".startsWith(args[1].toLowerCase())) { secondArg.add("formatting"); }
					} else {
						return new ArrayList<>();
					}
					return secondArg;
				case 3:
					List<String> thirdArg = new ArrayList<String>();
					if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("color")) {
						if ("DEFAULT".startsWith(args[2].toUpperCase())) { thirdArg.add("DEFAULT"); }
						for (String color : utils.colors) {
							if (color.startsWith(args[2].toUpperCase())) { thirdArg.add(color); }
						}
					} else if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("formatting")) {
						if (args[2].length() == 0) thirdArg.add("*");
						if ("ALL".startsWith(args[2].toUpperCase())) { thirdArg.add("ALL"); }
						if ("DEFAULT".startsWith(args[2].toUpperCase())) { thirdArg.add("DEFAULT"); }
						if ("CLEAR".startsWith(args[2].toUpperCase())) { thirdArg.add("CLEAR"); }
						for (String format : utils.formats) {
							if (format.startsWith(args[2].toUpperCase())) { thirdArg.add(format); }
						}
					} else {
						return new ArrayList<>();
					}
					return thirdArg;
				default:
					return new ArrayList<>();
				}
			}
			if (cmd.getName().equalsIgnoreCase("prefix") || cmd.getName().equalsIgnoreCase("suffix")) {
				List<String> arg = new ArrayList<String>();
				if (args.length == 1) {
					if ("set".startsWith(args[0].toLowerCase())) { arg.add("set"); }
					if ("help".startsWith(args[0].toLowerCase())) { arg.add("help"); }
					if ("remove".startsWith(args[0].toLowerCase())) { arg.add("remove"); }
					if ("default".startsWith(args[0].toLowerCase())) { arg.add("default"); }
					return arg;
				} else {
					return null;
				}
			}
			if (cmd.getName().equalsIgnoreCase("visibility")) {
				List<String> firstArg = new ArrayList<String>();
				if (args.length == 1) {
					if ("colors".startsWith(args[0].toLowerCase())) { firstArg.add("colors"); }
					if ("prefix".startsWith(args[0].toLowerCase())) { firstArg.add("prefix"); }
					if ("suffix".startsWith(args[0].toLowerCase())) { firstArg.add("suffix"); }
					if ("likes".startsWith(args[0].toLowerCase())) { firstArg.add("likes"); }
					if ("motd".startsWith(args[0].toLowerCase())) { firstArg.add("motd"); }
					if ("help".startsWith(args[0].toLowerCase())) { firstArg.add("help"); }
					if ("list".startsWith(args[0].toLowerCase())) { firstArg.add("list"); }
					return firstArg;
				} else if (args.length == 2) {
					List<String> secondArg = new ArrayList<String>();
					if (args[0].equalsIgnoreCase("colors") || args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("suffix") || args[0].equalsIgnoreCase("likes") || args[0].equalsIgnoreCase("motd")) {
						if ("help".startsWith(args[1].toLowerCase())) { secondArg.add("help"); }
						if ("true".startsWith(args[1].toLowerCase())) { secondArg.add("true"); }
						if ("false".startsWith(args[1].toLowerCase())) { secondArg.add("false"); }
						return secondArg;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
			if (cmd.getName().equalsIgnoreCase("broadcast")) {
				List<String> arg = new ArrayList<String>();
				if (args.length == 1) {
					if ("chat".startsWith(args[0].toLowerCase())) { arg.add("chat"); }
					if ("actionbar".startsWith(args[0].toLowerCase())) { arg.add("actionbar"); }
					if ("title".startsWith(args[0].toLowerCase())) { arg.add("title"); }
					if ("help".startsWith(args[0].toLowerCase())) { arg.add("help"); }
					return arg;
				} else {
					return null;
				}
			}
			if (cmd.getName().equalsIgnoreCase("dislike")) {
				String record = utils.saveType((OfflinePlayer)sender);
				List<String> arg = new ArrayList<String>();
				if (args.length == 1) {
					for (String name : main.getDatabase().getStringList("players." + record + ".likes.liked-players")) {
						if (name.toLowerCase().startsWith(args[0].toLowerCase())) { arg.add(name); }
					}
					return arg;
				} else {
					return null;
				}
			}
			
			return null;
		}
		return null;
	}
}
