package com.georgev22.disguise.handler;

import org.bukkit.entity.Player;

/**
 * @author GeorgeV22
 */
public interface SkinHandler {

    void updateSkin(Player player);

    void changeSkinAsync(Player player, String skinName);
}
