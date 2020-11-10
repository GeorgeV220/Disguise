package com.georgev22.disguise.manager;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

/**
 * @author GeorgeV22
 */
public class SkinManager {

    private static SkinManager skinManager;

    public static SkinManager getSkinManager() {
        return skinManager == null ? skinManager = new SkinManager() : skinManager;
    }

    private final Map<UUID, SkinUser> map = Maps.newHashMap();

    public Map<UUID, SkinUser> getMap() {
        return map;
    }
}
