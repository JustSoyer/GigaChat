package me.Soyer.GigaChat.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Soyer.GigaChat.Main;
import me.Soyer.GigaChat.Utils.ChatColors;
import me.Soyer.GigaChat.Utils.Utils;

public class JoinAndQuit implements Listener {
	
	private Main main;
	private ChatColors chatColors = new ChatColors();
	
	public JoinAndQuit(Main main) {
		main.getServer().getPluginManager().registerEvents(this,  main);
		this.main = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Utils utils = new Utils(main);
		Player p = e.getPlayer();
		String record = utils.saveType((OfflinePlayer)p);
		utils.checkDatabase((OfflinePlayer)p);
		e.setJoinMessage(null);
		if (main.getConfig().getBoolean("motd-enabled") && main.getDatabase().getBoolean("players." + record + ".visibility.motd")) {
			if (p.hasPermission("gigachat.admin") && main.getConfig().getBoolean("plugin-info-when-logging")) {
				utils.loginMotd(p, "info");
			} else if (p.hasPermission("gigachat.motd")) {
				utils.loginMotd(p, "motd");
			}
		}
		if (main.getConfig().getBoolean("join-enabled")) {
			Sound sound = null;
			if (!main.getConfig().getString("join-sound").equalsIgnoreCase("") && !main.getConfig().getString("join-sound").equalsIgnoreCase("false") && main.getConfig().getString("join-sound") != null) {
				sound = Sound.valueOf(main.getConfig().getString("join-sound").toUpperCase());
			}
			if (!main.getConfig().getString("join-message").equalsIgnoreCase("") && !(main.getConfig().getStringList("join-message").size() == 1 && main.getConfig().getStringList("join-message").get(0).isBlank()) && main.getConfig().getString("join-message") != null) {
				String playername = p.getName();
				if (main.getConfig().getBoolean("playername-design")) { utils.colorize(utils.formatting(p.getName(), p, "nick", true), p, "nick", true); }
				List<String> new_join_messages = new ArrayList<String>();
				for (String text : main.getConfig().getStringList("join-message")) {
					new_join_messages.add(chatColors.tacl(p, text.replace("{player}", playername)
							.replace("{likes_count}", main.getDatabase().getInt("players." + record + ".likes.likes-count") + "")
							.replace("{liked_count}", main.getDatabase().getInt("players." + record + ".likes.liked-count") + ""), true, false)
							.replace("{prefix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".prefix"), false, "prefix"))
							.replace("{suffix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".suffix"), false, "suffix")));
				}
				for (Player pl : Bukkit.getOnlinePlayers()) {
//					String pl_record = utils.saveType((OfflinePlayer)pl);
//					if (main.getDatabase().getBoolean("players." + pl_record + ".visibility.join-and-quit")) {
						for (String join_message : new_join_messages) {
							pl.sendMessage(join_message);
						}
						pl.playSound(pl.getLocation(), sound, 1F, 1F);
//					}
				}
			}
			if (main.getConfig().getBoolean("join-title-enabled")) {
				p.sendTitle(chatColors.tacl(p, main.getConfig().getString("join-title-settings.title"), true, false), chatColors.tacl(p, main.getConfig().getString("join-title-settings.subtitle"), true, false), main.getConfig().getInt("join-title-settings.fadeIn"), main.getConfig().getInt("join-title-settings.stay"), main.getConfig().getInt("join-title-settings.fadeOut"));
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Utils utils = new Utils(main);
		Player p = e.getPlayer();
		String record = utils.saveType((OfflinePlayer)p);
		utils.checkDatabase((OfflinePlayer)p);
		e.setQuitMessage(null);
		if (main.getConfig().getBoolean("quit-enabled")) {
			Sound sound = null;
			if (!main.getConfig().getString("quit-sound").equalsIgnoreCase("") && !main.getConfig().getString("quit-sound").equalsIgnoreCase("false") && main.getConfig().getString("quit-sound") != null) {
				sound = Sound.valueOf(main.getConfig().getString("quit-sound").toUpperCase());
			}
			if (!main.getConfig().getString("quit-message").equalsIgnoreCase("") && !(main.getConfig().getStringList("quit-message").size() == 1 && main.getConfig().getStringList("quit-message").get(0).isBlank()) && main.getConfig().getString("quit-message") != null) {
				String playername = p.getName();
				if (main.getConfig().getBoolean("playername-design")) { utils.colorize(utils.formatting(p.getName(), p, "nick", true), p, "nick", true); }
				List<String> new_quit_messages = new ArrayList<String>();
				for (String text : main.getConfig().getStringList("quit-message")) {
					new_quit_messages.add(chatColors.tacl(p, text.replace("{player}", playername)
							.replace("{likes_count}", main.getDatabase().getInt("players." + record + ".likes.likes-count") + "")
							.replace("{liked_count}", main.getDatabase().getInt("players." + record + ".likes.liked-count") + ""), true, false)
							.replace("{prefix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".prefix"), false, "prefix"))
							.replace("{suffix}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + ".suffix"), false, "suffix")));
				}
				for (Player pl : Bukkit.getOnlinePlayers()) {
//					String pl_record = utils.saveType((OfflinePlayer)pl);
//					if (main.getDatabase().getBoolean("players." + pl_record + ".visibility.join-and-quit")) {
						for (String quit_message : new_quit_messages) {
							pl.sendMessage(quit_message);
						}
						pl.playSound(pl.getLocation(), sound, 1F, 1F);
//					}
				}
			}
		}
	}
	
}
