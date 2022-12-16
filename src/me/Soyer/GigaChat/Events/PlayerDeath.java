package me.Soyer.GigaChat.Events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.Soyer.GigaChat.Main;
import me.Soyer.GigaChat.Utils.ChatColors;
import me.Soyer.GigaChat.Utils.Utils;

public class PlayerDeath implements Listener {
	
	private Main main;
	private ChatColors chatColors = new ChatColors();
	
	public PlayerDeath(Main main) {
		main.getServer().getPluginManager().registerEvents(this,  main);
		this.main = main;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Utils utils = new Utils(main);
		Player p = e.getEntity().getPlayer();
		LivingEntity k = e.getEntity().getKiller();
		String record = utils.saveType((OfflinePlayer)p);
		utils.checkDatabase((OfflinePlayer)p);
		if (main.getConfig().getBoolean("death-message-enabled")) {
			e.setDeathMessage(null);
			if (k instanceof Player) {
				
			} else {
				if (main.getConfig().getBoolean("not-player-caused-death")) {
					
				}
			}
		}
	}

}
