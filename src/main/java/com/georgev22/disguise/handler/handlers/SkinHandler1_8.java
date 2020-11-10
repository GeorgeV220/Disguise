package com.georgev22.disguise.handler.handlers;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.utilities.Utils;
import com.georgev22.disguise.handler.SkinHandler;
import com.georgev22.disguise.handler.SkinUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

/**
 * @author GeorgeV22
 */
public class SkinHandler1_8 implements SkinHandler {

    @Override
    public void updateSkin(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        EntityPlayer ep = cp.getHandle();

        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);

        WorldServer world = (WorldServer) ep.getWorld();
        EnumDifficulty difficulty = world.getDifficulty();
        WorldType worldType = world.worldData.getType();

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(world.dimension, difficulty, worldType, ep.playerInteractManager.getGameMode());
        PacketPlayOutPosition position = new PacketPlayOutPosition(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch(),
                Collections.emptySet()
        );
        PacketPlayOutHeldItemSlot slot = new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot());

        Bukkit.getServer().getScheduler().runTask(Main.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }

            ep.playerConnection.sendPacket(removeInfo);
            ep.playerConnection.sendPacket(addInfo);
            ep.playerConnection.sendPacket(respawn);
            ep.playerConnection.sendPacket(position);
            ep.playerConnection.sendPacket(slot);

            ep.updateAbilities();
            SkinUtils.updateData(player);
        });
    }

    public void reloadSkin(Player player) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        final PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        final PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        final Location loc = player.getLocation().clone();
        ep.playerConnection.sendPacket(removeInfo);
        ep.playerConnection.sendPacket(addInfo);
        player.teleport(loc);
        ep.playerConnection.sendPacket(new PacketPlayOutRespawn(ep.dimension, ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode()));
        player.updateInventory();
    }

    @Override
    public void changeSkinAsync(Player player, String skinName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PropertyMap propertyMap = ((CraftPlayer) player).getProfile().getProperties();
                propertyMap.removeAll("textures");

                if (SkinUtils.cache.containsKey(skinName)) {
                    propertyMap.put("textures", new Property("textures", SkinUtils.cache.get(skinName).get("value").getAsString(), SkinUtils.cache.get(skinName).get("signature").getAsString()));
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
                        SkinUtils.cache.put(skinName, textureProperty);
                    } catch (IOException e) {
                        Utils.msg(player, "An error has occurred");
                    }
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> updateSkin(player));
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
