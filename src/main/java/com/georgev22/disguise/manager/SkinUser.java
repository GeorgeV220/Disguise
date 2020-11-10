package com.georgev22.disguise.manager;

import org.bukkit.entity.Player;

/**
 * @author GeorgeV22
 */
public class SkinUser {

    final Player player;

    private String oldSkin = "";
    private String newSkin = "";

    public SkinUser(final Player player) {
        this.player = player;
    }

    public void setOldSkin(String oldSkin) {
        this.oldSkin = oldSkin;
    }

    public void setNewSkin(String newSkin) {
        this.newSkin = newSkin;
    }

    public String getNewSkin() {
        return newSkin;
    }

    public String getOldSkin() {
        return oldSkin;
    }

    public Player getPlayer() {
        return player;
    }
}
