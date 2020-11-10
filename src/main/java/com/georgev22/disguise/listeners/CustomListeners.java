package com.georgev22.disguise.listeners;

import com.georgev22.disguise.utilities.Utils;
import com.georgev22.disguise.events.DisguiseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author GeorgeV22
 */
public class CustomListeners implements Listener {

    @EventHandler
    public void onDisguise(DisguiseEvent event) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.hasPermission("disguise.notify")) {
                if (event.isDisguised())
                    Utils.msg(all, "Player " + event.getOldUsername() + " disguised as " + event.getName());
                else
                    Utils.msg(all, "Player " + event.getOldUsername() + " removed his disguise [" + event.getName() + "]");
            }
        }
    }

}
