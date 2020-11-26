package com.georgev22.disguise.utilities;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.handler.SkinHandler;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

/**
 * @author GeorgeV22
 */
public class SkinUtils {

    public static Map<String, JsonObject> cache = Maps.newHashMap();
    private static SkinHandler skinHandler = null;
    private static JsonObject textureProperty;

    /**
     * Initialize the SkinHandler base on server version
     *
     * @return SkinHandler
     */
    public static SkinHandler initialize() {
        try {
            skinHandler = (SkinHandler) Class.forName("com.georgev22.disguise.handler.handlers.SkinHandler_" + Utils.getVersion()).newInstance();
            Bukkit.getLogger().info("[Disguise] Using skin handler: " + skinHandler.getClass().getName());
            return skinHandler;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Bukkit.getLogger().warning("[Disguise] Version " + Utils.getVersion() + " is not supported or something bad happened." +
                    "\nPlease report this error to https://github.com/GeorgeV220/Disguise/issues");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the skin handler
     *
     * @return SkinHandler
     */
    public static SkinHandler getSkinHandler() {
        return skinHandler != null ? skinHandler : initialize();
    }


    /**
     * Updates the Player's data
     *
     * @param player set player's data
     */
    public static void updateData(Player player) {
        Bukkit.getServer().getScheduler().runTask(Main.getInstance(), () -> {
            player.updateInventory();
            player.setExp(player.getExp());
            player.setLevel(player.getLevel());
            player.setHealth(player.getHealth());
            player.setFlying(player.isFlying());
            player.setPlayerListName(player.getPlayerListName());
        });
    }

    /**
     * Works from 1.8+.
     *
     * @param name   new name of the player
     * @param player player to change the name of
     */
    public static void changeName(String name, Player player) {

        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(player);
                players.showPlayer(player);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the local or remote(Database) cache
     *
     * @param skinName name of the skin
     */
    public static void updateCache(String skinName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url_0;
                    url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
                    InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
                    String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

                    URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                    textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                    if (cache.containsKey(skinName) && cache.get(skinName).equals(textureProperty)) {
                        return;
                    }
                    cache.remove(skinName);
                    cache.put(skinName, textureProperty);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    /**
     * Changes the skin async (download the texture from Mojang sites async and then change the skin sync)
     *
     * @param player   player to change the skin of
     * @param skinName name of the skin (premium account with that name must exists)
     */
    public static void changeSkinAsync(Player player, String skinName) {
        skinHandler.changeSkinAsync(player, skinName);
    }
}
