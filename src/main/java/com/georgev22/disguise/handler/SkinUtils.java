package com.georgev22.disguise.handler;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.ReflectionUtil;
import com.georgev22.disguise.handler.handlers.*;
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
import java.util.Collection;
import java.util.Map;

/**
 * @author GeorgeV22
 */
public class SkinUtils {

    public static Map<String, JsonObject> cache = Maps.newHashMap();
    private static SkinHandler skinHandler;
    private static JsonObject textureProperty;

    public static void initialize() {
        switch (ReflectionUtil.getVersion()) {
            case "v1_8_R3":
                skinHandler = new SkinHandler1_8();
                break;
            case "v1_9_R1":
                skinHandler = new SkinHandler1_9_R1();
                break;
            case "v1_9_R2":
                skinHandler = new SkinHandler1_9_R2();
                break;
            case "v1_10_R1":
                skinHandler = new SkinHandler1_10_R1();
                break;
            case "v1_1v1_R1":
                skinHandler = new SkinHandler1_11_R1();
                break;
            case "v1_12_R1":
                skinHandler = new SkinHandler1_12_R1();
                break;
            case "v1_13_R1":
                skinHandler = new SkinHandler1_13_R1();
                break;
            case "v1_13_R2":
                skinHandler = new SkinHandler1_13_R2();
                break;
            case "v1_14_R1":
                skinHandler = new SkinHandler1_14_R1();
                break;
            case "v1_15_R1":
                skinHandler = new SkinHandler1_15_R1();
                break;
            case "v1_16_R1":
                skinHandler = new SkinHandler1_16_R1();
                break;
            case "v1_16_R2":
                skinHandler = new SkinHandler1_16_R2();
                break;
            default:
                throw new IllegalStateException("Version " + ReflectionUtil.getVersion() + " is not supported!");
        }
    }

    public static SkinHandler getSkinHandler() {
        return skinHandler;
    }

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
     * Works from 1.0+.
     *
     * @param name   new name of the player
     * @param player player to change the name of
     */
    @SuppressWarnings("unchecked")
    public static void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            boolean gameProfileExists = false;
            try {
                Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            try {
                Class.forName("com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            if (!gameProfileExists) {
                Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(entityPlayer, name);
            } else {
                Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                Field ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, name);
            }

            if (Bukkit.class.getMethod("getOnlinePlayers").getReturnType() == Collection.class) {
                Collection<? extends Player> players = (Collection<? extends Player>) Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            } else {
                Player[] players = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers").invoke(null));
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

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
     *
     */
    /*public static void changeSkin(Player player, String skinName) {
        PropertyMap propertyMap = ((CraftPlayer) player).getProfile().getProperties();
        propertyMap.removeAll("textures");

        if (cache.containsKey(skinName)) {
            propertyMap.put("textures", new Property("textures", cache.get(skinName).get("value").getAsString(), cache.get(skinName).get("signature").getAsString()));
        } else {
            InputStreamReader reader_1;
            try {
                URL url_0;
                url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
                InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
                String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

                URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                reader_1 = new InputStreamReader(url_1.openStream());
                JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                String texture = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();
                propertyMap.put("textures", new Property("textures", texture, signature));
                cache.put(skinName, textureProperty);
            } catch (IOException e) {
                Utils.msg(player, "An error has occurred");
            }
        }
        skinHandler.updateSkin(player);
    }
    */
    public static void changeSkinAsync(Player player, String skinName) {
        skinHandler.changeSkinAsync(player, skinName);
    }
}
