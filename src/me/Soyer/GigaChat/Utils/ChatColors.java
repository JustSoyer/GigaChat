package me.Soyer.GigaChat.Utils;

import org.bukkit.entity.Player;

import me.Soyer.GigaChat.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class ChatColors {
	
	private Main main = Main.getInstance();
	
	public String colors(String s, Boolean prefix) {
		if (!s.contains("<<no-prefix>>")) {
			if (main.getConfig().getBoolean("use-messages-prefix") && prefix) { s = main.getMessages().getString("messages-prefix") + s; }
		} else {
			s = s.replace("<<no-prefix>>", "");
		}
		for (int i = 0; i < s.length() - 1; i++) {
			if (s.charAt(i) == main.altColorChar || s.charAt(i) == '§') {
				Integer index = main.colors_str.indexOf(s.charAt(i + 1));
				if (index > -1) {
					if (index < main.colors_str.length() - 2) {
						s = s.substring(0, i) + "§" + Character.toLowerCase(s.charAt(i + 1)) + s.substring(i + 2);
					} else {
						if (s.substring(i + 2).length() > 5) {
							Boolean hex = true;
							for (int j = i + 2; j < i + 8; j++) {
								if (!Character.toString(s.charAt(j)).matches("[0-9a-fA-F]")) {
									i = i + (j - i) - 1;
									hex = false;
									break;
								}
							}
							if (hex) {
								s = s.substring(0, i) + "§x" + "§" + s.charAt(i + 1) + "§" + s.charAt(i + 2) + "§" + s.charAt(i + 3) + "§" + s.charAt(i + 4) + "§" + s.charAt(i + 5) + "§" + s.charAt(i + 6) + "§" + s.charAt(i + 7) + s.substring(i + 8);
								i += 15;
							}
						}
					}
				}
			}
		}
		return s;
	}

	public String tacl(Player p, String s, Boolean papi, Boolean prefix) {
		if (!s.contains("<<no-prefix>>")) { 
			if (main.getConfig().getBoolean("use-messages-prefix") && prefix) { s = main.getMessages().getString("messages-prefix") + s; }
		} else {
			s = s.replace("<<no-prefix>>", "");
		}
		for (int i = 0; i < s.length() - 1; i++) {
			if (s.charAt(i) == main.altColorChar || s.charAt(i) == '§') {
				Integer index = main.colors_str.indexOf(s.charAt(i + 1));
				if (index > -1) {
					if (index < main.colors_str.length() - 2) {
						s = s.substring(0, i) + "§" + Character.toLowerCase(s.charAt(i + 1)) + s.substring(i + 2);
					} else {
						if (s.substring(i + 2).length() > 5) {
							Boolean hex = true;
							for (int j = i + 2; j < i + 8; j++) {
								if (!Character.toString(s.charAt(j)).matches("[0-9a-fA-F]")) {
									j = j - 1;
//									i = i + (j - i) - 1;
									hex = false;
									break;
								}
							}
							if (hex) {
								s = s.substring(0, i) + "§x" + "§" + s.charAt(i + 1) + "§" + s.charAt(i + 2) + "§" + s.charAt(i + 3) + "§" + s.charAt(i + 4) + "§" + s.charAt(i + 5) + "§" + s.charAt(i + 6) + "§" + s.charAt(i + 7) + s.substring(i + 8);
								i += 15;
							}
						}
					}
				}
			}
		}
		if (main.getConfig().getBoolean("use-placeholderapi") && main.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && papi) {
			return PlaceholderAPI.setPlaceholders(p, s);
		}
		return s;
	}
	
	public String ptacl(Player p, String s, Boolean papi, String type /* "chat", "broadcast", "prefix", "suffix" */) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("chat") && !type.equalsIgnoreCase("broadcast") && !type.equalsIgnoreCase("prefix") && !type.equalsIgnoreCase("suffix")) {
			main.send("§4Report issue to the plugin author if you are reading this! Code: §cptacl_type_err§4");
			return s;
		}
		for (int i = 0; i < s.length() - 1; i++) {
			if (s.charAt(i) == main.altColorChar || s.charAt(i) == '§') {
				Integer index = main.colors_str.indexOf(s.charAt(i + 1));
				if (index > -1) {
					if (index < main.colors_str.length() - 2) {
						if (isPermitted(p, "gigachat." + type + ".colorcode." + s.charAt(i + 1)) || isPermitted(p, "gigachat." + type + ".colorcode." + ChatColor.getByChar(s.toLowerCase().charAt(i + 1)).getName().toLowerCase())) { /* Mojang - уебаны! */
							s = s.substring(0, i) + "§" + Character.toLowerCase(s.charAt(i + 1)) + s.substring(i + 2);
						} else if (index < 22 && isPermitted(p, "gigachat." + type + ".colorcode.colors")) {
							s = s.substring(0, i) + "§" + Character.toLowerCase(s.charAt(i + 1)) + s.substring(i + 2);
						} else if (index >= 22 && isPermitted(p, "gigachat." + type + ".colorcode.formats")) {
							s = s.substring(0, i) + "§" + Character.toLowerCase(s.charAt(i + 1)) + s.substring(i + 2);
						}
					} else {
						if (isPermitted(p, "gigachat." + type + ".colorcode.hex")) {
							if (s.substring(i + 2).length() > 5) {
								Boolean hex = true;
								for (int j = i + 2; j < i + 8; j++) {
									if (!Character.toString(s.charAt(j)).matches("[0-9a-fA-F]")) {
										i = j - 1;
//										i = i + (j - i) - 1;
										hex = false;
										break;
									}
								}
								if (hex) {
									s = s.substring(0, i) + "§x" + "§" + s.charAt(i + 1) + "§" + s.charAt(i + 2) + "§" + s.charAt(i + 3) + "§" + s.charAt(i + 4) + "§" + s.charAt(i + 5) + "§" + s.charAt(i + 6) + "§" + s.charAt(i + 7) + s.substring(i + 8);
									i += 15;
								}
							}
						}
					}
				}
			}
		}
		if (main.getConfig().getBoolean("use-placeholderapi") && main.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && papi) {
			return PlaceholderAPI.setPlaceholders(p, s);
		}
		return s;
	}
	

	private boolean isPermitted(Player p, String perm) {
		if (p.hasPermission("gigachat.admin") || p.hasPermission(perm)) {
			return true;
		} else {
			return false;
		}
	}

}
