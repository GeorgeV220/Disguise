package com.georgev22.disguise.commands;

import com.georgev22.disguise.Cooldown;
import com.georgev22.disguise.Main;
import com.georgev22.disguise.manager.NickManager;
import com.georgev22.disguise.manager.NickUser;
import com.georgev22.disguise.utilities.FileManager;
import com.georgev22.disguise.utilities.MessagesUtil;
import com.georgev22.disguise.utilities.SkinUtils;
import com.georgev22.disguise.utilities.Utils;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;


/**
 * @author GeorgeV22
 */
public class Disguise implements CommandExecutor {

    private static final NickManager nm = NickManager.getInstance();
    private final Main m = Main.getInstance();
    private final Map<String, String> placeholders = Maps.newHashMap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Cooldown cooldown = new Cooldown(player.getUniqueId(), "disguiseSet", m.getConfig().getInt("options.cooldown.disguise"));
        if (!sender.hasPermission("disguise.use")) {
            MessagesUtil.NO_PERMISSION.msg(sender);
            return true;
        }

        if (args.length == 0) {
            MessagesUtil.DISGUISE_INVALID_USAGE.msg(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 1) {
                MessagesUtil.DISGUISE_INVALID_USAGE_SET.msg(sender);
                return true;
            }
            NickUser user = nm.getNickedPlayers().get(player.getUniqueId());

            if (nm.getNickedPlayers().containsKey(player.getUniqueId())) {
                MessagesUtil.DISGUISE_ALREADY.msg(sender);
                return true;
            }

            if (user == null) {
                user = new NickUser(player);
                nm.getNickedPlayers().put(player.getUniqueId(), user);
            }
            if (sender.hasPermission("disguise.set")) {
                if (Cooldown.isInCooldown(player.getUniqueId(), "disguiseSet")) {
                    if (!player.isOp() || !player.hasPermission("disguise.cooldown.bypass"))
                        cooldown.start();
                    if (!Utils.isUsernamePremium(args[1])) {
                        user.setNick(NickManager.getName(), m.getConfig().getBoolean("options.random"), true);
                        MessagesUtil.DISGUISE_ACCOUNT_DOES_NOT_EXIST.msg(sender);
                        return true;
                    }
                    if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        user.setNick(NickManager.getName(), m.getConfig().getBoolean("options.random"), true);
                        MessagesUtil.DISGUISE_PLAYED_BEFORE.msg(sender);
                        return true;
                    }

                    user.setNick(args[1], m.getConfig().getBoolean("options.random"), true);

                    placeholders.put("%name%", args[1]);

                    MessagesUtil.DISGUISE_SUCCESSFULLY.msg(sender, placeholders, true);
                } else {
                    int input = Cooldown.getTimeLeft(player.getUniqueId(), "disguiseSet");

                    placeholders.put("%seconds%", String.valueOf(input));

                    MessagesUtil.WAIT.msg(sender, placeholders, true);
                }
                placeholders.clear();
            } else {
                MessagesUtil.NO_PERMISSION.msg(sender);
            }
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (NickManager.getInstance().resetPlayer(player)) {
                MessagesUtil.DISGUISE_REMOVED.msg(sender);
            } else {
                MessagesUtil.DISGUISE_ERROR.msg(sender);
            }
        } else if (args[0].equalsIgnoreCase("random")) {
            if (!sender.hasPermission("disguise.set.random")) {
                MessagesUtil.NO_PERMISSION.msg(sender);
                return true;
            }
            if (Cooldown.isInCooldown(player.getUniqueId(), "disguiseSet")) {
                if (!player.isOp() || !player.hasPermission("disguise.cooldown.bypass"))
                    cooldown.start();
                NickUser user = nm.getNickedPlayers().get(player.getUniqueId());

                if (nm.getNickedPlayers().containsKey(player.getUniqueId())) {
                    MessagesUtil.DISGUISE_RESET.msg(sender);
                    return true;
                }

                if (user == null) {
                    user = new NickUser(player);
                    nm.getNickedPlayers().put(player.getUniqueId(), user);
                }
                if (sender.hasPermission("disguise.random")) {
                    user.setNick(NickManager.getName(), m.getConfig().getBoolean("options.random"), true);
                } else {
                    MessagesUtil.NO_PERMISSION.msg(sender);
                }
            } else {
                int input = Cooldown.getTimeLeft(player.getUniqueId(), "disguiseSet");
                placeholders.put("%seconds%", String.valueOf(input));

                MessagesUtil.WAIT.msg(sender, placeholders, true);
            }
            placeholders.clear();
        } else if (args[0].equalsIgnoreCase("update")) {
            SkinUtils.changeSkinAsync(player, player.getName());

            MessagesUtil.DISGUISE_SET.msg(sender);
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("disguise.reload")) {
                MessagesUtil.NO_PERMISSION.msg(sender);
                return true;
            }
            FileManager fileManager = FileManager.getInstance();
            fileManager.getConfig().reloadFile();
        } else {
            MessagesUtil.DISGUISE_INVALID_USAGE.msg(sender);
        }
        return true;
    }

}

