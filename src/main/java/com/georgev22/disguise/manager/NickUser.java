package com.georgev22.disguise.manager;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.utilities.Utils;
import com.georgev22.disguise.events.DisguiseEvent;
import com.georgev22.disguise.utilities.SkinUtils;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author GeorgeV22
 */
public class NickUser {
    private String name = "";
    private final String oldUsername;

    private final Player player;
    private final Chat chat = Main.getChat();

    private final Main m = Main.getInstance();

    public NickUser(final Player player) {
        this.player = player;
        this.oldUsername = player.getName();
    }


    /**
     * Set player disguise
     *
     * @param name Player name
     */
    public void setNick(String name, boolean random) {
        Bukkit.getServer().getPluginManager().callEvent(new DisguiseEvent(name, oldUsername, true));
        this.name = name;
        SkinUtils.changeName(name, player);
        updatePlayer();
        SkinUtils.changeSkinAsync(player, name);
        if (random) {
            String b = (String) Utils.getRandomElement(m.getConfig().getConfigurationSection("options.randomRanks").getKeys(false));
            setChat(m.getConfig().getString("options.randomRanks." + b + ".chat"));
            setNameTag(m.getConfig().getString("options.randomRanks." + b + ".tag"));
        } else {
            setChat(chat.getGroupPrefix(player.getWorld(), m.getConfig().getString("options.defaultRank")));
            setNameTag(m.getConfig().getString("options.defaultTag"));
        }


        m.getEconomy().withdrawPlayer(name, m.getEconomy().getBalance(name));

        m.getEconomy().depositPlayer(name, m.getEconomy().getBalance(oldUsername));

    }


    /**
     * Reset player disguise
     */
    public void resetNick() {
        SkinUtils.changeName(oldUsername, player);
        updatePlayer();
        SkinUtils.changeSkinAsync(player, oldUsername);
        resetNameTag();
        resetChat();
        m.getEconomy().withdrawPlayer(oldUsername, m.getEconomy().getBalance(oldUsername));
        m.getEconomy().depositPlayer(oldUsername, m.getEconomy().getBalance(name));
        m.getEconomy().withdrawPlayer(name, m.getEconomy().getBalance(name));
        Bukkit.getServer().getPluginManager().callEvent(new DisguiseEvent(name, oldUsername, false));
    }

    public void updatePlayer() {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId() != player.getUniqueId()).forEach(p -> {
            try {
                Bukkit.getServer().getScheduler().runTask(Main.getInstance(), () -> p.hidePlayer(player));
                Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> p.showPlayer(player), 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set player name tag
     */
    public void setNameTag(String nameTag) {
        if (m.getConfig().getBoolean("options.hooks.NameTagEdit")) {
            com.nametagedit.plugin.NametagEdit.getApi().setPrefix(Bukkit.getPlayer(oldUsername), nameTag);
        }
    }

    /**
     * Set player chat
     */
    public void setChat(String groupPrefix) {
        chat.setPlayerPrefix(player, groupPrefix);
    }

    /**
     * Reset player name tag
     */
    public void resetNameTag() {
        if (m.getConfig().getBoolean("options.hooks.NameTagEdit")) {
            com.nametagedit.plugin.NametagEdit.getApi().reloadNametag(player);
        }
    }

    /**
     * Reset player chat
     */
    public void resetChat() {
        Chat chat = Main.getChat();
        chat.setPlayerPrefix(player, chat.getGroupPrefix(player.getWorld(), chat.getPrimaryGroup(player)));
    }

    public String getOldUsername() {
        return this.oldUsername;
    }

    public Player getPlayer() {
        return player;
    }
}
