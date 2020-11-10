package com.georgev22.disguise.utilities;

import com.georgev22.disguise.Main;
import com.georgev22.disguise.ReflectionUtil;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {
        throw new AssertionError();
    }


    public static List<Player> deathList = Lists.newArrayList();
    public static List<Player> opList = new ArrayList<>();

    public static String convertSeconds(long input, String secondInput, String secondsInput, String minuteInput,
                                        String minutesInput, String hourInput, String hoursInput, String dayInput, String daysInput,
                                        String invalidInput) {
        if (input < 0) {
            Utils.printMsg(
                    "An attempt to convert a negative number was made for: " + input + ", making the number absolute.");
            input = Math.abs(input);
        }

        final StringBuilder builder = new StringBuilder();

        boolean comma = false;

        /* Days */
        final long days = TimeUnit.SECONDS.toDays(input);
        if (days > 0) {
            builder.append(days + " " + (days == 1 ? dayInput : daysInput));
            comma = true;
        }

        /* Hours */
        final long hours = (TimeUnit.SECONDS.toHours(input) - TimeUnit.DAYS.toHours(days));
        if (hours > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(hours + " " + (hours == 1 ? hourInput : hoursInput));
            comma = true;
        }

        /* Minutes */
        final long minutes = (TimeUnit.SECONDS.toMinutes(input) - TimeUnit.HOURS.toMinutes(hours)
                - TimeUnit.DAYS.toMinutes(days));
        if (minutes > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(minutes + " " + (minutes == 1 ? minuteInput : minutesInput));
            comma = true;
        }

        /* Seconds */
        final long seconds = (TimeUnit.SECONDS.toSeconds(input) - TimeUnit.MINUTES.toSeconds(minutes)
                - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));
        if (seconds > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(seconds + " " + (seconds == 1 ? secondInput : secondsInput));
        }

        /* Result */
        final String result = builder.toString();
        return result.equals("") ? invalidInput : result;
    }

    // public static String convertSeconds(long input) {
    // return convertSeconds(input, "second", "seconds", "minute", "minutes",
    // "hour", "hours", "day", "days",
    // "invalid time");
    // }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */
    public static boolean isLong(final String input) {
        return Longs.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    public static boolean isDouble(final String input) {
        return Doubles.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    public static boolean isInt(final String input) {
        return Ints.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */
    public static boolean isList(final FileConfiguration file, final String path) {
        return isList(file.get(path));
    }

    public static boolean isList(final Object obj) {
        return obj instanceof List;
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */

    public static void broadcastMsg(final String input) {
        Bukkit.broadcastMessage(colorize(input));
    }

    public static void broadcastMsg(final List<String> input) {
        input.forEach(Utils::broadcastMsg);
    }

    public static void broadcastMsg(final Object input) {
        broadcastMsg(String.valueOf(input));
    }

    public static void printMsg(final String input) {
        Bukkit.getConsoleSender().sendMessage(colorize(input));
    }

    public static void printMsg(final List<String> input) {
        input.forEach(Utils::printMsg);
    }

    public static void printMsg(final Object input) {
        printMsg(String.valueOf(input));
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */

    public static void msg(final CommandSender target, final String message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null) {
            return;
        }
        target.sendMessage(colorize(Main.getPlugin(Main.class).getConfig().getString("options.prefix") + " " + message));
    }

    public static void msg(final CommandSender target, final String... message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null || message.length == 0) {
            return;
        }
        Validate.noNullElements(message, "The string array can't have null elements.");
        target.sendMessage(colorize(message));
    }

    public static void msg(final CommandSender target, final List<String> message) {
        Validate.notNull(target, "The target can't be null");
        if (message == null || message.isEmpty()) {
            return;
        }
        Validate.noNullElements(message, "The list can't have null elements.");
        msg(target, message.toArray(new String[0]));
    }

    /* ----------------------------------------------------------------- */

    public static void msg(final CommandSender target, final String message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeHolder(message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path) {
        msg(target, file, path, null, false);
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path,
                           final Map<String, String> map, final boolean replace) {
        Validate.notNull(file, "The file can't be null");
        Validate.notNull(file, "The path can't be null");

        if (!file.isSet(path)) {
            throw new IllegalArgumentException("The path: " + path + " doesn't exist.");
        }

        if (isList(file, path)) {
            msg(target, file.getStringList(path), map, replace);
        } else {
            msg(target, file.getString(path), map, replace);
        }
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */

    public static String placeHolder(String str, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(str, "The string can't be null!");
        if (map == null) {
            return str;
        }
        for (final Entry<String, String> entr : map.entrySet()) {
            str = ignoreCase ? replaceIgnoreCase(str, entr.getKey(), entr.getValue())
                    : str.replace(entr.getKey(), entr.getValue());
        }
        return str;
    }

    private static String replaceIgnoreCase(final String text, String searchString, final String replacement) {

        if (text == null || text.length() == 0) {
            return text;
        }
        if (searchString == null || searchString.length() == 0) {
            return text;
        }
        if (replacement == null) {
            return text;
        }

        int max = -1;

        final String searchText = text.toLowerCase();
        searchString = searchString.toLowerCase();
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = Math.max(increase, 0);
        increase *= 16;

        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        return buf.append(text, start, text.length()).toString();
    }

    public static String[] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        if (map == null) {
            return newarr;
        }
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = placeHolder(newarr[i], map, ignoreCase);
        }
        return newarr;
    }

    public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
                                           final boolean ignoreCase) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        return map == null ? coll
                : coll.stream().map(str -> placeHolder(str, map, ignoreCase)).collect(Collectors.toList());
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */

    /**
     * Returns a translated string.
     *
     * @param msg The message to be translated
     * @return A translated message
     */
    public static String colorize(final String msg) {
        Validate.notNull(msg, "The string can't be null!");
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static Object getRandomElement(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static Object getRandomElement(Set<String> set) {
        List<String> b = Lists.newArrayList();
        b.addAll(set);
        return getRandomElement(b);
    }

    /**
     * Returns a translated string array.
     *
     * @return A translated message array
     */
    public static String[] colorize(final String... array) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = colorize(newarr[i]);
        }
        return newarr;
    }

    /**
     * Returns a translated string collection.
     *
     * @param coll The collection to be translated
     * @return A translated message
     */
    public static List<String> colorize(final List<String> coll) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(Utils::colorize);
        return newColl;
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */

    public static void debug(final JavaPlugin plugin, final Map<String, String> map, String... messages) {
        Utils.printMsg("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        Utils.printMsg("=");
        final String name, desc;
        name = plugin.getDescription().getName() + " ";
        desc = plugin.getDescription().getVersion();
        Utils.printMsg("           " + name + desc);
        Utils.printMsg("=");
        for (final String msg : messages) {
            Utils.printMsg(placeHolder(msg, map, false));
        }
        Utils.printMsg("=");
        Utils.printMsg("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    public static void debug(final JavaPlugin plugin, String... messages) {
        debug(plugin, null, messages);
    }

    public static void debug(final JavaPlugin plugin, List<String> messages) {
        debug(plugin, null, messages.toArray(new String[0]));
    }

    public static ItemStack[] getItems(final ItemStack item, int amount) {

        final int maxSize = item.getMaxStackSize();
        if (amount <= maxSize) {
            item.setAmount(Math.max(amount, 1));
            return new ItemStack[]{item};
        }
        final List<ItemStack> resultItems = Lists.newArrayList();
        do {
            item.setAmount(Math.min(amount, maxSize));
            resultItems.add(new ItemStack(item));
            amount = amount >= maxSize ? amount - maxSize : 0;
        } while (amount != 0);
        return resultItems.toArray(new ItemStack[0]);
    }

    private static String formatNumber(double input) {
        Validate.notNull(Locale.US);
        return NumberFormat.getInstance(Locale.US).format(input);
    }

    public static String getProgressBar(double current, double max, int totalBars, String symbol, String completedColor,
                                        String notCompletedColor) {
        final double percent = (float) Math.min(current, max) / max;
        final int progressBars = (int) ((int) totalBars * percent);
        final int leftOver = totalBars - progressBars;

        final StringBuilder sb = new StringBuilder();

        sb.append(colorize(completedColor));
        for (int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }

        sb.append(colorize(notCompletedColor));
        for (int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        return sb.toString();
    }

    /**
     * Get the greatest values in a map
     *
     * @param map
     * @param n
     * @param <K>
     * @param <V>
     * @return Map
     */
    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v1.compareTo(v0);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }

    public static ItemStack resetItemMeta(final ItemStack item) {
        final ItemStack copy = item.clone();
        copy.setItemMeta(Bukkit.getItemFactory().getItemMeta(copy.getType()));
        return copy;
    }

    public static String convertItemStackToJson(ItemStack itemStack) {

        Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj;
        Object nmsItemStackObj;
        Object itemAsJsonObject;

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = Objects.requireNonNull(asNMSCopyMethod).invoke(null, itemStack);
            itemAsJsonObject = Objects.requireNonNull(saveNmsItemStackMethod).invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }

        return itemAsJsonObject.toString();
    }

    public static void sendItemTooltipMessage(Player player, String message, String text, ItemStack item) {
        String itemJson = convertItemStackToJson(item);

        BaseComponent[] hoverEventComponents = new BaseComponent[]{new TextComponent(itemJson)};

        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        TextComponent component = new TextComponent(text);
        component.setHoverEvent(event);

        TextComponent msg = new TextComponent(message);
        msg.addExtra(component);
        player.spigot().sendMessage(msg);
    }

    public static void removeItem(ItemStack item, Iterator<ItemStack> itemsx, Material Item, String displayName) {
        if ((item != null) && (item.getType() == Item) && (item.hasItemMeta()) && (item.getItemMeta().hasDisplayName())
                && (item.getItemMeta().getDisplayName().equals(Utils.colorize(displayName)))) {
            itemsx.remove();
        }
    }

    public static void removeItem(ItemStack item, Iterator<ItemStack> itemsx, Material Item) {
        if ((item != null) && (item.getType() == Item)) {
            itemsx.remove();
        }
    }


    public static boolean isCritical(Player player) {
        return player.getFallDistance() > 0.0F && !player.isOnGround() &&
                !player.isInsideVehicle() &&
                !player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                player.getLocation().getBlock().getType() != Material.LADDER &&
                player.getLocation().getBlock().getType() != Material.VINE;
    }

    public static List<Player> getDeathList() {
        return deathList;
    }

    public static List<Player> getOpList() {
        return opList;
    }

    public static void setDeathList(List<Player> deathList) {
        Utils.deathList = deathList;
    }

    public static void setOpList(List<Player> opList) {
        Utils.opList = opList;
    }

    /*
    public static void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle",
                    (Class<?>[]) null);
            try {
                Class.forName("com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException e) {
                Bukkit.broadcastMessage("CHANGE NAME METHOD DOES NOT WORK IN 1.7 OR LOWER!");
                return;
            }
            Object profile = getHandle.invoke(player).getClass()
                    .getMethod("getProfile")
                    .invoke(getHandle.invoke(player));
            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);

            for (Player players : Bukkit.getOnlinePlayers()) {
                players.hidePlayer(player);
                players.showPlayer(player);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        (CraftPlayer craftPlayer = ((CraftPlayer) player);
        GameProfile profile = new GameProfile(player.getUniqueId(), name);
        EntityLiving entityLiving = craftPlayer.getHandle();
        craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));
        try {
            Field gp2 = entityLiving.getClass().getSuperclass().getDeclaredField("bT");
            gp2.setAccessible(true);
            gp2.set(entityLiving, profile);
            gp2.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId() != player.getUniqueId()).forEach(p -> {
            try {
                sendPacket(p, new PacketPlayOutEntityDestroy(player.getEntityId()));
                sendPacket(p, new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
                Bukkit.getServer().getScheduler().runTask(Main.getInstance(), () -> p.hidePlayer(player));
                Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> p.showPlayer(player), 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }*/

    /**
     * Check if a user is premium
     *
     * @param username player name
     * @return boolean
     * @throws IOException
     */
    public static boolean isUsernamePremium(String username) throws IOException {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return !result.toString().equals("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get player ping
     *
     * @param player Player
     * @return Integer
     */
    public static int getPlayerPing(Player player) {
        try {
            int ping;
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + ReflectionUtil.getVersion() + ".entity.CraftPlayer");
            Object converted = craftPlayer.cast(player);
            Method handle = converted.getClass().getMethod("getHandle");
            Object entityPlayer = handle.invoke(converted);
            Field pingField = entityPlayer.getClass().getField("ping");
            ping = pingField.getInt(entityPlayer);
            return ping;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get a field from class
     *
     * @param clazz Class
     * @param name  String(Field)
     * @return Field
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


}
