package com.georgev22.disguise.listeners;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.manager.NickManager;
import com.georgev22.disguise.manager.NickUser;
import com.georgev22.disguise.manager.SkinManager;
import com.georgev22.disguise.manager.SkinUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final Main m = Main.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLeave(PlayerQuitEvent event) {
        NickUser user = NickManager.getInstance().getNickedPlayers().get(event.getPlayer().getUniqueId());
        if (user == null) {
            return;
        }
        //Bukkit.getServer().getPluginManager().callEvent(new DisguiseEvent(event.getPlayer().getName(), user.getOldUsername(), false));
        NickManager.getInstance().resetPlayer(event.getPlayer());
        //NickManager.getInstance().getNickedPlayers().remove(event.getPlayer().getUniqueId());

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        SkinUser skinUser = SkinManager.getSkinManager().getMap().get(event.getPlayer().getUniqueId());
        if (skinUser == null) {
            return;
        }

        m.getSkin().set("Skin." + event.getPlayer().getName() + ".newSkin", skinUser.getNewSkin());
        m.getSkin().set("Skin." + event.getPlayer().getName() + ".oldSkin", skinUser.getOldSkin());
        m.saveSkin();
    }

}
