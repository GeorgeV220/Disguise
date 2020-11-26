package com.georgev22.disguise.listeners;

import com.georgev22.disguise.events.DisguiseEvent;
import com.georgev22.disguise.events.SkinEvent;
import com.georgev22.disguise.utilities.Utils;
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

    @EventHandler
    public void onSkin(SkinEvent event) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.hasPermission("skin.notify")) {
                if (event.isUpdate())
                    Utils.msg(all, "Player " + event.getPlayer().getName() + " update his skin!");
                else
                    Utils.msg(all, "Player " + event.getPlayer().getName() + " set his skin to " + event.getSkinName());
            }
        }
    }

}
