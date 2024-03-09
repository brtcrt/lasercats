package com.lasercats.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Room {
    private String id;
    private String name;
    private String[] playerIDs;
    private JSONObject json;

    public Room() {
        this.id = "";
        this.name = "";
        this.playerIDs = new String[2];
        this.json = new JSONObject();
    }

    public Room(JSONObject json) {
        try {
            this.json = json;
            this.id = json.getString("roomId");
            this.name = json.getString("roomName");
            this.playerIDs = new String[2];
            JSONArray players = json.getJSONArray("players");
            for (int i = 0; i < players.length(); i++) {
                this.playerIDs[i] = players.getString(i);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public Room(String id, String name, String[] playerIDs) {
        this.id = id;
        this.name = name;
        this.playerIDs = playerIDs;
        this.json = new JSONObject();
        try {
            this.json.put("roomId", id);
            this.json.put("roomName", name);
            this.json.put("players", playerIDs);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public Room(String id, String name, JSONArray playerIDs) {
        this.id = id;
        this.name = name;
        this.playerIDs = new String[2];
        try {
            for (int i = 0; i < playerIDs.length(); i++) {
                this.playerIDs[i] = playerIDs.getString(i);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getPlayerIDs() {
        return playerIDs;
    }


    public boolean isEmpty() {
        return this.id.isEmpty();
    }

    public int getPlayerCount() {
        int count = 0;
        for (String id : playerIDs) {
            if (id != null) count++;
        }
        return count;
    }

    public JSONObject getJSON() {
        return this.json;
    }

    @Override
    public String toString() {
        return this.name + " - " + getPlayerCount() + " Player(s)";
    }
}
