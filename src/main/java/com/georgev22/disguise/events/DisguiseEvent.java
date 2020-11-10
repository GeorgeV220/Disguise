package com.georgev22.disguise.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author GeorgeV22
 */
public class DisguiseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final String oldUsername;
    private final boolean disguised;

    public DisguiseEvent(String name, String oldUsername, boolean disguised) {
        this.name = name;
        this.oldUsername = oldUsername;
        this.disguised = disguised;
    }

    public String getName() {
        return name;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public boolean isDisguised() {
        return disguised;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
