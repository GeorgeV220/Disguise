package com.georgev22.disguise.commands;

import com.georgev22.disguise.Cooldown;
import com.georgev22.disguise.Main;
import com.georgev22.disguise.events.SkinEvent;
import com.georgev22.disguise.utilities.SkinUtils;
import com.georgev22.disguise.manager.SkinManager;
import com.georgev22.disguise.manager.SkinUser;
import com.georgev22.disguise.utilities.MessagesUtil;
import com.georgev22.disguise.utilities.Utils;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;

/**
 * @author GeorgeV22
 */
public class Skin implements CommandExecutor {

    private final Main m = Main.getInstance();
    private final Map<String, String> placeholders = Maps.newHashMap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skins.skin")) {
            MessagesUtil.NO_PERMISSION.msg(sender);
            return true;
        }
        if (args.length == 0) {
            MessagesUtil.SKIN_INVALID_USAGE.msg(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("skins.set")) {
                MessagesUtil.NO_PERMISSION.msg(sender);
                return true;
            }
            if (args.length == 1) {
                MessagesUtil.SKIN_INVALID_USAGE_SET.msg(sender);
                return true;
            }
            Player player = (Player) sender;
            try {
                if (!Utils.isUsernamePremium(args[1])) {
                    MessagesUtil.SKIN_ACCOUNT_DOES_NOT_EXIST.msg(sender);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            SkinUser skinUser = SkinManager.getSkinManager().getMap().get(player.getUniqueId());
            if (skinUser == null) {
                skinUser = new SkinUser(player);
                skinUser.setOldSkin(player.getName());
                SkinManager.getSkinManager().getMap().put(player.getUniqueId(), skinUser);
            }
            Cooldown cooldown = new Cooldown(player.getUniqueId(), "skinSet", m.getConfig().getInt("options.cooldown.skin"));
            if (Cooldown.isInCooldown(player.getUniqueId(), "skinSet")) {
                skinUser.setNewSkin(args[1]);
                SkinUtils.changeSkinAsync(player, args[1]);
                Bukkit.getServer().getPluginManager().callEvent(new SkinEvent(player, skinUser.getNewSkin(), false));

                placeholders.put("%name%", args[1]);

                MessagesUtil.SKIN_SUCCESSFULLY.msg(sender, placeholders, true);

                if (!player.isOp() || !player.hasPermission("skins.cooldown.bypass"))
                    cooldown.start();
            } else {
                int input = Cooldown.getTimeLeft(player.getUniqueId(), "skinSet");
                placeholders.put("%seconds%", String.valueOf(input));

                MessagesUtil.WAIT.msg(sender, placeholders, true);
            }
            placeholders.clear();
            return true;
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (!sender.hasPermission("skins.reset")) {
                MessagesUtil.NO_PERMISSION.msg(sender);
                return true;
            }
            Player player = (Player) sender;
            SkinUser skinUser = SkinManager.getSkinManager().getMap().get(player.getUniqueId());

            if (skinUser == null) {
                skinUser = new SkinUser(player);
                skinUser.setOldSkin(player.getName());
                SkinManager.getSkinManager().getMap().put(player.getUniqueId(), skinUser);
            }
            skinUser.setNewSkin(player.getName());
            Bukkit.getServer().getPluginManager().callEvent(new SkinEvent(player, skinUser.getNewSkin(), false));
            SkinUtils.changeSkinAsync(player, skinUser.getOldSkin());

            MessagesUtil.SKIN_RESET.msg(sender);
        } else if (args[0].equalsIgnoreCase("update")) {
            if (!sender.hasPermission("skins.update")) {
                MessagesUtil.NO_PERMISSION.msg(sender);
                return true;
            }
            Player player = (Player) sender;
            SkinUser skinUser = SkinManager.getSkinManager().getMap().get(player.getUniqueId());

            if (skinUser == null) {
                skinUser = new SkinUser(player);
                skinUser.setOldSkin(player.getName());
                SkinManager.getSkinManager().getMap().put(player.getUniqueId(), skinUser);
            }
            skinUser.setNewSkin(player.getName());
            SkinUtils.changeSkinAsync(player, skinUser.getNewSkin());
            Bukkit.getServer().getPluginManager().callEvent(new SkinEvent(player, skinUser.getNewSkin(), true));
            MessagesUtil.SKIN_UPDATE.msg(sender);
        }

        return true;
    }
}
