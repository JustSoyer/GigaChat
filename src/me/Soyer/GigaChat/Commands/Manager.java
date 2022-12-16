package me.Soyer.GigaChat.Commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sun.tools.doclint.Entity;

import me.Soyer.GigaChat.Main;
import me.Soyer.GigaChat.Utils.ChatColors;
import me.Soyer.GigaChat.Utils.Lists;
import me.Soyer.GigaChat.Utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Manager implements CommandExecutor {
	
	private Main main;
	private ChatColors chatColors = new ChatColors();
	private Lists lists = new Lists();
	public HashMap<Player, Long> broadcast_cooldown = new HashMap<Player, Long>();
	
	public Manager(Main main) {
		main.getCommand("gigachat").setExecutor(this);
		main.getCommand("chatcolor").setExecutor(this);
		main.getCommand("nickcolor").setExecutor(this);
		main.getCommand("motd").setExecutor(this);
		main.getCommand("visibility").setExecutor(this);
		main.getCommand("broadcast").setExecutor(this);
		main.getCommand("prefix").setExecutor(this);
		main.getCommand("suffix").setExecutor(this);
		main.getCommand("like").setExecutor(this);
		main.getCommand("dislike").setExecutor(this);
		main.getCommand("likes").setExecutor(this);
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Utils utils = new Utils(main);
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String record = utils.saveType((OfflinePlayer)p);
			utils.checkDatabase((OfflinePlayer)p);
			
			if (cmd.getName().equalsIgnoreCase("gigachat")) {
				if (utils.isNotPermitted(p, "gigachat.command.gigachat.use")) return true;
				switch (args.length) {
				case 0:
					for (String text : main.getMessages().getStringList("help-gigachat")) {
						colorMessage(p, text.replace("{command}", label));
					}
					break;
				case 1:
					if (args[0].equalsIgnoreCase("reload")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.reload")) return true;
						main.send("§eReloading plugin...");
						main.reloadConfig();
						main.reloadDatabase();
						main.reloadMessages();
						main.registerDepends();
						main.registerDefaults();
						main.registerChats();
						main.colors_str = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr" + main.getConfig().getString("HEX-symbol").toUpperCase().charAt(0) + main.getConfig().getString("HEX-symbol").toLowerCase().charAt(0);
						main.altColorChar = main.getConfig().getString("alt-color-code").charAt(0);
						main.send("§bList of active chats: §f" + lists.listOfActiveChats("chats"));
						main.send("§aPlugins successfully reloaded.");
						colorMessage(p, main.getMessages().getString("plugin-reloaded"));
					} else if (args[0].equalsIgnoreCase("help")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.help")) return true;
						for (String text : main.getMessages().getStringList("help-commands")) {
							colorMessage(p, text.replace("{command}", label));
						}
					} else if (args[0].equalsIgnoreCase("getchats")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.getchats")) return true;
						colorMessage(p, main.getMessages().getString("list-of-active-chats").replace("{chats}", lists.listOfActiveChats("chats")));
					} else if (args[0].equalsIgnoreCase("info")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.info")) return true;
						utils.loginMotd(p, "info");
					} else if (args[0].equalsIgnoreCase("getworlds") || args[0].equalsIgnoreCase("getchatname") || args[0].equalsIgnoreCase("getstart")) {
						for (String text : main.getMessages().getStringList("help-gigachat")) {
							colorMessage(p, text.replace("{command}", label));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
					break;
				case 2:
					if (args[0].equalsIgnoreCase("getworlds")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.getworlds")) return true;
						if (main.active_chats.containsKey(args[1].toLowerCase())) {
							colorMessage(p, main.getMessages().getString("list-of-chat-worlds")
									.replace("{chat}", args[1].toLowerCase())
									.replace("{worlds}", lists.listOfChatWorlds(args[1].toLowerCase())));
						} else {
							colorMessage(p, main.getMessages().getString("chat-not-found").replace("{chat}", args[1].toLowerCase()));
						}
					} else if (args[0].equalsIgnoreCase("getchatname")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.getchatname")) return true;
						if (main.active_chats.containsKey(args[1].toLowerCase())) {
							colorMessage(p, main.getMessages().getString("get-chat-name")
									.replace("{chat}", args[1].toLowerCase())
									.replace("{chat_name}", main.getConfig().getString("chats." + args[1].toLowerCase() + ".name")));
						} else {
							colorMessage(p, main.getMessages().getString("chat-not-found").replace("{chat}", args[1].toLowerCase()));
						}
					} else if (args[0].equalsIgnoreCase("getstart")) {
						if (utils.isNotPermitted(p, "gigachat.command.gigachat.getstart")) return true;
						if (main.active_chats_starts.containsKey(args[1].toLowerCase())) {
							colorMessage(p, main.getMessages().getString("get-start")
									.replace("{chat}", args[1].toLowerCase())
									.replace("{start}", main.active_chats_starts.get(args[1].toLowerCase())));
						} else {
							colorMessage(p, main.getMessages().getString("chat-not-found").replace("{chat}", args[1].toLowerCase()));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
					break;
				default:
					colorMessage(p, main.getMessages().getString("wrong-command"));
					break;
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("chatcolor") || cmd.getName().equalsIgnoreCase("nickcolor")) {
				String cmd_name = null;
				if (cmd.getName().equalsIgnoreCase("chatcolor")) {
					cmd_name = "chat";
				} else if (cmd.getName().equalsIgnoreCase("nickcolor")) {
					cmd_name = "nick";
				} else {
					colorMessage(p, "§cSomething went wrong. Notify the administration!");
					main.send("§4Report issue to the plugin author if you are reading this! Code: §cchatc_nickc_cmd_name_err§4");
					return true;
				}
				if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.use")) return true;
				String color = utils.colorize(main.getDatabase().getString("players." + record + "." + cmd_name + ".color"), p, cmd_name, false);
				String formatting = main.getMessages().getString("nothing-here");
				List<String> current_list_formatting = main.getDatabase().getStringList("players." + record + "." + cmd_name + ".formatting");
				if (!current_list_formatting.isEmpty()) { formatting = lists.listOfPlayerFormats(p, current_list_formatting); }
				String color_list = lists.colorsAndFormatting(cmd_name, "colors");
				String format_list = lists.colorsAndFormatting(cmd_name, "formatting");
				switch (args.length) {
				case 0:
					for (String text : main.getMessages().getStringList("help-" + cmd_name + "color")) {
						colorMessage(p, text.replace("{command}", label).replace("{" + cmd_name + "_color}", color).replace("{" + cmd_name + "_formatting}", formatting));
					}
					break;
				case 1:
					if (args[0].equalsIgnoreCase("list")) {
						if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.list.colors")) {
							colorMessage(p, main.getMessages().getString("list-of-" + cmd_name + "-colors").replace("{" + cmd_name + "_color_list}", color_list));
						}
						if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.list.formats")) {
							colorMessage(p, main.getMessages().getString("list-of-" + cmd_name + "-formats").replace("{" + cmd_name + "_format_list}", format_list));
						}
						if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.list.colors") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.list.formats")) {
							colorMessage(p, main.getMessages().getString("no-permission"));
						}
					} else if (args[0].equalsIgnoreCase("help")) {
						for (String text : main.getMessages().getStringList("help-" + cmd_name + "color")) {
							colorMessage(p, text.replace("{command}", label).replace("{" + cmd_name + "_color}", color).replace("{" + cmd_name + "_formatting}", formatting));
						}
					} else if (args[0].equalsIgnoreCase("set")) {
						if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.use") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.color") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.formatting")) { colorMessage(p, main.getMessages().getString("no-permission")); return true; }
						for (String text : main.getMessages().getStringList("help-" + cmd_name + "color-set")) {
							colorMessage(p, text.replace("{command}", label));
						}
					} else if (args[0].equalsIgnoreCase("DEFAULT")) {
						if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.color.default")) return true;	
						main.getDatabase().set("players." + record + "." + cmd_name + ".color", main.defaultColor.get(cmd_name));
						main.saveDatabase();
						color = utils.colorize(main.getDatabase().getString("players." + record + "." + cmd_name + ".color"), p, cmd_name, false);
						colorMessage(p, main.getMessages().getString(cmd_name + "-color-change-complete").replace("{new_" + cmd_name + "_color}", color));
					} else if (Arrays.asList(utils.colors).contains(args[0].toUpperCase())) {
						if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.color." + args[0].toLowerCase())) {
							colorMessage(p, main.getMessages().getString("no-color-permission").replace("{no-perm-color}", utils.justColorize(args[0].toUpperCase(), args[0].toUpperCase())));
							return true;
						}
						main.getDatabase().set("players." + record + "." + cmd_name + ".color", args[0].toUpperCase());
						main.saveDatabase();
						color = utils.colorize(args[0].toUpperCase(), p, cmd_name, false);
						colorMessage(p, main.getMessages().getString(cmd_name + "-color-change-complete").replace("{new_" + cmd_name + "_color}", color));
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command-or-color"));
					}
					break;
				case 2:
					if (args[0].equalsIgnoreCase("set")) {
						if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.use") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.color") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.formatting")) { colorMessage(p, main.getMessages().getString("no-permission")); return true; }
						if (args[1].equalsIgnoreCase("color") || args[1].equalsIgnoreCase("formatting")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.set." + args[1].toLowerCase())) return true;
							for (String text : main.getMessages().getStringList("help-" + cmd_name + "color-set")) {
								colorMessage(p, text.replace("{command}", label));
							}
						} else {
							colorMessage(p, main.getMessages().getString("wrong-command"));
						}
					} else if (Arrays.asList(utils.colors).contains(args[0].toUpperCase()) || args[0].equalsIgnoreCase("DEFAULT")) {
						if (args[0].equalsIgnoreCase("DEFAULT")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.color.default")) return true;
						} else {
							if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.color." + args[0].toLowerCase())) {
								colorMessage(p, main.getMessages().getString("no-color-permission").replace("{no-perm-color}", utils.justColorize(args[0].toUpperCase(), args[0].toUpperCase())));
								return true;
							}
						}
						List<String> new_list_formatting = new ArrayList<String>();
						String new_color = args[0].toUpperCase();
						if (args[0].equalsIgnoreCase("DEFAULT")) {
							new_color = main.defaultColor.get(cmd_name).toUpperCase();
						}
						List<String> wrong_formats = new ArrayList<>();
						List<String> no_perm_formats = new ArrayList<>();
						if (args[1].equalsIgnoreCase("DEFAULT")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.format.default")) return true;
							new_list_formatting.addAll(main.defaultFormatting.get(cmd_name));
						} else if (args[1].equalsIgnoreCase("CLEAR")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.format.clear")) return true;
							new_list_formatting.clear();
						} else if (args[1].equalsIgnoreCase("*") || args[1].equalsIgnoreCase("all")) {
							for (String new_format : utils.formats) {
								if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.format." + new_format.toLowerCase())) {
									new_list_formatting.add(new_format);
								} else {
									no_perm_formats.add(new_format);
								}
							}
						} else {
							for (String new_format : args[1].toUpperCase().split(",")) {
								if (Arrays.asList(utils.formats).contains(new_format)) {
									if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.format." + new_format.toLowerCase())) {
										if (!new_list_formatting.contains(new_format)) {
											new_list_formatting.add(new_format);
										}
									} else {
										no_perm_formats.add(new_format);
									}
								} else {
									wrong_formats.add(new_format);
								}
							}
						}
						if (!wrong_formats.isEmpty()) {
							colorMessage(p, main.getMessages().getString("wrong-formatting").replace("{wrong_formats}", lists.listString(wrong_formats)));
						}
						if (!no_perm_formats.isEmpty()) {
							colorMessage(p, main.getMessages().getString("no-format-permission").replace("{no-perm-formats}", lists.listFormattingString(no_perm_formats)));
						}
						if (new_list_formatting.isEmpty() && !args[1].equalsIgnoreCase("CLEAR") && !args[1].equalsIgnoreCase("DEFAULT")) {
							colorMessage(p, main.getMessages().getString("have-not-been-changed"));
						} else {
							main.getDatabase().set("players." + record + "." + cmd_name + ".color", new_color.toUpperCase());
							main.getDatabase().set("players." + record + "." + cmd_name + ".formatting", new_list_formatting);
							main.saveDatabase();
							color = utils.colorize(main.getDatabase().getString("players." + record + "." + cmd_name + ".color"), p, cmd_name, false);
							colorMessage(p, main.getMessages().getString(cmd_name + "-color-change-complete").replace("{new_" + cmd_name + "_color}", color));
							if (args[1].equalsIgnoreCase("DEFAULT")) {
								colorMessage(p, main.getMessages().getString("chat-formatting-became-default"));
							}
							if (!new_list_formatting.isEmpty()) {
								colorMessage(p, main.getMessages().getString(cmd_name + "-formatting-change-complete").replace("{new_" + cmd_name + "_formatting}", lists.listOfPlayerFormats(p, new_list_formatting)));
							} else {
								colorMessage(p, main.getMessages().getString(cmd_name + "-formatting-cleared"));
							}
						}
					} else if (args[0].equalsIgnoreCase("list")) {
						if (args[1].equalsIgnoreCase("colors") || args[1].equalsIgnoreCase("c")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.list.colors")) return true;
							colorMessage(p, main.getMessages().getString("list-of-" + cmd_name + "-colors").replace("{" + cmd_name + "_color_list}", color_list));
						} else if (args[1].equalsIgnoreCase("formats") || args[1].equalsIgnoreCase("f")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.list.formats")) return true;
							colorMessage(p, main.getMessages().getString("list-of-" + cmd_name + "-formats").replace("{" + cmd_name + "_format_list}", format_list));
						} else {
							colorMessage(p, main.getMessages().getString("wrong-command"));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command-or-color"));
					}
					break;
				case 3:
					if (args[0].equalsIgnoreCase("set")) {
						if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.use") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.color") && !utils.isPermitted(p, "gigachat.command." + cmd_name + "color.set.formatting")) { colorMessage(p, main.getMessages().getString("no-permission")); return true; }
						if (args[1].equalsIgnoreCase("color")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.set.color")) return true;
							if (Arrays.asList(utils.colors).contains(args[2].toUpperCase()) || args[2].equalsIgnoreCase("default")) {
								if (args[2].equalsIgnoreCase("default")) {
									if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.color.default") && utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.color." + main.defaultColor.get(cmd_name).toLowerCase())) return true;
									main.getDatabase().set("players." + record + "." + cmd_name + ".color", main.defaultColor.get(cmd_name));
									main.saveDatabase();
									color = utils.colorize(main.getDatabase().getString("players." + record + "." + cmd_name + ".color"), p, cmd_name, false);
									colorMessage(p, main.getMessages().getString(cmd_name + "-color-change-complete").replace("{new_" + cmd_name + "_color}", color));
								} else {
									if (!utils.isPermitted(p, "gigachat.command." + cmd_name + "color.color." + args[2].toLowerCase())) {
										colorMessage(p, main.getMessages().getString("no-color-permission").replace("{no-perm-color}", utils.justColorize(args[2].toUpperCase(), args[2].toUpperCase())));
										return true;
									}
									main.getDatabase().set("players." + record + "." + cmd_name + ".color", args[2].toUpperCase());
									main.saveDatabase();
									color = utils.colorize(args[2].toUpperCase(), p, cmd_name, false);
									colorMessage(p, main.getMessages().getString(cmd_name + "-color-change-complete").replace("{new_" + cmd_name + "_color}", color));
								}
							} else {
								colorMessage(p, main.getMessages().getString("wrong-color").replace("{wrong_color}", args[2].toUpperCase()));
							}
						} else if (args[1].equalsIgnoreCase("formatting")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.set.formatting")) return true;
							List<String> new_list_formatting = new ArrayList<String>();
							List<String> wrong_formats = new ArrayList<>();
							List<String> no_perm_formats = new ArrayList<>();
							if (args[2].equalsIgnoreCase("DEFAULT")) {
								if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.format.default")) return true;
								new_list_formatting.addAll(main.defaultFormatting.get(cmd_name));
							} else if (args[2].equalsIgnoreCase("CLEAR")) {
								if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + "color.format.clear")) return true;
								new_list_formatting.clear();
							} else if (args[2].equalsIgnoreCase("*") || args[2].equalsIgnoreCase("all")) {
								for (String new_format : utils.formats) {
									if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.format." + new_format.toLowerCase())) {
										new_list_formatting.add(new_format);
									} else {
										no_perm_formats.add(new_format);
									}
								}
							} else {
								for (String new_format : args[2].toUpperCase().split(",")) {
									if (Arrays.asList(utils.formats).contains(new_format)) {
										if (utils.isPermitted(p, "gigachat.command." + cmd_name + "color.format." + new_format.toLowerCase())) {
											if (!new_list_formatting.contains(new_format)) {
												new_list_formatting.add(new_format);
											}
										} else {
											no_perm_formats.add(new_format);
										}
									} else {
										wrong_formats.add(new_format);
									}
								}
							}
							if (!wrong_formats.isEmpty()) {
								colorMessage(p, main.getMessages().getString("wrong-formatting").replace("{wrong_formats}", lists.listString(wrong_formats)));
							}
							if (!no_perm_formats.isEmpty()) {
								colorMessage(p, main.getMessages().getString("no-format-permission").replace("{no-perm-formats}", lists.listFormattingString(no_perm_formats)));
							}
							if (new_list_formatting.isEmpty() && !args[2].equalsIgnoreCase("CLEAR") && !args[2].equalsIgnoreCase("DEFAULT")) {
								colorMessage(p, main.getMessages().getString("have-not-been-changed"));
							} else {
								main.getDatabase().set("players." + record + "." + cmd_name + ".formatting", new_list_formatting);
								main.saveDatabase();
								if (args[2].equalsIgnoreCase("DEFAULT")) {
									colorMessage(p, main.getMessages().getString("chat-formatting-became-default"));
								}
								if (!new_list_formatting.isEmpty()) {
									colorMessage(p, main.getMessages().getString(cmd_name + "-formatting-change-complete").replace("{new_" + cmd_name + "_formatting}", lists.listOfPlayerFormats(p, new_list_formatting)));
								} else {
									colorMessage(p, main.getMessages().getString(cmd_name + "-formatting-cleared"));
								}
							}
						} else {
							colorMessage(p, main.getMessages().getString("wrong-command"));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
					break;
				default:
					colorMessage(p, main.getMessages().getString("wrong-command"));
					break;
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("motd")) {
				if (utils.isNotPermitted(p, "gigachat.command.motd.use")) return true;
				switch (args.length) {
				case 0:
					if (main.getConfig().getBoolean("motd-enabled")) {
						utils.loginMotd(p, "motd");
					} else {
						colorMessage(p, main.getMessages().getString("motd-unavailable"));
					}
					break;
				default:
					colorMessage(p, main.getMessages().getString("wrong-command"));
					break;
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("visibility")) {
				if (utils.isNotPermitted(p, "gigachat.command.visibility.use")) return true;
				switch (args.length) {
				case 0:
					for (String text : main.getMessages().getStringList("help-visibility")) {
						colorMessage(p, text.replace("{command}", label));
					}
					break;
				case 1:
					if (args[0].equalsIgnoreCase("list")) {
						if (utils.isNotPermitted(p, "gigachat.command.visibility.list")) return true;
						for (String text : main.getMessages().getStringList("visibility-list")) {
							if (!main.getConfig().getBoolean("prefixes")) {
								if (text.contains("{prefix}") || text.contains("{prefix_capital_letter}") || text.contains("{prefix_visibility_status}")) { continue; }
							}
							if (!main.getConfig().getBoolean("suffixes")) {
								if (text.contains("{suffix}") || text.contains("{suffix_capital_letter}") || text.contains("{suffix_visibility_status}")) { continue; }
							}
							if (!main.getConfig().getBoolean("likes-and-dislikes")) {
								if (text.contains("{likes}") || text.contains("{likes_capital_letter}") || text.contains("{likes_visibility_status}")) { continue; }
							}
							if (!main.getConfig().getBoolean("motd-enabled")) {
								if (text.contains("{motd}") || text.contains("{motd_capital_letter}") || text.contains("{motd_visibility_status}")) { continue; }
							}
							p.sendMessage(chatColors.tacl(p, text
									.replace("{colors}", main.getConfig().getString("colors-visibility"))
									.replace("{prefix}", main.getConfig().getString("prefix-visibility"))
									.replace("{suffix}", main.getConfig().getString("suffix-visibility"))
									.replace("{likes}", main.getConfig().getString("likes-visibility"))
									.replace("{motd}", main.getConfig().getString("motd-visibility"))
									.replace("{colors_capital_letter}", main.getConfig().getString("colors-visibility").toUpperCase().charAt(0) + main.getConfig().getString("colors-visibility").substring(1))
									.replace("{prefix_capital_letter}", main.getConfig().getString("prefix-visibility").toUpperCase().charAt(0) + main.getConfig().getString("prefix-visibility").substring(1))
									.replace("{suffix_capital_letter}", main.getConfig().getString("suffix-visibility").toUpperCase().charAt(0) + main.getConfig().getString("suffix-visibility").substring(1))
									.replace("{likes_capital_letter}", main.getConfig().getString("likes-visibility").toUpperCase().charAt(0) + main.getConfig().getString("likes-visibility").substring(1))
									.replace("{motd_capital_letter}", main.getConfig().getString("motd-visibility").toUpperCase().charAt(0) + main.getConfig().getString("motd-visibility").substring(1))
									.replace("{colors_visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility.colors")))
									.replace("{prefix_visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility.prefix")))
									.replace("{suffix_visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility.suffix")))
									.replace("{likes_visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility.likes")))
									.replace("{motd_visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility.motd"))), true, true));
						}
					} else if (args[0].equalsIgnoreCase("colors") || args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("suffix") || args[0].equalsIgnoreCase("likes") || args[0].equalsIgnoreCase("motd")) {
						if (utils.isNotPermitted(p, "gigachat.command.visibility." + args[0].toLowerCase())) return true;
						main.getDatabase().set("players." + record + ".visibility." + args[0].toLowerCase(), !main.getDatabase().getBoolean("players." + record + ".visibility." + args[0].toLowerCase()));
						main.saveDatabase();
						colorMessage(p, main.getMessages().getString("visibility-status-update")
								.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
								.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
								.replace("{visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility." + args[0].toLowerCase()))));
					} else if (args[0].equalsIgnoreCase("help")) {
						for (String text : main.getMessages().getStringList("help-visibility")) {
							colorMessage(p, text.replace("{command}", label));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
					break;
				case 2:
					if (args[0].equalsIgnoreCase("colors") || args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("suffix") || args[0].equalsIgnoreCase("likes") || args[0].equalsIgnoreCase("motd")) {
						if (utils.isNotPermitted(p, "gigachat.command.visibility." + args[0].toLowerCase())) return true;
						switch (args[1].toLowerCase()) {
						case "help":
							for (String text : main.getMessages().getStringList("help-visibility-args")) {
								p.sendMessage(chatColors.tacl(p, text.replace("{command}", label)
										.replace("{arg}", args[0].toLowerCase())
										.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
										.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
										.replace("{visibility_status}", visibilityStatus(main.getDatabase().getBoolean("players." + record + ".visibility." + args[0].toLowerCase()))), true, true));
							}
							break;
						case "true":
							if (main.getDatabase().getBoolean("players." + record + ".visibility." + args[0].toLowerCase())) {
								colorMessage(p, main.getMessages().getString("visibility-status-already")
										.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
										.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
										.replace("{visibility_status}", main.getConfig().getString("enabled")));
							} else {
								main.getDatabase().set("players." + record + ".visibility." + args[0].toLowerCase(), true);
								main.saveDatabase();
								colorMessage(p, main.getMessages().getString("visibility-status-update")
										.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
										.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
										.replace("{visibility_status}", main.getConfig().getString("enabled")));
							}
							break;
						case "false":
							if (!main.getDatabase().getBoolean("players." + record + ".visibility." + args[0].toLowerCase())) {
								colorMessage(p, main.getMessages().getString("visibility-status-already")
										.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
										.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
										.replace("{visibility_status}", main.getConfig().getString("disabled")));
							} else {
								main.getDatabase().set("players." + record + ".visibility." + args[0].toLowerCase(), false);
								main.saveDatabase();
								colorMessage(p, main.getMessages().getString("visibility-status-update")
										.replace("{visibility_arg_capital_letter}", main.getConfig().getString(args[0].toLowerCase() + "-visibility").toUpperCase().charAt(0) + main.getConfig().getString(args[0].toLowerCase() + "-visibility").substring(1))
										.replace("{visibility_arg}", main.getConfig().getString(args[0].toLowerCase() + "-visibility"))
										.replace("{visibility_status}", main.getConfig().getString("disabled")));
							}
							break;
						default:
							colorMessage(p, main.getMessages().getString("wrong-command"));
							break;
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
					break;
				default:
					colorMessage(p, main.getMessages().getString("wrong-command"));
					break;
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("prefix") || cmd.getName().equalsIgnoreCase("suffix")) {
				if (!cmd.getName().equalsIgnoreCase("prefix") && !cmd.getName().equalsIgnoreCase("suffix")) {
					colorMessage(p, "§cSomething went wrong. Notify the administration!");
					main.send("§4Report issue to the plugin author if you are reading this! Code: §cprefix_suffix_cmd_name_err§4");
					return true;
				}
				String cmd_name = cmd.getName().toLowerCase();
				if (main.getConfig().getBoolean(cmd_name + "es")) {
					if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".use")) return true;
					String status = main.getConfig().getString("not-set");
					if (!main.getDatabase().getString("players." + record + "." + cmd_name).isBlank()) { status = main.getDatabase().getString("players." + record + "." + cmd_name); }
					switch (args.length) {
					case 0:
						for (String text : main.getMessages().getStringList("help-" + cmd_name)) {
							text = chatColors.colors(text.replace("{command}", label).replace("{min_" + cmd_name + "_length}", main.getConfig().getInt("min-" + cmd_name + "-length") + "").replace("{max_" + cmd_name + "_length}", main.getConfig().getInt("max-" + cmd_name + "-length") + ""), true);
							if (status != main.getConfig().getString("not-set")) {
								text = text.replace("{" + cmd_name + "}", chatColors.ptacl(p, status, false, cmd_name));
							} else {
								text = text.replace("{" + cmd_name + "}", chatColors.tacl(p, status, false, false));
							}
							p.sendMessage(text);
						}
						break;
					case 1:
						if (args[0].equalsIgnoreCase("help")) {
							for (String text : main.getMessages().getStringList("help-" + cmd_name)) {
								text = chatColors.colors(text.replace("{command}", label).replace("{min_" + cmd_name + "_length}", main.getConfig().getInt("min-" + cmd_name + "-length") + "").replace("{max_" + cmd_name + "_length}", main.getConfig().getInt("max-" + cmd_name + "-length") + ""), true);
								if (status != main.getConfig().getString("not-set")) {
									text = text.replace("{" + cmd_name + "}", chatColors.ptacl(p, status, false, cmd_name));
								} else {
									text = text.replace("{" + cmd_name + "}", chatColors.tacl(p, status, false, false));
								}
								p.sendMessage(text);
							}
						} else if (args[0].equalsIgnoreCase("set")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".set")) return true;
							for (String text : main.getMessages().getStringList("help-" + cmd_name)) {
								text = chatColors.colors(text.replace("{command}", label).replace("{min_" + cmd_name + "_length}", main.getConfig().getInt("min-" + cmd_name + "-length") + "").replace("{max_" + cmd_name + "_length}", main.getConfig().getInt("max-" + cmd_name + "-length") + ""), true);
								if (status != main.getConfig().getString("not-set")) {
									text = text.replace("{" + cmd_name + "}", chatColors.ptacl(p, status, false, cmd_name));
								} else {
									text = text.replace("{" + cmd_name + "}", chatColors.tacl(p, status, false, false));
								}
								p.sendMessage(text);
							}
						} else if (args[0].equalsIgnoreCase("remove")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".remove")) return true;
							main.getDatabase().set("players." + record + "." + cmd_name, "");
							main.saveDatabase();
							colorMessage(p, main.getMessages().getString(cmd_name + "-removed"));
						} else if (args[0].equalsIgnoreCase("default")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".default")) return true;
							main.getDatabase().set("players." + record + "." + cmd_name, main.getConfig().getString("default-" + cmd_name));
							main.saveDatabase();
							colorMessage(p, main.getMessages().getString(cmd_name + "-to-default"));
						} else {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".set")) return true;
							setPrefixOrSuffix(args[0], p, cmd_name);
						}
						break;
					default:
						if (args[0].equalsIgnoreCase("set")) {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".set")) return true;
							if (args.length == 2) {
								setPrefixOrSuffix(args[1], p, cmd_name);
							} else {
								if (main.getConfig().getBoolean(cmd_name + "-with-spaces")) {
									if (!utils.isPermitted(p, "gigachat.command." + cmd_name + ".spaces")) { colorMessage(p, main.getMessages().getString("no-" + cmd_name + "es-with-spaces-permission")); return true; }
									String new_string = "";
									for (int i = 1; i < args.length; i++) {
										if (i < args.length - 1) {
											new_string += args[i] + " ";
										} else {
											new_string += args[i];
										}
									}
									setPrefixOrSuffix(new_string, p, cmd_name);
								} else {
									colorMessage(p, main.getMessages().getString(cmd_name + "es-with-spaces-unavailable"));
								}
							}
						} else {
							if (utils.isNotPermitted(p, "gigachat.command." + cmd_name + ".set")) return true;
							if (main.getConfig().getBoolean(cmd_name + "-with-spaces")) {
								if (!utils.isPermitted(p, "gigachat.command." + cmd_name + ".spaces")) { colorMessage(p, main.getMessages().getString("no-" + cmd_name + "es-with-spaces-permission")); return true; }
								String new_string = "";
								for (int i = 0; i < args.length; i++) {
									if (i < args.length - 1) {
										new_string += args[i] + " ";
									} else {
										new_string += args[i];
									}
								}
								setPrefixOrSuffix(new_string, p, cmd_name);
							} else {
								colorMessage(p, main.getMessages().getString(cmd_name + "es-with-spaces-unavailable"));
							}
						}
						break;
					}
				} else {
					colorMessage(p, main.getMessages().getString(cmd_name + "es-unavailable"));
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("broadcast")) {
				if (utils.isNotPermitted(p, "gigachat.command.broadcast.use")) return true;
				if (args.length == 0) {
					for (String text : main.getMessages().getStringList("help-broadcast")) {
						colorMessage(p, text.replace("{command}", label));
					}
				} else {
					if (args[0].equalsIgnoreCase("help")) {
						if (args.length == 1) {
							for (String text : main.getMessages().getStringList("help-broadcast")) {
								colorMessage(p, text.replace("{command}", label));
							}
						} else {
							colorMessage(p, main.getMessages().getString("wrong-command"));
						}
					} else {
						String bc_type = args[0].toLowerCase();
						int i;
						if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("actionbar") || args[0].equalsIgnoreCase("title")) {
							if (utils.isNotPermitted(p, "gigachat.command.broadcast." + bc_type)) return true;
							if (args.length == 1) {
								for (String text : main.getMessages().getStringList("help-broadcast")) {
									colorMessage(p, text.replace("{command}", label));
								}
								return true;
							}
							i = 1;
						} else {
							if (main.getConfig().getString("broadcast-default-type").equalsIgnoreCase("chat") || main.getConfig().getString("broadcast-default-type").equalsIgnoreCase("actionbar") || main.getConfig().getString("broadcast-default-type").equalsIgnoreCase("title")) {
								bc_type = main.getConfig().getString("broadcast-default-type").toLowerCase();
							} else {
								bc_type = "chat";
							}
							if (utils.isNotPermitted(p, "gigachat.command.broadcast." + bc_type)) return true;
							i = 0;
						}
						Long time = new Date().getTime();
						if (broadcast_cooldown.containsKey(p) && time < (broadcast_cooldown.get(p) + main.getConfig().getLong("broadcast-cooldown"))) {
							if (!utils.isPermitted(p, "gigachat.broadcast.cooldown.bypass")) {
								colorMessage(p, main.getMessages().getString("cooldown-message").replace("{time}", new DecimalFormat("#0.0").format((float)(main.getConfig().getLong("broadcast-cooldown") - (time - broadcast_cooldown.get(p))) / 1000).replace(',', '.')));
								return true;
							}
						}
						String broadcast_message = "";
						while (i < args.length) {
							if (i < args.length - 1) {
								broadcast_message += args[i] + " ";
							} else {
								broadcast_message += args[i];
							}
							i++;
						}
						if (!main.getConfig().getBoolean("allow-empty-messages")) {
							if (utils.pUncolorize(p, broadcast_message, true, "broadcast").isBlank()) {
								if (main.getConfig().getBoolean("failed-to-send")) {
									colorMessage(p, main.getMessages().getString("failed-to-send-broadcast").replace("{message}", broadcast_message));
								}
								colorMessage(p, main.getMessages().getString("empty-message"));
								return true;
							}
						}
						Sound sound = null;
						if (!main.getConfig().getString("broadcast-sound").equalsIgnoreCase("") && !main.getConfig().getString("broadcast-sound").equalsIgnoreCase("false") && main.getConfig().getString("broadcast-sound") != null) {
							sound = Sound.valueOf(main.getConfig().getString("broadcast-sound").toUpperCase());
						}
						String bc_sender = p.getName();
						if (main.getConfig().getBoolean("broadcast-sender-design") || main.getConfig().getBoolean("playername-design")) { bc_sender = utils.colorize(utils.formatting(p.getName(), p, "nick", true), p, "nick", true); }
						if (bc_type.equalsIgnoreCase("chat")) {
							List<String> new_bc_format = new ArrayList<String>();
							for (String bc_str : main.getConfig().getStringList("broadcast-chat-format")) {
								new_bc_format.add(chatColors.tacl(p, bc_str, true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast")));
							}
							for (Player pl : Bukkit.getOnlinePlayers()) {
								for (String result_str : new_bc_format) {
									pl.sendMessage(result_str);
								}
								pl.playSound(pl.getLocation(), sound, 1F, 1F);
							}
						}
						if (bc_type.equalsIgnoreCase("actionbar")) {
							String bc_str = chatColors.tacl(p, main.getConfig().getString("broadcast-actionbar-format"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
							for (Player pl : Bukkit.getOnlinePlayers()) {
								pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bc_str));
								pl.playSound(pl.getLocation(), sound, 1F, 1F);
							}
						}
						if (bc_type.equalsIgnoreCase("title")) {
							String bc_title = chatColors.tacl(p, main.getConfig().getString("broadcast-title-settings.broadcast-title"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
							String bc_subtitle = chatColors.tacl(p, main.getConfig().getString("broadcast-title-settings.broadcast-subtitle"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
							for (Player pl : Bukkit.getOnlinePlayers()) {
								pl.sendTitle(bc_title, bc_subtitle, main.getConfig().getInt("broadcast-title-settings.fadeIn"), main.getConfig().getInt("broadcast-title-settings.stay"), main.getConfig().getInt("broadcast-title-settings.fadeOut"));
								pl.playSound(pl.getLocation(), sound, 1F, 1F);
							}
						}
						if (!utils.isPermitted(p, "gigachat.broadcast.cooldown.bypass")) {
							broadcast_cooldown.put(p, time);
						}
					}
//					} else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("title") || args[0].equalsIgnoreCase("actionbar")) {
//						if (utils.isNotPermitted(p, "gigachat.command.broadcast." + args[0].toLowerCase())) return true;
//						if (args.length == 1) {
//							for (String text : main.getMessages().getStringList("help-broadcast")) {
//								colorMessage(p, text.replace("{command}", label));
//							}
//						} else {
//							String broadcast_message = "";
//							for (int i = 1; i < args.length; i++) {
//								if (i < args.length - 1) {
//									broadcast_message += args[i] + " ";
//								} else {
//									broadcast_message += args[i];
//								}
//							}
//							if (!main.getConfig().getBoolean("allow-empty-messages")) {
//								if (utils.pUncolorize(p, broadcast_message, true, "broadcast").isBlank()) {
//									if (main.getConfig().getBoolean("failed-to-send")) {
//										colorMessage(p, main.getMessages().getString("failed-to-send").replace("{message}", broadcast_message).replace("{chat_name}", main.getMessages().getString("broadcast-name")));
//									}
//									colorMessage(p, main.getMessages().getString("empty-message"));
//									return true;
//								}
//							}
//							Sound sound = null;
//							if (!main.getConfig().getString("broadcast-sound").equalsIgnoreCase("") && !main.getConfig().getString("broadcast-sound").equalsIgnoreCase("false") && main.getConfig().getString("broadcast-sound") != null) {
//								sound = Sound.valueOf(main.getConfig().getString("broadcast-sound"));
//							}
//							String bc_sender = p.getName();
//							if (main.getConfig().getBoolean("broadcast-sender-design")) { bc_sender = utils.colorize(utils.formatting(p.getName(), p, "nick", true), p, "nick", true); }
//							if (args[0].equalsIgnoreCase("chat")) {
//								List<String> new_bc_format = new ArrayList<String>();
//								for (String bc_str : main.getConfig().getStringList("broadcast-chat-format")) {
//									new_bc_format.add(chatColors.tacl(p, bc_str, true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast")));
//								}
//								for (Player pl : Bukkit.getOnlinePlayers()) {
//									for (String result_str : new_bc_format) {
//										pl.sendMessage(result_str);
//									}
//									pl.playSound(pl.getLocation(), sound, 1F, 1F);
//								}
//							}
//							if (args[0].equalsIgnoreCase("actionbar")) {
//								String bc_str = chatColors.tacl(p, main.getConfig().getString("broadcast-actionbar-format"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
//								for (Player pl : Bukkit.getOnlinePlayers()) {
//									pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bc_str));
//									pl.playSound(pl.getLocation(), sound, 1F, 1F);
//								}
//							}
//							if (args[0].equalsIgnoreCase("title")) {
//								String bc_title = chatColors.tacl(p, main.getConfig().getString("broadcast-title-settings.broadcast-title"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
//								String bc_subtitle = chatColors.tacl(p, main.getConfig().getString("broadcast-title-settings.broadcast-subtitle"), true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast"));
//								for (Player pl : Bukkit.getOnlinePlayers()) {
//									pl.sendTitle(bc_title, bc_subtitle, main.getConfig().getInt("broadcast-title-settings.fadeIn"), main.getConfig().getInt("broadcast-title-settings.stay"), main.getConfig().getInt("broadcast-title-settings.fadeOut"));
//									pl.playSound(pl.getLocation(), sound, 1F, 1F);
//								}
//							}
//						}
//					} else {
//						if (utils.isNotPermitted(p, "gigachat.command.broadcast.chat")) return true;
//						String broadcast_message = "";
//						for (int i = 0; i < args.length; i++) {
//							if (i < args.length - 1) {
//								broadcast_message += args[i] + " ";
//							} else {
//								broadcast_message += args[i];
//							}
//						}
//						if (!main.getConfig().getBoolean("allow-empty-messages")) {
//							if (utils.pUncolorize(p, broadcast_message, true, "broadcast").isBlank()) {
//								if (main.getConfig().getBoolean("failed-to-send")) {
//									colorMessage(p, main.getMessages().getString("failed-to-send").replace("{message}", broadcast_message).replace("{chat_name}", main.getMessages().getString("broadcast-name")));
//								}
//								colorMessage(p, main.getMessages().getString("empty-message"));
//								return true;
//							}
//						}
//						Sound sound = null;
//						if (!main.getConfig().getString("broadcast-sound").equalsIgnoreCase("") && !main.getConfig().getString("broadcast-sound").equalsIgnoreCase("false") && main.getConfig().getString("broadcast-sound") != null) {
//							sound = Sound.valueOf(main.getConfig().getString("broadcast-sound"));
//						}
//						List<String> new_bc_format = new ArrayList<String>();
//						String bc_sender = p.getName();
//						if (main.getConfig().getBoolean("broadcast-sender-design")) { bc_sender = utils.colorize(utils.formatting(p.getName(), p, "nick", true), p, "nick", true); }
//						for (String bc_str : main.getConfig().getStringList("broadcast-chat-format")) {
//							new_bc_format.add(chatColors.tacl(p, bc_str, true, false).replace("{sender}", bc_sender).replace("{broadcast}", chatColors.ptacl(p, broadcast_message, false, "broadcast")));
//						}
//						for (Player pl : Bukkit.getOnlinePlayers()) {
//							for (String result_str : new_bc_format) {
//								pl.sendMessage(result_str);
//							}
//							pl.playSound(pl.getLocation(), sound, 1F, 1F);
//						}
//					}
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("like")) {
				if (main.getConfig().getBoolean("likes-and-dislikes")) {
					switch (args.length) {
					case 0:
						for (String text : main.getMessages().getStringList("help-like-and-dislike")) {
							colorMessage(p, text.replace("{command}", label).replace("{like-or-dislike}", cmd.getName()).replace("{liked_count}", main.getDatabase().getString("players." + record + ".likes.liked-count")).replace("{likes_count}", main.getDatabase().getString("players." + record + ".likes.likes-count")).replace("{max_liked_players}", main.getConfig().getString("max-liked-players")));
						}
						break;
					case 1:
						Player pl = null;
						List<Player> found_players = new ArrayList<Player>();
						for (Player plll : Bukkit.getOnlinePlayers()) {
							if (plll.getName().equalsIgnoreCase(args[0])) {
								found_players.add(plll);
							}
						}
						if (!found_players.isEmpty()) {
							for (Player plll : found_players) {
								if (plll.getName().equals(args[0])) { pl = plll; break; }
							}
							if (pl == null) {
								pl = found_players.get(0);
							}
						}
						if (pl != null) {
							if (main.getDatabase().getStringList("players." + record + ".likes.liked-players").size() >= main.getConfig().getInt("max-liked-players")) {
								colorMessage(p, main.getMessages().getString("too-many-liked-players").replace("{max_liked_players}", main.getConfig().getString("max-liked-players")));
								return true;
							}
							if (!main.getDatabase().getStringList("players." + record + ".likes.liked-players").contains(pl.getName())) {
								String pl_name = pl.getName();
								if (pl_name != p.getName()) {
									main.getDatabase().set("players." + record + ".likes.liked-count", main.getDatabase().getInt("players." + record + ".likes.liked-count") + 1);
									List<String> temp = main.getDatabase().getStringList("players." + record + ".likes.liked-players"); temp.add(pl_name);
									main.getDatabase().set("players." + record + ".likes.liked-players", temp);
									p.sendMessage(chatColors.tacl(p, main.getMessages().getString("you-liked-the-player").replace("{player}", pl_name), true, true));
									utils.checkDatabase((OfflinePlayer)pl);
									String pl_record = utils.saveType((OfflinePlayer)pl);
									main.getDatabase().set("players." + pl_record + ".likes.likes-count", main.getDatabase().getInt("players." + pl_record + ".likes.likes-count") + 1);
									main.saveDatabase();
									if (main.getConfig().getBoolean("like-notice")) {
										pl.sendMessage(chatColors.tacl(pl, main.getMessages().getString("you-have-been-liked").replace("{player}", p.getName()), true, true));
									}
								} else {
									colorMessage(p, main.getMessages().getString("you-already-like-yourself"));
								}
							} else {
								colorMessage(p, main.getMessages().getString("you-already-like-this-player"));
							}
						} else {
							colorMessage(p, main.getMessages().getString("player-not-found"));
						}
						break;
					default:
						colorMessage(p, main.getMessages().getString("wrong-command"));
						break;
					}
				} else {
					colorMessage(p, main.getMessages().getString("likes-and-dislikes-not-available"));
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("dislike")) {
				if (main.getConfig().getBoolean("likes-and-dislikes")) {
					switch (args.length) {
					case 0:
						for (String text : main.getMessages().getStringList("help-like-and-dislike")) {
							colorMessage(p, text.replace("{command}", label).replace("{like-or-dislike}", cmd.getName()).replace("{liked_count}", main.getDatabase().getString("players." + record + ".likes.liked-count")).replace("{likes_count}", main.getDatabase().getString("players." + record + ".likes.likes-count")).replace("{max_liked_players}", main.getConfig().getString("max-liked-players")));
						}
						break;
					case 1:
						OfflinePlayer pl = null;
						List<OfflinePlayer> found_players = new ArrayList<OfflinePlayer>();
						for (OfflinePlayer plll : Bukkit.getOfflinePlayers()) {
							if (plll.getName().equalsIgnoreCase(args[0])) {
								found_players.add(plll);
							}
						}
						if (!found_players.isEmpty()) {
							for (OfflinePlayer plll : found_players) {
								if (plll.getName().equals(args[0])) { pl = plll; break; }
							}
							if (pl == null) {
								pl = found_players.get(0);
							}
						}
						if (pl != null) {
							String pl_name = pl.getName();
							if (pl_name != p.getName()) {
								if (main.getDatabase().getStringList("players." + record + ".likes.liked-players").contains(pl.getName())) {
										main.getDatabase().set("players." + record + ".likes.liked-count", main.getDatabase().getInt("players." + record + ".likes.liked-count") - 1);
										List<String> temp = main.getDatabase().getStringList("players." + record + ".likes.liked-players"); temp.remove(pl_name);
										main.getDatabase().set("players." + record + ".likes.liked-players", temp);
										p.sendMessage(chatColors.tacl(p, main.getMessages().getString("you-disliked-the-player").replace("{player}", pl_name), true, true));
										utils.checkDatabase((OfflinePlayer)pl);
										String pl_record = utils.saveType((OfflinePlayer)pl);
										main.getDatabase().set("players." + pl_record + ".likes.likes-count", main.getDatabase().getInt("players." + pl_record + ".likes.likes-count") - 1);
										main.saveDatabase();
								} else {
									colorMessage(p, main.getMessages().getString("player-not-liked"));
								}
							} else {
								colorMessage(p, main.getMessages().getString("pls-love-yourself"));
							}
						} else {
							if (main.getDatabase().getStringList("players." + record + ".likes.liked-players").contains(args[0])) {
								List<String> temp = main.getDatabase().getStringList("players." + record + ".likes.liked-players"); temp.remove(args[0]);
								main.getDatabase().set("players." + record + ".likes.liked-players", temp);
								main.saveDatabase();
								colorMessage(p, main.getMessages().getString("player-not-found-but-removed").replace("{player}", args[0]));
							} else {
								colorMessage(p, main.getMessages().getString("player-not-found"));
							}
						}
						break;
					default:
						colorMessage(p, main.getMessages().getString("wrong-command"));
						break;
					}
				} else {
					colorMessage(p, main.getMessages().getString("likes-and-dislikes-not-available"));
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("likes")) {
				if (main.getConfig().getBoolean("likes-and-dislikes")) {
					if (args.length == 0) {
						List<String> liked_players = main.getDatabase().getStringList("players." + record + ".likes.liked-players");
						String liked_players_str = "";
						List<String> online_players = new ArrayList<String>();
						for (Player plll : Bukkit.getOnlinePlayers()) {
							online_players.add(plll.getName());
						}
						for (int i = 0; i < liked_players.size(); i++) {
							if (online_players.contains(liked_players.get(i))) {
								liked_players_str += "§a" + liked_players.get(i);
							} else {
								liked_players_str += "§c" + liked_players.get(i);
							}
							if (i != liked_players.size() - 1) {
								liked_players_str += "§r, ";
							} else {
								liked_players_str += "§r";
							}
						}
						if (liked_players.isEmpty()) {
							liked_players_str = main.getMessages().getString("no-one-here");
						}
						for (String text : main.getMessages().getStringList("likes")) {
							colorMessage(p, text.replace("{liked_count}", main.getDatabase().getString("players." + record + ".likes.liked-count")).replace("{likes_count}", main.getDatabase().getString("players." + record + ".likes.likes-count")).replace("{liked_players}", liked_players_str).replace("{max_liked_players}", main.getConfig().getString("max-liked-players")));
						}
					} else {
						colorMessage(p, main.getMessages().getString("wrong-command"));
					}
				} else {
					colorMessage(p, main.getMessages().getString("likes-and-dislikes-not-available"));
				}
			}
			
		}
		
		return true;
	}

	public void setPrefixOrSuffix(String s, Player p, String type /* "prefix" or "suffix" */) {
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("prefix") && !type.equalsIgnoreCase("suffix")) {
			p.sendMessage(chatColors.colors("§cSomething went wrong. Notify the administration!", true));
			main.send("§4Report issue to the plugin author if you are reading this! Code: §ccolorize_type_err§4");
			return;
		}
		Utils utils = new Utils(main);
		String record = utils.saveType((OfflinePlayer)p);
		if (main.getConfig().getBoolean("uncolorize-" + type)) {
			
		}
		String unc_s = utils.pUncolorize(p, s, true, type);
		type = type.toLowerCase();
		if (!type.equalsIgnoreCase("prefix") && !type.equalsIgnoreCase("suffix")) {
			main.send("§cWrong setPrefixOrSuffix() type: §f" + type);
			colorMessage(p, "§cSomething went wrong. Notify the administration!");
			return;
		}
		if (unc_s.length() < main.getConfig().getInt("min-" + type + "-length")) { colorMessage(p, main.getMessages().getString("too-short-" + type)); return; }
		if (unc_s.length() > main.getConfig().getInt("max-" + type + "-length")) { colorMessage(p, main.getMessages().getString("too-long-" + type)); return; }
		if (unc_s.isBlank()) { colorMessage(p, main.getMessages().getString("empty-" + type)); return; }
		main.getDatabase().set("players." + record + "." + type, s);
		main.saveDatabase();
		p.sendMessage(chatColors.colors(main.getMessages().getString(type + "-change-complete"), true).replace("{" + type + "}", chatColors.ptacl(p, main.getDatabase().getString("players." + record + "." + type), false, type)));
	}
	
	public String visibilityStatus(Boolean status) {
		if (status) {
			return main.getConfig().getString("enabled");
		} else {
			return main.getConfig().getString("disabled");
		}
	}
	
	public void colorMessage(Player p, String message) {
//		Utils utils = new Utils(main);
////		if (message.matches("<<perm=*>>")) {
//		if (message.matches("<<perm=[0-9a-zA-Z]*>>")) {
//			p.sendMessage("ssss");
//			if (!utils.isPermitted(p, message.split("<<perm=")[1].split(">>")[0])) {
//				return;
//			}
//			message.replaceAll("<<perm=[a-zA-Z0-9]+>>", "");
//		}
		
//		Pattern pattern = Pattern.compile("[a-zA-Z0-9]");
//		if ("sfdsd edss<<perm=t2323est>> ssas".matches(".*<<perm=[a-zA-Z0-9\\-]+>>.*")) {
//			p.sendMessage("true1");
//		} else {
//			p.sendMessage("false1");
//		}
		
		
//		if (message.matches("<<perm=[0-9a-zA-Z]+]>>")) {
//			p.sendMessage("aaa");
//		}
//		Pattern pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
//		if (message.matches("^[a-zA-Z]+$")) {
//			p.sendMessage("bbb");
//		}
//		if ("abcde <<perm=ads>>".matches("^<<perm=[0-9a-zA-Z+_.-]+>>$")) p.sendMessage("ccc");
//		if ("ab cde <<>> test=d".matches("test=[0-9a-zA-Z+_.-]")) p.sendMessage("ddd");
		
		p.sendMessage(chatColors.colors(message, true));
	}
	
}