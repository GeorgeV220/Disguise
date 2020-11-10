package com.georgev22.disguise.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author GeorgeV22
 */
public class SkinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String skinName;
    private final boolean update;

    public SkinEvent(Player player, String skinName, boolean update) {
        this.player = player;
        this.skinName = skinName;
        this.update = update;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public String getSkinName() {
        return skinName;
    }

    public boolean isUpdate() {
        return update;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
