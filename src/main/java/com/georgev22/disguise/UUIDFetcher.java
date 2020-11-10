package com.georgev22.disguise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDFetcher {

    private final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    private Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private Map<UUID, String> nameCache = new HashMap<UUID, String>();

    private String name;
    private UUID id;

    public UUID getUUID(String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    public UUID getUUIDAt(String name, long timestamp) {
        name = name.toLowerCase();

        if (uuidCache.containsKey(name))
            return uuidCache.get(name);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
            connection.setReadTimeout(5000);

            UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            uuidCache.put(name, data.id);
            nameCache.put(data.id, data.name);

            return data.id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName(UUID uuid) {
        if (nameCache.containsKey(uuid))
            return nameCache.get(uuid);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);

            UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
            UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];
            uuidCache.put(currentNameData.name.toLowerCase(), uuid);
            nameCache.put(uuid, currentNameData.name);

            return currentNameData.name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}