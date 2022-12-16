package me.Soyer.GigaChat.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.Soyer.GigaChat.Main;
import me.Soyer.GigaChat.Commands.Manager;
import me.Soyer.GigaChat.Utils.ChatColors;
import me.Soyer.GigaChat.Utils.Utils;

public class Chat implements Listener {
	
	private Main main;
	private ChatColors chatColors = new ChatColors();
	
	public Chat(Main main) {
		main.getServer().getPluginManager().registerEvents(this,  main);
		this.main = main;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Utils utils = new Utils(main);
		Player p = e.getPlayer();
		String record = utils.saveType((OfflinePlayer)p);
		utils.checkDatabase((OfflinePlayer)p);
		e.setCancelled(true);
		
		String str = e.getMessage();
		String temp_str = str;
		String temp_chars = "";
		List<String> failed_to_send_empty_messages = new ArrayList<String>();

		HashMap<String, String> chats_with_starts = new HashMap<String, String>();
		List<String> actual_chats = new ArrayList<String>();
		
		for (String chat : main.active_chats_starts.keySet()) {
			if (main.active_chats_starts.get(chat).length() > 0 && str.startsWith(main.active_chats_starts.get(chat))) { actual_chats.add(chat); }
		}
		if (actual_chats.isEmpty()) {
			for (String chat : main.active_chats_starts.keySet()) {
				if (main.active_chats_starts.get(chat).isEmpty()) { actual_chats.add(chat);	}
			}
			if (actual_chats.isEmpty()) {
				utils.colorMessage(p, main.getMessages().getString("no-available-chats"));
				return;
			}
		}
		
		for (String chat : actual_chats) {
			if (!utils.isPermitted(p, "gigachat.chat.write." + chat)) { utils.colorMessage(p, main.getMessages().getString("no-permission-chat-write")); continue; }
			str = str.replace(main.active_chats_starts.get(chat), ""); // Message without start.
			
			
			
			if (!main.getConfig().getBoolean("allow-empty-messages")) {
				if (utils.pUncolorize(p, temp_str, (Boolean) main.active_chats_settings.get(chat)[4], "chat").isBlank()) {
					failed_to_send_empty_messages.add(chat);
//					if (main.getConfig().getBoolean("failed-to-send")) {
//						p.sendMessage(chatColors.colors(main.getMessages().getString("failed-to-send").replace("{chat_name}", (String) main.active_chats_settings.get(chat)[1]), true).replace("{message}", str));
//					}
//					utils.colorMessage(p, main.getMessages().getString("empty-message"));
					continue;
				}
			}
			
			
//			String format = main.getConfig().getString("chats." + chat + ".format");
//			String chat_name = main.getConfig().getString("chats." + chat + ".name");
//			String start_with = main.active_chats_starts.get(chat);
//			Integer range = -1;
//			Boolean color_codes = main.getConfig().getBoolean("color-codes");
//			Boolean capital_letter = main.getConfig().getBoolean("capital-letter");
//			if (main.getConfig().contains("chats." + chat + ".color-codes")) { color_codes = main.getConfig().getBoolean("color-codes") && main.getConfig().getBoolean("chats." + chat + ".color-codes"); }
//			if (main.getConfig().contains("chats." + chat + ".capital-letter")) { capital_letter = main.getConfig().getBoolean("capital-letter") && main.getConfig().getBoolean("chats." + chat + ".capital-letter"); }
//			if (main.getConfig().contains("chats." + chat + ".range")) { range = main.getConfig().getInt("chats." + chat + ".range"); }
			
//			if (!main.getConfig().getBoolean("allow-empty-messages")) {
//				if (utils.pUncolorize(p, temp_str, color_codes, "chat").isBlank()) {
//					if (main.getConfig().getBoolean("failed-to-send")) {
//						p.sendMessage(chatColors.colors(main.getMessages().getString("failed-to-send").replace("{chat_name}", chat_name), true).replace("{message}", str));
//					}
//					utils.colorMessage(p, main.getMessages().getString("empty-message"));
//					continue;
//				}
//			}
			
			
			
		}
		
		
		
		
//		// Сначала проверять все чаты, где есть startWith && startWith != ""
//		
////		String temp_chat = null;
////		String temp_world = null;
////		for (String chat : main.getConfig().getStringList("enabled-chats")) {
////			temp_chat = chat;
////			if (chat.contains(":")) {
////				p.sendMessage(temp_world = chat.split(":", 2)[0]);
////				p.sendMessage(temp_chat = chat.split(":", 2)[1]);
////			}
////			if (str.startsWith(main.getConfig().getString("chats." + temp_chat + ".start-with"))) {
////				
////			}
////		}
//
//		Boolean chat_color_codes = main.getConfig().getBoolean("color-codes");
//		if (main.getConfig().contains("chats." + /*chat +*/ ".color-codes")) {
//			chat_color_codes = main.getConfig().getBoolean("color-codes") && main.getConfig().getBoolean("chats." + /*chat +*/ ".color-codes");
//		} else {
//			chat_color_codes = main.getConfig().getBoolean("color-codes");
//		}
//		// Переделать! (Смотри /broadcast)
//		if (!main.getConfig().getBoolean("allow-empty-messages")) { ///Пермишины
//			if (utils.uncolorize(temp_str, chat_color_codes).isBlank()) {
//				if (main.getConfig().getBoolean("failed-to-send")) {
//					p.sendMessage(chatColors.colors(main.getMessages().getString("failed-to-send")
//							.replace("{chat_name}", main.getConfig().getString("chats." + /*chat +*/ ".name"))
//							.replace("{message}", str), true));
//				}
//				p.sendMessage(chatColors.colors(main.getMessages().getString("empty-message"), true));
//				return;
//			}
//		}
//		
//		String format = main.getConfig().getString("chats." + /*chat +*/ ".format"); // Переделать
//		Boolean bool = true;
//		while ((temp_str.charAt(0) == '&' || temp_str.charAt(0) == '§') && temp_str.length() > 2 && bool) {
//			bool = false;
////			for (char ch : utils.symbols) {
////				if (temp_str.charAt(1) == ch) {
////					temp_chars += temp_str.charAt(0) + "" + temp_str.charAt(1);
////					temp_str = temp_str.substring(2);
////					bool = true;
////					break;
////				}
////			}
//		}
//		
//		if (main.getConfig().getBoolean("capital-letter")) {
//			if (main.getConfig().contains("chats." + /*chat +*/ ".capital-letter")) {
//				str = capitalLetter(str, temp_chars, temp_str, main.getConfig().getBoolean("chats." + /*chat +*/ ".capital-letter"), chat_color_codes);
//			} else {
//				str = capitalLetter(str, temp_chars, temp_str, main.getConfig().getBoolean("capital-letter"), chat_color_codes);
//			}
//		} else {
//			str = temp_chars + temp_str;
//		}
////		if (main.getConfig().getBoolean("capital-letter")) { // Переделать 
////			if (main.getConfig().getBoolean("color-codes")) {
////				str = temp_chars + temp_str.toUpperCase().charAt(0) + temp_str.substring(1);
////			} else {
////				str = str.toUpperCase().charAt(0) + str.substring(1);
////			}
////		} else {
////			str = temp_chars + temp_str;
////		}
//		
//		if (chat_color_codes) { str = utils.tacl('&', str); } ///////////
//		String unc_str = utils.uncolorize(str, chat_color_codes);
//		
//		if (unc_str.length() < main.getConfig().getInt("min-length")) {
//			p.sendMessage(utils.tacl('&', main.getMessages().getString("too-short-message")));
//			return;
//		}
//		if (unc_str.length() > main.getConfig().getInt("max-length")) {
//			p.sendMessage(utils.tacl('&', main.getMessages().getString("too-long-message")));
//			return;
//		}
//		String colored_message = utils.colorize(str, p, "chat", true);
//		String colored_player = utils.colorize(p.getName(), p, "nick", true);
//		if (main.getDatabase().getString("players." + record + ".prefix").isBlank()) { format = format.replace("{formatted_prefix}", "").replace("{prefix}", ""); }
//		if (main.getDatabase().getString("players." + record + ".suffix").isBlank()) { format = format.replace("{formatted_suffix}", "").replace("{suffix}", ""); }
//		for (Player pl : Bukkit.getOnlinePlayers()) {
//			String temp_format = format;
//			String pl_record = utils.saveType((OfflinePlayer)pl);
//			utils.checkDatabase((OfflinePlayer)pl);
//			// {player}, {message}, {formatted_prefix}, {prefix}, {formatted_suffix}, {suffix}, {formatted_likes}, {likes_count}, {liked_count}
//			if (!main.getDatabase().getBoolean("players." + pl_record + ".visibility.prefix")) { temp_format = temp_format.replace("{formatted_prefix}", "").replace("{prefix}", ""); }
//			if (!main.getDatabase().getBoolean("players." + pl_record + ".visibility.suffix")) { temp_format = temp_format.replace("{formatted_suffix}", "").replace("{suffix}", ""); }
//			if (!main.getDatabase().getBoolean("players." + pl_record + ".visibility.likes")) { temp_format = temp_format.replace("{formatted_likes}", "").replace("{likes_count}", "").replace("{liked_count}", ""); }
//			temp_format = temp_format.replace("{player}", colored_player)
//					.replace("{formatted_prefix}", main.getConfig().getString("prefix-format"))
//					.replace("{formatted_suffix}", main.getConfig().getString("suffix-format"))
//					.replace("{formatted_likes}", main.getConfig().getString("likes-format"))
//					.replace("{prefix}", main.getDatabase().getString("players." + record + ".prefix"))
//					.replace("{suffix}", main.getDatabase().getString("players." + record + ".suffix"))
//					.replace("{likes_count}", main.getDatabase().getInt("players." + record + ".likes.likes-count") + "")
//					.replace("{liked_count}", main.getDatabase().getInt("players." + record + ".likes.liked-count") + "");
//			temp_format = utils.tacl('&', temp_format);
//			if (main.getDatabase().getBoolean("players." + pl_record + ".visibility.colors")) {
//				pl.sendMessage(temp_format.replace("{message}", colored_message));
//			} else {
//				pl.sendMessage(utils.uncolorize(temp_format, true).replace("{message}", unc_str));
//			}
//		}
	}
	
	public String pSplitString(Player p, String str, String type) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("chat") && !type.equalsIgnoreCase("broadcast") && !type.equalsIgnoreCase("prefix") && !type.equalsIgnoreCase("suffix")) {
			main.send("§4Report issue to the plugin author if you are reading this! Code: §csplitstr_type_err§4");
			return str;
		}
		Utils utils = new Utils(main);
		String temp_str = str;
		String temp_chars = "";
		while (temp_str.charAt(0) == main.altColorChar || temp_str.charAt(0) == '§') {
			Integer index = main.colors_str.indexOf(temp_str.charAt(1)); 
			if (index > -1) {
				if (index < main.colors_str.length() - 2) {
					
				} else {
					if (utils.isPermitted(p, "gigachat." + type + ".colorcode.hex")) {
						if (temp_str.substring(2).matches("[0-9a-f-A-F]{6}.*")) {
							temp_str = temp_str.substring(8);
							temp_chars += temp_str.substring(0, 8);
						}
					}
				}
			}
		}
		
//		String[] temp = {"dfs", "sdfsfd"};
		return "заглушка";
	}
	
	public String capitalLetter(String str, String temp_chars, String temp_str, Boolean bool, Boolean colors) {
		if (bool) { 
			if (colors) {
				str = temp_chars + temp_str.toUpperCase().charAt(0) + temp_str.substring(1);
			} else {
				str = str.toUpperCase().charAt(0) + str.substring(1);
			}
		}
		
		return str;
	}

}
