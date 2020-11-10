package com.georgev22.disguise;

import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class Cooldown {
    private static final Map<String, Cooldown> cooldowns = Maps.newHashMap();
    private final int timeInSeconds;
    private final UUID id;
    private final String cooldownName;
    private long start;

    public Cooldown(UUID id, String cooldownName, int timeInSeconds) {
        this.id = id;
        this.cooldownName = cooldownName;
        this.timeInSeconds = timeInSeconds;
    }

    public static boolean isInCooldown(UUID id, String cooldownName) {
        if (Cooldown.getTimeLeft(id, cooldownName) >= 1) {
            return false;
        }
        Cooldown.stop(id, cooldownName);
        return true;
    }

    private static void stop(UUID id, String cooldownName) {
        cooldowns.remove(id + cooldownName);
    }

    private static Cooldown getCooldown(UUID id, String cooldownName) {
        return cooldowns.get(id.toString() + cooldownName);
    }

    public static int getTimeLeft(UUID id, String cooldownName) {
        Cooldown cooldown = Cooldown.getCooldown(id, cooldownName);
        int f = -1;
        if (cooldown != null) {
            long now = System.currentTimeMillis();
            long cooldownTime = cooldown.start;
            int r = (int) (now - cooldownTime) / 1000;
            f = (r - cooldown.timeInSeconds) * -1;
        }
        return f;
    }

    public static String getTimeLeft(int secondTime) {
        TimeZone tz = TimeZone.getTimeZone("EEST");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date(secondTime * 1000));
    }

    public void start() {
        start = System.currentTimeMillis();
        cooldowns.put(id.toString() + cooldownName, this);
    }
}
