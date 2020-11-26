package com.georgev22.disguise;

import com.georgev22.disguise.commands.Disguise;
import com.georgev22.disguise.commands.Skin;
import com.georgev22.disguise.utilities.SkinUtils;
import com.georgev22.disguise.listeners.CustomListeners;
import com.georgev22.disguise.listeners.LeaveListener;
import com.georgev22.disguise.listeners.LoginListener;
import com.georgev22.disguise.manager.NickManager;
import com.georgev22.disguise.utilities.FileManager;
import com.georgev22.disguise.utilities.MessagesUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * @author GeorgeV22
 */
public class Main extends JavaPlugin {

    private static Chat chat = null;
    private Economy econ = null;

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }

    @Override
    public void onEnable() {
        final FileManager fm = FileManager.getInstance();
        fm.loadFiles(this);
        MessagesUtil.repairPaths(fm.getMessages());
        setupEconomy();
        if (setupChat()) {
            Bukkit.getLogger().info("Vault Chat hooked on Disguise " + getDescription().getVersion());
        }
        SkinUtils.initialize();
        NickManager.loadUsernames();
        getCommand("disguise").setExecutor(new Disguise());
        getCommand("skin").setExecutor(new Skin());
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomListeners(), this);


        if (getCache().get("DB") != null) {
            for (String key : getCache().getConfigurationSection("DB").getKeys(false)) {
                SkinUtils.cache.put(key, new JsonParser().parse(getCache().getString("DB." + key)).getAsJsonObject());
            }
        }

    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, JsonObject> entry : SkinUtils.cache.entrySet()) {
            getCache().set("DB." + entry.getKey(), entry.getValue().toString());
        }
        FileManager.getInstance().getCache().saveFile();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    /**
     * @return b
     */
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public static Chat getChat() {
        return chat;
    }

    public FileConfiguration getConfig() {
        return FileManager.getInstance().getConfig().getFileConfiguration();
    }

    public FileConfiguration getUsername() {
        return FileManager.getInstance().getUsernames().getFileConfiguration();
    }

    public FileConfiguration getCache() {
        return FileManager.getInstance().getCache().getFileConfiguration();
    }

    public FileConfiguration getSkin() {
        return FileManager.getInstance().getSkins().getFileConfiguration();
    }

    public void saveSkin() {
        FileManager.getInstance().getSkins().saveFile();
    }
}
