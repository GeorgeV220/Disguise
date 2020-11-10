package com.georgev22.disguise.utilities;

import org.bukkit.plugin.java.JavaPlugin;

public final class FileManager {

    private static FileManager instance;
    private CFG config;
    private CFG usernames;
    private CFG messages;
    private CFG skins;
    private CFG cache;

    private FileManager() {
    }

    public static FileManager getInstance() {
        return instance == null ? instance = new FileManager() : instance;
    }

    public void loadFiles(final JavaPlugin plugin) {
        this.messages = new CFG(plugin, "messages", false);
        this.config = new CFG(plugin, "config", true);
        this.usernames = new CFG(plugin, "usernames", true);
        this.skins = new CFG(plugin, "skins", false);
        this.cache = new CFG(plugin, "cache", false);
    }

    public CFG getMessages() {
        return messages;
    }

    public CFG getConfig() {
        return config;
    }

    public CFG getUsernames() {
        return usernames;
    }

    public CFG getSkins() {
        return skins;
    }

    public CFG getCache() {
        return cache;
    }
}
