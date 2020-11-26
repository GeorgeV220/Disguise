package com.georgev22.disguise.utilities;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

public enum MessagesUtil {

    /*
     */

    NO_PERMISSION("No Permission", "&c&l(!) &cYou do not have the correct permissions to do this!"),

    ONLY_PLAYER_COMMAND("Only Player Command", "&c&l(!) &cOnly players can run this command!"),

    WAIT("Wait", "&ePlease wait &6%seconds%s"),

    DISGUISE_INVALID_USAGE("Disguise.Invalid Usage", "&eInvalid Usage, use &6/disguise set <name> &Eor &6/disguise reset&e or &6/disguise random&e or &6/disguise update&e."),

    DISGUISE_INVALID_USAGE_SET("Disguise.Invalid Usage Set", "Invalid usage. /disguise set <player>"),

    DISGUISE_ALREADY("Disguise.Already", "&eYou are already disguised. Please remove your disguise and try again."),

    DISGUISE_ACCOUNT_DOES_NOT_EXIST("Disguise.Account Does Not Exist", "&eA premium account with this username does not exist. You have received a random premium username."),

    DISGUISE_PLAYED_BEFORE("Disguise.Player Played Before", "&eThis username belongs to a player that has played before. You have received a random premium username."),

    DISGUISE_SUCCESSFULLY("Disguise.Successfully disguised", "&eSuccessfully disguised to %name%"),

    DISGUISE_REMOVED("Disguise.Removed", "&eYour disguise has been removed!"),

    DISGUISE_ERROR("Disguise.Error", "&eThere was an error resetting your disguise! Have you set your disguise yet?"),

    DISGUISE_RESET("Disguise.Reset", "&eUse /disguise reset first!"),

    DISGUISE_SET("Disguise.Set", "&6Your disguise has been updated!"),

    SKIN_INVALID_USAGE("Skin.Invalid Usage", "&eInvalid Usage, use /skin help"),

    SKIN_INVALID_USAGE_SET("Skin.Invalid Usage Set", "&eInvalid Usage, use /skin set <skinName>"),

    SKIN_ACCOUNT_DOES_NOT_EXIST("Skin.Account Does Not Exist", "&eA premium account with this username does not exist."),

    SKIN_SUCCESSFULLY("Skin.Successfully changed", "&eSuccessfully changed skin to %name%"),

    SKIN_RESET("Skin.Reset", "&eYour skin has been resetted!"),

    SKIN_UPDATE("Skin.Update", "&eYour skin has been updated!"),

    ;

    private final String path;
    /**
     * @see #getMessages()
     */
    private String[] messages;

    MessagesUtil(final String path, final String... messages) {
        this.messages = messages;
        this.path = path;
    }

    /**
     * @param cfg
     */
    public static void repairPaths(final CFG cfg) {

        boolean changed = false;

        for (MessagesUtil enumMessage : MessagesUtil.values()) {

            /* Does our file contain our path? */
            if (cfg.getFileConfiguration().contains(enumMessage.getPath())) {
                /* It does! Let's set our message to be our path. */
                setPathToMessage(cfg, enumMessage);
                continue;
            }

            /* Since the path doesn't exist, let's set our default message to that path. */
            setMessageToPath(cfg, enumMessage);
            if (!changed) {
                changed = true;
            }

        }
        /* Save the custom yaml file. */
        if (changed) {
            cfg.saveFile();
        }
    }

    /**
     * Sets a message from the MessagesX enum to the file.
     *
     * @param cfg
     * @param enumMessage
     */
    private static void setMessageToPath(final CFG cfg, final MessagesUtil enumMessage) {
        /* Is our message multilined? */
        if (enumMessage.isMultiLined()) {
            /* Set our message (array) to the path. */
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages());
        } else {
            /* Set our message (string) to the path. */
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages()[0]);
        }
    }

    /**
     * Sets the current MessagesX messages to a string/list retrieved from the
     * messages file.
     *
     * @param cfg
     * @param enumMessage
     */
    private static void setPathToMessage(final CFG cfg, final MessagesUtil enumMessage) {
        /* Is our path a list? */
        if (Utils.isList(cfg.getFileConfiguration(), enumMessage.getPath())) {
            /* Set our default message to be the path's message. */
            enumMessage.setMessages(
                    cfg.getFileConfiguration().getStringList(enumMessage.getPath()).toArray(new String[0]));
        } else {
            /* Set our default message to be the path's message. */
            enumMessage.setMessages(cfg.getFileConfiguration().getString(enumMessage.getPath()));
        }
    }

    /**
     * @return boolean -> Whether or not the messages array contains more than 1
     * element. If true, it's more than 1 message/string.
     */
    private boolean isMultiLined() {
        return this.messages.length > 1;
    }

    /**
     * @return the path -> The path of the enum in the file.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @return the messages -> The messages array that contains all strings.
     */
    public String[] getMessages() {
        return this.messages;
    }

    /**
     * Sets the current messages to a different string array.
     *
     * @param messages
     */
    public void setMessages(final String[] messages) {
        this.messages = messages;
    }

    /**
     * Sets the string message to a different string assuming that the array has
     * only 1 element.
     *
     * @param messages
     */
    public void setMessages(final String messages) {
        this.messages[0] = messages;
    }

    /**
     * @param target
     * @see #msg(CommandSender, Map, boolean)
     */
    public void msg(final CommandSender target) {
        msg(target, null, false);
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     *
     * @param target
     * @param map
     */
    public void msg(final CommandSender target, final Map<String, String> map, final boolean ignoreCase) {
        if (this.isMultiLined()) {
            Utils.msg(target, this.getMessages(), map, ignoreCase);
        } else {
            Utils.msg(target, this.getMessages()[0], map, ignoreCase);
        }
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     */
    public void msgAll() {
        if (this.isMultiLined()) {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()));
        } else {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0]));
        }
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     *
     * @param map
     */
    public void msgAll(final Map<String, String> map, final boolean ignoreCase) {
        if (this.isMultiLined()) {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages(), map, ignoreCase));
        } else {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0], map, ignoreCase));
        }
    }

}
