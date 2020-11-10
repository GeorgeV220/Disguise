package com.georgev22.disguise.manager;

import com.georgev22.disguise.Main;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author GeorgeV22
 */
public class NickManager {

    private static NickManager nickManager;
    public static List<String> usernames = Lists.newArrayList();


    public static void loadUsernames() {
        usernames.addAll(Main.getInstance().getUsername().getStringList("username"));
    }

    public static NickManager getInstance() {
        return nickManager == null ? nickManager = new NickManager() : nickManager;
    }


    private final Map<UUID, NickUser> nickedPlayers = Maps.newHashMap();

    public Map<UUID, NickUser> getNickedPlayers() {
        return nickedPlayers;
    }

    public boolean resetPlayer(final Player player) {
        final NickUser user = nickedPlayers.remove(player.getUniqueId());
        if (user == null) {
            return false;
        }
        user.resetNick();
        return true;
    }

    public static String getName() {
        Random rand = new Random();

        return usernames.get(rand.nextInt(usernames.size()));
    }


}
