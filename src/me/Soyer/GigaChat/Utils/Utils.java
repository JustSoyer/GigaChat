package me.Soyer.GigaChat.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.Soyer.GigaChat.Main;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	
	private Main main;
	private ChatColors chatColors = new ChatColors();
	private Lists lists = new Lists();

	public Utils(Main main) {
		this.main = main;
	}
	
	// Does not contain DEFAULT and HEX.
	public String[] colors = { "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY", "DARK_GRAY", "BLUE", "BLACK", "GREEN", "AQUA", "RED", "LIGHT_PURPLE", "YELLOW", "WHITE" };
	// Does not contain RESET (&r).
	public String[] formats = { "BOLD", "STRIKETHROUGH", "UNDERLINE", "ITALIC", "MAGIC" };
//	// Does not contain HEX symbol.
//	public char[] symbols = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F', 'k', 'l', 'm', 'n', 'o', 'r', 'K', 'L', 'M', 'N', 'O', 'R' };
	
	
	// Coloring any message anywhere.
	public String justColorize(String s, String color) {
		if (Arrays.asList(colors).contains(color.toUpperCase())) {
			return ChatColor.of(color.toUpperCase()) + s;
		} else {
			main.send("§cWrong justColorize() color name: §f" + color);
			return s;
		}
	}

	// Coloring messages and nicknames in the chat.
	public String colorize(String s, Player p, String type /* "chat" or "nick" */, Boolean perm) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("chat") && !type.equalsIgnoreCase("nick")) {
			main.send("§4Report issue to the plugin author if you are reading this! Code: §ccolorize_type_err§4");
			return s;
		}
		String result = s;
		String record = saveType((OfflinePlayer)p);
		if (!Arrays.asList(colors).contains(main.getDatabase().getString("players." + record + "." + type + ".color").toUpperCase())) {
			main.send("§cWrong " + p.getName() + "'s " + type + " color: §f" + main.getDatabase().getString("players." + record + "." + type + ".color").toUpperCase());
			main.send("§cCheck the \".chat\" and \".nick\" (" + record + ") in your database.yml.");
			main.send("§eIf the problem persists, write to the plugin author.");
			return result;
		}
		if (perm) {
			if (main.getDatabase().getString("players." + record + "." + type + ".color").equalsIgnoreCase(main.defaultColor.get(type)) || isPermitted(p, "gigachat.command." + type + "color.color." + main.getDatabase().getString("players." + record + "." + type + ".color").toLowerCase()) || isPermitted(p, "gigachat.colorize." + type + "." + main.getDatabase().getString("players." + record + "." + type + ".color").toLowerCase())) {
				result = ChatColor.of(main.getDatabase().getString("players." + record + "." + type + ".color")) + result;
			} else {
				String action = main.getConfig().getString("no-colorize-permission-action");
				if (action.equalsIgnoreCase("forced")) {
					result = ChatColor.of(main.getDatabase().getString("players." + record + "." + type + ".color")) + result;
				} else if (Arrays.asList(colors).contains(action.toUpperCase())) {
					result = ChatColor.of(action.toUpperCase()) + result;
				} else {
					result = ChatColor.of(main.defaultColor.get(type)) + result;
				}
			}
		} else {
			result = ChatColor.of(main.getDatabase().getString("players." + record + "." + type + ".color")) + result;
		}
		
		return result;
	}

	// Formatting any message anywhere.
	public String justFormatting(String s, List<String> list_formats) {
		String result = s;
		for (String temp_format : list_formats) {
			if (!Arrays.asList(formats).contains(temp_format.toUpperCase())) {
				list_formats.remove(temp_format);
			}
		}
		for (int i = 0; i < list_formats.size(); i++) {
			String format = list_formats.get(i).toUpperCase();
			result = ChatColor.of(format) + result;
		}
		
		return result;
	}
	
	public String formatting(String s, Player p, String type /* "chat" or "nick" */, Boolean perm) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("chat") && !type.equalsIgnoreCase("nick")) {
			main.send("§4Report issue to the plugin author if you are reading this! Code: §ccolorize_type_err§4");
			return s;
		}
		String result = s;
		String record = saveType((OfflinePlayer)p);
		List<String> list = new ArrayList<String>();
		List<String> wrong_formats = new ArrayList<String>();
		for (String format : main.getDatabase().getStringList("players." + record + "." + type + ".formatting")) {
			if (Arrays.asList(formats).contains(format.toUpperCase())) {
				if (!list.contains(format.toUpperCase())) {
					list.add(format.toUpperCase());
				}
			} else {
				wrong_formats.add(format.toUpperCase());
			}
		}
		if (!wrong_formats.isEmpty()) {
			main.send("§cWrong " + p.getName() + "'s " + type + " formatting: §f" + lists.listString(wrong_formats));
			main.send("§cCheck the \".chat\" and \".nick\" (" + record + ") in your database.yml.");
			main.send("§eIf the problem persists, write to the plugin author.");
		}
//		Boolean no_perm = false;
		if (perm) {
			for (String format : list) {
				if (!main.defaultFormatting.get(type).contains(format) && !isPermitted(p, "gigachat.command." + type + "color.format." + format.toLowerCase()) && !isPermitted(p, "gigachat.formatting." + type + "." + format.toLowerCase())) {
//					no_perm = true;
					String action = main.getConfig().getString("no-formatting-permission-action");
					if (Arrays.asList(formats).contains(action.toUpperCase())) {
						list.clear();
						list.add(action.toUpperCase());
					} else if (action.contains(",")) {
						action = action.toUpperCase();
						List<String> new_list = new ArrayList<String>();
						List<String> wrong_no_perm_formats = new ArrayList<String>();
						for (String temp_action : action.split(",")) {
							if (Arrays.asList(formats).contains(temp_action)) {
								new_list.add(temp_action);
							} else {
								wrong_no_perm_formats.add(temp_action);
							}
						}
						if (!new_list.isEmpty()) {
							list.clear();
							list = new_list;
						}
						if (!wrong_no_perm_formats.isEmpty()) {
							main.send("§cWrong formats in \"§4no-formatting-permission-action§c\": §f" + lists.listString(wrong_no_perm_formats));
						}
					} else {
						if (!action.equalsIgnoreCase("forced")) {
							list = main.defaultFormatting.get(type);
						}
					}
					break;
				}
			}
		}
//		if (no_perm) {
//			String action = main.getConfig().getString("no-formatting-permission-action");
//			if (Arrays.asList(formats).contains(action.toUpperCase())) {
//				list.clear();
//				list.add(action.toUpperCase());
//			} else if (action.contains(",")) {
//				action = action.toUpperCase();
//				List<String> new_list = new ArrayList<String>();
//				List<String> wrong_no_perm_formats = new ArrayList<String>();
//				for (String temp_action : action.split(",")) {
//					if (Arrays.asList(formats).contains(temp_action)) {
//						new_list.add(temp_action);
//					} else {
//						wrong_no_perm_formats.add(temp_action);
//					}
//				}
//				if (!new_list.isEmpty()) {
//					list.clear();
//					list = new_list;
//				}
//				if (!wrong_no_perm_formats.isEmpty()) {
//					main.send("§cWrong formats in \"§4no-formatting-permission-action§c\": §f" + lists.listString(wrong_no_perm_formats));
//				}
//			} else {
//				if (!action.equalsIgnoreCase("forced")) {
//					list = main.defaultFormatting.get(type);
//				}
//			}
//		}
		for (String format : list) {
			if (!format.equalsIgnoreCase("MAGIC")) {
				result = ChatColor.of(format.toUpperCase()) + result;
			} else {
				result = ChatColor.MAGIC + result;
			}
		}
		
		return result;
	}

	public String uncolorize(String s, Boolean b) {
		String new_temp = "";
		if (b) {
			for (int i = 0; i < s.length(); i++) {
				if ((s.charAt(i) != main.altColorChar && s.charAt(i) != '§')) {
					new_temp += s.charAt(i);
				} else {
					if (i == s.length() - 1) {
						new_temp += s.charAt(i);
					} else {
						Integer index = main.colors_str.indexOf(s.charAt(i + 1));
						if (index > -1) {
							if (index < main.colors_str.length() - 2) {
								i += 1;
							} else {
								if (s.substring(i + 2).length() > 5) {
									i += 2;
									int j = i;
									for (; j < i + 6; j++) {
										if (!Character.toString(s.charAt(j)).matches("[0-9a-fA-F]")) {
											new_temp += s.substring(i - 2, j);
											break;
										}
									}
									i += j - i - 1;
								} else {
									new_temp += s.substring(i);
								}
							}
						} else {
							new_temp += s.charAt(i);
						}
					}
				}
			}
		} else {
			new_temp = s;
		}
		return new_temp;
	}
	
	public String pUncolorize(Player p, String s, Boolean b, String type /* "chat", "broadcast", "prefix", "suffix" */) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("chat") && !type.equalsIgnoreCase("broadcast") && !type.equalsIgnoreCase("prefix") && !type.equalsIgnoreCase("suffix")) {
//			p.sendMessage(chatColors.colors("§cSomething went wrong. Notify the administration!", true));
			main.send("§4Report issue to the plugin author if you are reading this! Code: §cchatc_nickc_cmd_name_err§4");
			return s;
		}
		String new_temp = "";
		if (b) {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) != main.altColorChar && s.charAt(i) != '§') {
					new_temp += s.charAt(i);
				} else {
					if (i == s.length() - 1) {
						new_temp += s.charAt(i);
					} else {
						Integer index = main.colors_str.indexOf(s.charAt(i + 1));
						if (index > -1) {
							if (index < main.colors_str.length() - 2) {
								if (isPermitted(p, "gigachat." + type + ".colorcode." + s.toLowerCase().charAt(i + 1)) || isPermitted(p, "gigachat." + type + ".colorcode." + ChatColor.getByChar(s.toLowerCase().charAt(i + 1)).getName().toLowerCase())) { /* Mojang - уебаны! */
									i += 1;
								} else if (index < 22 && isPermitted(p, "gigachat." + type + ".colorcode.colors")) {
									i += 1;
								} else if (index >= 22 && isPermitted(p, "gigachat." + type + ".colorcode.formats")) {
									i += 1;
								}
							} else {
								if (s.substring(i + 2).length() > 5 && isPermitted(p, "gigachat." + type + ".colorcode.hex")) {
									i += 2;
									int j = i;
									for (; j < i + 6; j++) {
										if (!Character.toString(s.charAt(j)).matches("[0-9a-fA-F]")) {
											new_temp += s.substring(i - 2, j);
											break;
										}
									}
									i += j - i - 1;
								} else {
									new_temp += s.substring(i);
								}
							}
						} else {
							new_temp += s.charAt(i);
						}
					}
				}
			}
		} else {
			new_temp = s;
		}
		return new_temp;
	}

	public void loginMotd(Player p, String type /* "info", "motd" */) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("info") && !type.equalsIgnoreCase("motd")) {
			main.send("§4Report issue to the plugin author if you are reading this! Code: §cloginMotd_type_err§4");
			return;
		}
		String record = saveType((OfflinePlayer)p);
		String prefix_status = main.getConfig().getString("not-set");
		String suffix_status = main.getConfig().getString("not-set");
		if (!main.getDatabase().getString("players." + record + ".prefix").isBlank()) { prefix_status = chatColors.tacl(p, main.getDatabase().getString("players." + record + ".prefix"), false, false); }
		if (!main.getDatabase().getString("players." + record + ".suffix").isBlank()) { suffix_status = chatColors.tacl(p, main.getDatabase().getString("players." + record + ".suffix"), false, false); }
		String playername = p.getName();
		if (main.getConfig().getBoolean("playername-design")) { colorize(formatting(p.getName(), p, "nick", true), p, "nick", true); }
		if (type.equalsIgnoreCase("info")) {
			String chat_list = lists.listOfActiveChats("chats");
			String chat_names = lists.listOfActiveChats("chat_names");
			String is_papi_enabled = main.getConfig().getString("disabled");
			String is_vault_enabled = main.getConfig().getString("disabled");
			if (main.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && main.getConfig().getBoolean("use-placeholderapi")) { is_papi_enabled = main.getConfig().getString("enabled"); }
			if (main.getServer().getPluginManager().isPluginEnabled("Vault") && main.getConfig().getBoolean("use-vault")) { is_vault_enabled = main.getConfig().getString("enabled"); }
			for (String text : main.getConfig().getStringList("plugin-info")) {
				p.sendMessage(chatColors.tacl(p, text.replace("{player}", playername)
						.replace("{chat_list}", chat_list)
						.replace("{chat_names}", chat_names)
						.replace("{is_papi_enabled}", is_papi_enabled)
						.replace("{is_vault_enabled}", is_vault_enabled)
						.replace("{prefix_with_status}", prefix_status)
						.replace("{suffix_with_status}", suffix_status)
						.replace("{likes_count}", main.getDatabase().getInt("players." + record + ".likes.likes-count") + "")
						.replace("{liked_count}", main.getDatabase().getInt("players." + record + ".likes.liked-count") + ""), true, false)
						.replace("{prefix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".prefix"), false, "prefix"))
						.replace("{suffix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".suffix"), false, "suffix")));
			}
		}
		if (type.equalsIgnoreCase("motd")) {
			for (String text : main.getConfig().getStringList("motd")) {
				p.sendMessage(chatColors.tacl(p, text.replace("{player}", playername)
						.replace("{likes_count}", main.getDatabase().getInt("players." + record + ".likes.likes-count") + "")
						.replace("{liked_count}", main.getDatabase().getInt("players." + record + ".likes.liked-count") + ""), true, false)
						.replace("{prefix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".prefix"), false, "prefix"))
						.replace("{suffix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".suffix"), false, "suffix"))
						.replace("{prefix_with_status}", chatColors.ptacl(p, prefix_status, false, "prefix"))
						.replace("{suffix_with_status}", chatColors.ptacl(p, suffix_status, false, "suffix")));
			}
		}
	}
	
	public boolean isPermitted(Player p, String perm) {
		if (p.hasPermission("gigachat.admin") || p.hasPermission(perm)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isNotPermitted(Player p, String perm) {
		if (!p.hasPermission("gigachat.admin") && !p.hasPermission(perm)) {
			p.sendMessage(chatColors.colors(main.getMessages().getString("no-permission"), true));
			return true;
		} else {
			return false;
		}
	}
	
	public void colorMessage(Player p, String message) {
		p.sendMessage(chatColors.colors(message, true));
	}
	
	public String saveType(OfflinePlayer off_p) {
		if (main.getConfig().getString("database-type").equalsIgnoreCase("name")) {
			return off_p.getName().toLowerCase();
		} else {
			return off_p.getUniqueId().toString();
		}
	}
	
	public void checkDatabase(OfflinePlayer off_p) {
		String record = saveType(off_p);
		if (!main.getDatabase().contains("players." + record)) {
			main.getDatabase().set("players." + record + ".chat.color", main.defaultColor.get("chat"));
			main.getDatabase().set("players." + record + ".chat.formatting", main.defaultFormatting.get("chat"));
			main.getDatabase().set("players." + record + ".nick.color", main.defaultColor.get("nick"));
			main.getDatabase().set("players." + record + ".nick.formatting", main.defaultFormatting.get("nick"));
			main.getDatabase().set("players." + record + ".prefix", main.getConfig().getString("default-prefix"));
			main.getDatabase().set("players." + record + ".suffix", main.getConfig().getString("default-suffix"));
			main.getDatabase().set("players." + record + ".visibility.colors", main.defaultVisibility.get("colors"));
			main.getDatabase().set("players." + record + ".visibility.prefix", main.defaultVisibility.get("prefix"));
			main.getDatabase().set("players." + record + ".visibility.suffix", main.defaultVisibility.get("suffix"));
			main.getDatabase().set("players." + record + ".visibility.likes", main.defaultVisibility.get("likes"));
			main.getDatabase().set("players." + record + ".visibility.motd", main.defaultVisibility.get("motd"));
			main.getDatabase().set("players." + record + ".likes.liked-count", 0);
			main.getDatabase().set("players." + record + ".likes.liked-players", new ArrayList<String>());
			main.getDatabase().set("players." + record + ".likes.likes-count", 0);
			main.saveDatabase();
		} else {
			Boolean changed = false;
			if (!main.getDatabase().contains("players." + record + ".chat.color")) {
				main.getDatabase().set("players." + record + ".chat.color", main.defaultColor.get("chat"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".chat.formatting")) {
				main.getDatabase().set("players." + record + ".chat.formatting", main.defaultFormatting.get("chat"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".nick.color")) {
				main.getDatabase().set("players." + record + ".nick.color", main.defaultColor.get("nick"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".nick.formatting")) {
				main.getDatabase().set("players." + record + ".nick.formatting", main.defaultFormatting.get("nick"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".prefix")) {
				main.getDatabase().set("players." + record + ".prefix", main.getConfig().getString("default-prefix"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".suffix")) {
				main.getDatabase().set("players." + record + ".suffix", main.getConfig().getString("default-suffix"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".visibility.colors")) {
				main.getDatabase().set("players." + record + ".visibility.colors", main.defaultVisibility.get("colors"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".visibility.prefix")) {
				main.getDatabase().set("players." + record + ".visibility.prefix", main.defaultVisibility.get("prefix"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".visibility.suffix")) {
				main.getDatabase().set("players." + record + ".visibility.suffix", main.defaultVisibility.get("suffix"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".visibility.likes")) {
				main.getDatabase().set("players." + record + ".visibility.likes", main.defaultVisibility.get("likes"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".visibility.motd")) {
				main.getDatabase().set("players." + record + ".visibility.motd", main.defaultVisibility.get("motd"));
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".likes.liked-count")) {
				main.getDatabase().set("players." + record + ".likes.liked-count", 0);
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".likes.liked-players")) {
				main.getDatabase().set("players." + record + ".likes.liked-players", new ArrayList<String>());
				changed = true;
			}
			if (!main.getDatabase().contains("players." + record + ".likes.likes-count")) {
				main.getDatabase().set("players." + record + ".likes.likes-count", 0);
				changed = true;
			}
			if (changed) {
				main.saveDatabase();
			}
			/*if (!main.getDatabase().contains("players." + record + ".")) {
				main.getDatabase().set("players." + record + ".", );
			}*/
		}
	}
	
}
