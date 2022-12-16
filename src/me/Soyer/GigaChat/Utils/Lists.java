package me.Soyer.GigaChat.Utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.Soyer.GigaChat.Main;
import net.md_5.bungee.api.ChatColor;

public class Lists {
	
	private Main main = Main.getInstance();

	public String listString(List<String> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				result += list.get(i) + ", ";
			} else {
				result += list.get(i);
			}
		}
		
		return result;
	}
	
	public String listFormattingString(List<String> formats) {
		Utils utils = new Utils(main);
		String formatting = "";
		for (String temp_format : formats) {
			if (!formats.contains(temp_format.toUpperCase())) {
				formats.remove(temp_format);
				main.send("§cWrong listFormattingString() format: §f" + temp_format);
			}
		}
		for (int i = 0; i < formats.size(); i++) {
			String format = formats.get(i).toUpperCase();
			if (i < formats.size() - 1) {
				if (!format.equalsIgnoreCase("MAGIC")) {
					formatting += ChatColor.of(format) + format + "§r, ";
				} else {
					formatting += format + "§r (" + ChatColor.MAGIC + "az1§r), ";
				}
			} else {
				if (!format.equalsIgnoreCase("MAGIC")) {
					formatting += ChatColor.of(format) + format + "§r";
				} else {
					formatting += format + "§r (" + ChatColor.MAGIC + "az1§r)";
				}
			}
		}
		
		return ChatColor.RESET + formatting;
	}
	
	public String listOfActiveChats(String type /* "chats" or "chat_names" */) {
		if (main.active_chats.size() > 0) {
			String[] listOfChats = main.active_chats.keySet().toArray(String[]::new);
			Arrays.sort(listOfChats);
			if (type.equalsIgnoreCase("chats")) {
					String list_of_chats = "";
					for (int i = 0; i < listOfChats.length; i++) {
						if (i < listOfChats.length - 1) {
							list_of_chats += listOfChats[i] + ", ";
						} else {
							list_of_chats += listOfChats[i];
						}
					}
					return list_of_chats;
			} else if (type.equalsIgnoreCase("chat_names")) {
				if (main.active_chats.size() > 0) {
					String list_of_chat_names = "";
					for (int i = 0; i < listOfChats.length; i++) {
						if (i < listOfChats.length - 1) {
							list_of_chat_names += main.getConfig().getString("chats." + listOfChats[i] + ".name") + ", ";
						} else {
							list_of_chat_names += main.getConfig().getString("chats." + listOfChats[i] + ".name");
						}
					}
					return list_of_chat_names;
				}
			} else {
				main.send("§cSomewhere the Type for the listOfActiveChats(...) function is incorrectly set.");
				main.send("§ePlease report this issue to the author.");
			}
		} else {
			return main.getMessages().getString("no-chat-found");
		}
		return null;
	}
	
	public String listOfChatWorlds(String chat) {
		if (main.active_chats.containsKey(chat)) {
			String list_of_chat_worlds = "";
			if (main.active_chats.get(chat).size() > 0) {
				List<World> listOfChatWorlds = main.active_chats.get(chat);
				for (int i = 0; i < listOfChatWorlds.size(); i++) {
					if (Bukkit.getWorld(listOfChatWorlds.get(i).getName()) != null) {
						if (i < listOfChatWorlds.size() - 1) {
							list_of_chat_worlds += listOfChatWorlds.get(i).getName() + ", ";
						} else {
							list_of_chat_worlds += listOfChatWorlds.get(i).getName();
						}
					} else {
						main.send("§cFor some reason the world \"§4" + listOfChatWorlds.get(i).getName() + "§c\" is not available");
					}
				}
				if (list_of_chat_worlds.isBlank()) { list_of_chat_worlds = main.getMessages().getString("no-worlds-found"); }
				return list_of_chat_worlds;
			} else {
				return main.getMessages().getString("no-world-found");
			}
		} else {
			main.send("§cChat \"" + chat + "\" was not found among active chats.");
			return null;
		}
	}
	
	public String colorsAndFormatting(String name /* "chat" or "nick" */, String type /* "colors" or "formatting" */) {
		Utils utils = new Utils(main);
		String list = "";
		if (type.equalsIgnoreCase("colors")) {
			list = ChatColor.of(main.defaultColor.get(name)) + "DEFAULT§r, ";
			for (int i = 0; i < utils.colors.length; i++) {
				if (i < utils.colors.length - 1) {
					list += ChatColor.of(utils.colors[i]) + utils.colors[i] + "§r, ";
				} else {
					list += ChatColor.of(utils.colors[i]) + utils.colors[i] + "§r";
				}
			}
		} else if (type.equalsIgnoreCase("formatting")) {
			list = "DEFAULT";
			for (String default_format : main.defaultFormatting.get(name)) {
				if (!default_format.equalsIgnoreCase("MAGIC")) {
					list = ChatColor.of(default_format) + list;
				} else {
					list = list + "§r (" + ChatColor.MAGIC + "az1§r)";
				}
			}
			list += "§r, ";
			for (int j = 0; j < utils.formats.length; j++) {
				if (j < utils.formats.length - 1) {
					if (!utils.formats[j].equalsIgnoreCase("MAGIC")) {
						list += ChatColor.of(utils.formats[j]) + utils.formats[j] + "§r, ";									
					} else {
						list += utils.formats[j] + "§r (" + ChatColor.MAGIC + "az1§r), ";  
					}
				} else {
					if (!utils.formats[j].equalsIgnoreCase("MAGIC")) {
						list += ChatColor.of(utils.formats[j]) + utils.formats[j] + "§r";									
					} else {
						list += utils.formats[j] + "§r (" + ChatColor.MAGIC + "az1§r)";  
					}
				}
			}
		} else {
			main.send("§cSomewhere the Type for the colorsAndFormatting(...) function is incorrectly set.");
			main.send("§ePlease report this issue to the author.");
		}
		
		return ChatColor.RESET + list;
	}
	
	public String listOfPlayerFormats(Player p, List<String> formats) {
		Utils utils = new Utils(main);
		String record = utils.saveType((OfflinePlayer)p);
		String formatting = "";
		for (String temp_format : formats) {
			if (!Arrays.asList(utils.formats).contains(temp_format)) {
				formats.remove(temp_format);
				main.send("§cWrong " + p.getName() + "'s chat or nick format:§f " + temp_format);
				main.send("§cCheck the \".chat\" and \".nick\" (" + record + ") in your database.yml.");
				main.send("§eIf the problem persists, write to the plugin author.");
			}
		}
		for (int i = 0; i < formats.size(); i++) {
			String format = formats.get(i).toUpperCase();
			if (i < formats.size() - 1) {
				if (!format.equalsIgnoreCase("MAGIC")) {
					formatting += ChatColor.of(format) + format + "§r, ";
				} else {
					formatting += format + "§r (" + ChatColor.MAGIC + "az1§r), ";
				}
			} else {
				if (!format.equalsIgnoreCase("MAGIC")) {
					formatting += ChatColor.of(format) + format + "§r";
				} else {
					formatting += format + "§r (" + ChatColor.MAGIC + "az1§r)";
				}
			}
		}
		
		return ChatColor.RESET + formatting + ChatColor.RESET;
	}
	
}
