package com.georgev22.disguise.listeners;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.utilities.SkinUtils;
import com.georgev22.disguise.manager.SkinManager;
import com.georgev22.disguise.manager.SkinUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author GeorgeV22
 */
public class LoginListener implements Listener {

    private final Main m = Main.getInstance();

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        SkinUtils.updateCache(event.getPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SkinUser user = SkinManager.getSkinManager().getMap().get(event.getPlayer().getUniqueId());

        if (user == null) {
            user = new SkinUser(event.getPlayer());
            if (m.getSkin().getString("Skin." + event.getPlayer() + ".oldSkin") == null)
                user.setOldSkin(event.getPlayer().getName());
            else
                user.setOldSkin(m.getSkin().getString("Skin." + event.getPlayer() + ".oldSkin"));

            if (m.getSkin().getString("Skin." + event.getPlayer() + ".newSkin") == null)
                user.setNewSkin(event.getPlayer().getName());
            else
                user.setNewSkin(m.getSkin().getString("Skin." + event.getPlayer() + ".newSkin"));
            SkinManager.getSkinManager().getMap().put(event.getPlayer().getUniqueId(), user);
        }

        SkinUtils.changeSkinAsync(event.getPlayer(), user.getNewSkin());
    }

}
