package com.lasercats.Client;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Vector2;
import com.lasercats.GameObjects.Player;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Client {
    private final String uri;
    private Socket socket;
    private Player otherPlayer;
    public JSONObject otherPlayerData;
    private final String roomName;

    public Client (Player otherPlayer) {
        this.uri = "http://localhost:8080/";
        this.roomName = "cat";
        this.connectSocket();
        this.configSocketEvents();
        this.otherPlayer = otherPlayer;
    }

    public void sendUpdate(JSONObject data) {
        try {
            data.put("roomName", this.roomName);
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("updateFromPlayer", data);
    }

    public void close() {
        this.socket.close();
    }
    private void connectSocket() {
        try {
            this.socket = IO.socket(uri);
            socket.connect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void configSocketEvents() {
        this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject data = new JSONObject();
                try {
                    data.put("roomName", roomName);
                } catch (JSONException e) {
                    System.out.println(e);
                }
                socket.emit("newRoom", data);
                Gdx.app.log("SocketIO", "Connected");
            }
        }).on("socketID", new Emitter.Listener() {
            // currently not used
            @Override
            public void call(Object... objects) {
                JSONObject data = (JSONObject) objects[0];
                String id = "";
                try {
                    id = data.getString("id");
                } catch (JSONException e) {
                    System.out.println(e);
                }
                Gdx.app.log("SocketID", id);
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject data = (JSONObject) objects[0];
                String id = "";
                try {
                    id = data.getString("id");
                } catch (JSONException e) {
                    System.out.println(e);
                }
                Gdx.app.log("SocketID", "New player id: " + id);
            }
        }).on("updateFromServer", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                otherPlayerData = (JSONObject) objects[0];
                try {
                    // TODO change this later
                    JSONObject position = otherPlayerData.getJSONObject("position");
                    JSONObject velocity = otherPlayerData.getJSONObject("velocity");
                    JSONObject direction = otherPlayerData.getJSONObject("direction");
                    otherPlayer.x = (float) position.getDouble("x");
                    otherPlayer.y = (float) position.getDouble("y");
                    otherPlayer.velocity = new Vector2((float) velocity.getDouble("x"), (float) velocity.getDouble("y"));
                    otherPlayer.direction = new Vector2((float) direction.getDouble("x"), (float) direction.getDouble("y"));
                } catch (JSONException e) {
                    System.out.println(e);
                }

            }
        });
    }
}
