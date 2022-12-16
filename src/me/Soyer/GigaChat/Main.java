package me.Soyer.GigaChat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Charsets;

import me.Soyer.GigaChat.Commands.Manager;
import me.Soyer.GigaChat.Events.Chat;
import me.Soyer.GigaChat.Events.JoinAndQuit;
import me.Soyer.GigaChat.Events.PlayerDeath;
import me.Soyer.GigaChat.Utils.ChatColors;
import me.Soyer.GigaChat.Utils.Lists;
import me.Soyer.GigaChat.Utils.TabComplete;
import me.Soyer.GigaChat.Utils.Utils;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	private static Main instance;
	public char altColorChar = getConfig().getString("alt-color-code").charAt(0);
	public String colors_str = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr" + getConfig().getString("HEX-symbol").toUpperCase().charAt(0) + getConfig().getString("HEX-symbol").toLowerCase().charAt(0);
	public HashMap<String, List<World>> active_chats = new HashMap<String, List<World>>();
	public HashMap<String, String> active_chats_starts = new HashMap<String, String>(); // start-with
	public HashMap<String, Object[]> active_chats_settings = new HashMap<String, Object[]>(); // chats settings
//	public List<Object> active_chats_settings = new ArrayList<Object>();
//	public Object[] active_chats_settings_arr = new Object[5];
	
	public HashMap<String, String> defaultColor = new HashMap<String, String>();
	public HashMap<String, List<String>> defaultFormatting = new HashMap<String, List<String>>();
	public HashMap<String, Boolean> defaultVisibility = new HashMap<String, Boolean>();
	
//	private File configf = new File(getDataFolder(), "config.yml");
//	private FileConfiguration config;
	
	private File dbf = new File(getDataFolder(), "database.yml");
	private FileConfiguration db;
	
	private File msgf = new File(getDataFolder(), messagesFile());
	private FileConfiguration msg;

	private static Economy econ = null;
	private int i = 0;
	
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		saveDefaultDatabase();
		saveDefaultMessages();
//		config = YamlConfiguration.loadConfiguration(configf);
		db = YamlConfiguration.loadConfiguration(dbf);
		msg = YamlConfiguration.loadConfiguration(msgf);
		new Manager(this);
		new Chat(this);
		new JoinAndQuit(this);
//		new PlayerDeath(this);
		new Utils(this);
		Lists lists = new Lists();
		TabComplete tabCompleter = new TabComplete(this);
		if (getConfig().getBoolean("use-tabcomplete")) {
			getCommand("gigachat").setTabCompleter(tabCompleter);
			getCommand("chatcolor").setTabCompleter(tabCompleter);
			getCommand("nickcolor").setTabCompleter(tabCompleter);
			getCommand("visibility").setTabCompleter(tabCompleter);
			getCommand("motd").setTabCompleter(tabCompleter);
			getCommand("broadcast").setTabCompleter(tabCompleter);
			getCommand("prefix").setTabCompleter(tabCompleter);
			getCommand("suffix").setTabCompleter(tabCompleter);
			getCommand("dislike").setTabCompleter(tabCompleter);
		}
		send("§aPlugin is enabled!");
		if (!(getConfig().getString("database-type").equalsIgnoreCase("name") || getConfig().getString("database-type").equalsIgnoreCase("uuid"))) {
			send("§cThe save type (§4database-type§c) is incorrect.");
			send("§eSince save type is incorrect, the default save type §6uuid §ewill be used.");
		}
		registerDepends();
		registerDefaults();
		registerChats();
		send("§bList of active chats: §f" + lists.listOfActiveChats("chats"));
	}
	
	public void onDisable() {
		reloadConfig();
		reloadDatabase();
		reloadMessages();
		send("§cPlugin is disabled!");
	}
	
	private void saveDefaultDatabase() {
		if (!dbf.exists()) {
			saveResource("database.yml", false);
		}
	}
	
	private void saveDefaultMessages() {
		if (!msgf.exists()) {
			saveResource(messagesFile(), false);
			if (getResource("messages_" + getConfig().getString("locale") + ".yml") == null) {
				send("§cMessage file §4messages_" + getConfig().getString("locale") + ".yml§c does not exist.");
				send("§aThe default file §2" + messagesFile() + "§a has been saved and will be used.");
			}
		}
	}
	
	public void saveDatabase() {
		try {
			db.save(dbf);
		} catch (IOException ex) {
			send("§cFailed to save §4database.yml");
		}
	}
	
	public void saveMessages() {
		try {
			msg.save(msgf);
		} catch (IOException ex) {
			send("§cFailed to save §4" + messagesFile());
		}
	}

	public FileConfiguration getDatabase() {
		return db;
	}
	
	public FileConfiguration getMessages() {
		return msg;
	}
	
	public void reloadDatabase() {
		db = YamlConfiguration.loadConfiguration(dbf);
		InputStream defConfigStream = getResource("config.yml");
		if (defConfigStream == null)
			return;
		db.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}
	
	public void reloadMessages() {
		msg = YamlConfiguration.loadConfiguration(msgf);
		InputStream defConfigStream = getResource("config.yml");
		if (defConfigStream == null)
			return;
		msg.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}
	
	public void send(String s) {
		ChatColors chatColors = new ChatColors();
		Bukkit.getConsoleSender().sendMessage(chatColors.colors("§e[§6§lGigaChat§r§e]§r " + s, false));
	}
	
	public void registerChats() {
		Lists lists = new Lists();
		active_chats.clear();
		send("§3Registration of chats in progress:");
		List<String> enabled_chats = getConfig().getStringList("enabled-chats");
		String enabled_chat = null;
		List<World> active_chat_worlds = new ArrayList<World>();
		List<String> list_unavailable_worlds = new ArrayList<String>();
		for (int i = 0; i < enabled_chats.size(); i++) {
			String[] enabled_chat_worlds = {};
			active_chat_worlds = new ArrayList<World>();
			list_unavailable_worlds = new ArrayList<String>();
			if (enabled_chats.get(i).contains(":")) {
				enabled_chat = enabled_chats.get(i).split(":")[1].toLowerCase();
				enabled_chat_worlds = enabled_chats.get(i).split(":")[0].split(",");
			} else {
				enabled_chat = enabled_chats.get(i).toLowerCase();
				active_chat_worlds = Bukkit.getServer().getWorlds();
			}
			if (getConfig().contains("chats." + enabled_chat) == false || getConfig().getString("chats." + enabled_chat + ".format") == null || getConfig().getString("chats." + enabled_chat + ".name") == null || getConfig().getString("chats." + enabled_chat + ".start-with") == null) {
				send("§cChat \"§4" + enabled_chat + "§c\" is not defined or the settings are incorrect.");
				continue;
			}
			for (int j = 0; j < enabled_chat_worlds.length; j++) {
				if (Bukkit.getWorld(enabled_chat_worlds[j]) != null) {
					active_chat_worlds.add(Bukkit.getServer().getWorld(enabled_chat_worlds[j]));
				} else {
					list_unavailable_worlds.add(enabled_chat_worlds[j]);
				}
			}
			if (active_chats.containsKey(enabled_chat)) {
				send("§6Chat \"§e" + enabled_chat + "§6\" is repeated in the chats configuration (enabled-chats). It can be overwritten.");
			}
//			for (int w = 0; w < list_unavailable_worlds.size(); w++) {
//				if (w < list_unavailable_worlds.size() - 1) {
//					unavailable_worlds += list_unavailable_worlds.get(w) + ", ";
//				} else {
//					unavailable_worlds += list_unavailable_worlds.get(w);
//				}
//			}
			if (!list_unavailable_worlds.isEmpty()) {
				send("§cList of worlds that are not available in chat \"§4" + enabled_chat + "§c\":§f " + lists.listString(list_unavailable_worlds));
			}
			if (active_chat_worlds.size() > 0) {
				active_chats.put(enabled_chat, active_chat_worlds);
				active_chats_starts.put(enabled_chat, getConfig().getString("chats." + enabled_chat + ".start-with"));
				active_chats_settings.put(enabled_chat, setChatSettings(enabled_chat));
			} else {
				send("§cNo worlds set for chat \"§4" + enabled_chat + "§c\". This chat has not been registered.");
				continue;
			}
			send("§2Registered chat \"§a" + enabled_chat + "§2\" in worlds: §f" + lists.listOfChatWorlds(enabled_chat));
		}
	}
	
	public Object[] setChatSettings(String chat) {
		String format = getConfig().getString("chats." + chat + ".format");
		String chat_name = getConfig().getString("chats." + chat + ".name");
		Double range = (double) -1;
		Double price = (double) 0;
		Boolean color_codes = getConfig().getBoolean("color-codes");
		Boolean capital_letter = getConfig().getBoolean("capital-letter");
		if (getConfig().contains("chats." + chat + ".range")) { range = getConfig().getDouble("chats." + chat + ".range"); }
		if (getConfig().contains("chats." + chat + ".price")) { price = getConfig().getDouble("chats." + chat + ".price"); }
		if (getConfig().contains("chats." + chat + ".color-codes")) { color_codes = getConfig().getBoolean("color-codes") && getConfig().getBoolean("chats." + chat + ".color-codes"); }
		if (getConfig().contains("chats." + chat + ".capital-letter")) { capital_letter = getConfig().getBoolean("capital-letter") && getConfig().getBoolean("chats." + chat + ".capital-letter"); }
		// 0 - format, 1 - chat_name, 2 - range, 3 - price, 4 - color_codes, 5 - capital_letter, ...
		Object[] chatSettings = { format, chat_name, range, price, color_codes, capital_letter };
		return chatSettings;
	}
	
	public void registerDefaults() {
		Utils utils = new Utils(this);
		Lists lists = new Lists();
		defaultColor.clear();
		defaultFormatting.clear();
		defaultVisibility.clear();
		
		defaultColor.put("chat", "WHITE");
		if (Arrays.asList(utils.colors).contains(getConfig().getString("default-color.chat").toUpperCase())) {
			defaultColor.put("chat", getConfig().getString("default-color.chat").toUpperCase());
		} else {
			send("§cThe default chat color (§4default-color.chat§c) is set incorrectly in the config.");
			send("§eThe value §fWHITE §ewill be used as the chat default value.");
		}
		defaultColor.put("nick", "WHITE");
		if (Arrays.asList(utils.colors).contains(getConfig().getString("default-color.nick").toUpperCase())) {
			defaultColor.put("nick", getConfig().getString("default-color.nick").toUpperCase());
		} else {
			send("§cThe default nick color (§4default-color.nick§c) is set incorrectly in the config.");
			send("§eThe value §fWHITE §ewill be used as the nick default value.");
		}
		
		List<String> default_chat_formatting = new ArrayList<String>();
		List<String> default_nick_formatting = new ArrayList<String>();
		List<String> wrong_default_chat_formatting = new ArrayList<String>();
		List<String> wrong_default_nick_formatting = new ArrayList<String>();
		for (String chat_format : getConfig().getStringList("default-formatting.chat")) {
			chat_format = chat_format.toUpperCase();
			if (!default_chat_formatting.contains(chat_format)) {
				if (Arrays.asList(utils.formats).contains(chat_format)) {
					default_chat_formatting.add(chat_format);
				} else {
					wrong_default_chat_formatting.add(chat_format);
				}
			}
		}
		if (!wrong_default_chat_formatting.isEmpty()) { send("§cSome default chat formats (§4default-formatting.chat§c) are set incorrectly."); send("§cWrong formats list: §f" + lists.listString(wrong_default_chat_formatting)); }
		for (String nick_format : getConfig().getStringList("default-formatting.nick")) {
			nick_format = nick_format.toUpperCase();
			if (!default_nick_formatting.contains(nick_format)) {
				if (Arrays.asList(utils.formats).contains(nick_format)) {
					default_nick_formatting.add(nick_format);
				} else {
					wrong_default_nick_formatting.add(nick_format);
				}
			}
		}
		if (!wrong_default_nick_formatting.isEmpty()) { send("§cSome default nick formats (§4default-formatting.nick§c) are set incorrectly."); send("§cWrong formats list: §f" + lists.listString(wrong_default_nick_formatting)); }
		defaultFormatting.put("chat", default_chat_formatting);
		defaultFormatting.put("nick", default_nick_formatting);
		
		defaultVisibility.put("colors", getConfig().getBoolean("default-visibility.colors"));
		defaultVisibility.put("prefix", getConfig().getBoolean("default-visibility.prefix"));
		defaultVisibility.put("suffix", getConfig().getBoolean("default-visibility.suffix"));
		defaultVisibility.put("likes", getConfig().getBoolean("default-visibility.likes"));
		defaultVisibility.put("motd", getConfig().getBoolean("default-visibility.motd"));
	}
	
	public void registerDepends() {
		if (getConfig().getBoolean("use-placeholderapi")) {
			if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				send("§fPlaceholderAPI found and hooked!");
			} else {
				BukkitRunnable r = new BukkitRunnable() {
	
					@Override
					public void run() {
						if (i < 60) {
							if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
								send("§fPlaceholderAPI found and hooked!");
								this.cancel();
							}
						} else {
							this.cancel();
							send("§cPlaceholderAPI still not found (60s)! PAPI placeholders are disabled.");
						}
						i++;
					}
	
				};
				r.runTaskTimer(this, 20L, 20L);
			}
		}
		/*
		i = 0;
		if (getConfig().getBoolean("use-vault")) {
			if (getServer().getPluginManager().isPluginEnabled("Vault")) {
				setupEconomy();
				send("§fVault found and hooked!");
			} else {
				BukkitRunnable r = new BukkitRunnable() {
					
					@Override
					public void run() {
						if (i < 60) {
							if (getServer().getPluginManager().isPluginEnabled("Vault")) {
								setupEconomy();
								send("§fVault found and hooked!");
								this.cancel();
							}
						} else {
							this.cancel();
							send("§cVault still not found (60s)! Economy unavailable.");
						}
						i++;
					}
				};
				r.runTaskTimer(this, 20L, 20L);
			}
		}
	*/
	}
	/*
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	*/
	
	public String messagesFile() {
		String file_name = "messages_ru.yml";
		if (getResource("messages_" + getConfig().getString("locale") + ".yml") != null) {
			file_name = "messages_" + getConfig().getString("locale") + ".yml";
		}
		return file_name;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
}
