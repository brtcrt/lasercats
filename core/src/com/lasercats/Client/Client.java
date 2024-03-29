package com.lasercats.Client;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Vector2;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;

import com.lasercats.GameObjects.PlayerNonMain;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private final String uri;
    private Socket socket;
    private Player player;
    private PlayerNonMain otherPlayer;
    private ArrayList<GameObject> gameObjects;
    public JSONObject dataFromServer;
    private String clientID;
    private Room room;
    public JSONArray rooms;

    public Client (ArrayList<GameObject> gameObjects) {
        this.uri = "https://lasercats-dev.fly.dev";
        this.room = new Room();
        this.connectSocket();
        this.configSocketEvents();
        this.gameObjects = gameObjects;
        // IF we ever change the indexes of the two player objects we are fucked btw... ~brtcrt
        this.player = (Player) gameObjects.get(0);
        this.otherPlayer = (PlayerNonMain) gameObjects.get(1);
        this.rooms = new JSONArray();
    }

    public void sendUpdate(JSONObject data) {
        try {
            data.put("roomId", room.getId());
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("updateFromPlayer", data);
    }

    public void createRoom(String roomName) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomName", roomName);
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("newRoomReq", data);
    }

    public void joinRoom(Room r) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomId", r.getId());
            data.put("roomName", r.getName());
            data.put("players", r.getPlayerIDs());
            data.put("currentRoom", this.room.getJSON());
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("joinRoomReq", data);
    }

    public Room getRoom() {
        return room;
    }

    public void updateRooms() {
        this.socket.emit("getRooms");
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
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = new JSONObject();
                try {
                    data.put("roomName", room.getName());
                } catch (JSONException e) {
                    System.out.println(e);
                }
                // socket.emit("newRoom", data);
                clientID = socket.id();
                Gdx.app.log("SocketIO", "Connected with ID: " + clientID);
            }
        });
        socket.on("createRoomRes", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    int code = data.getInt("code");
                    if (code == 201) {
                        room = new Room(data.getJSONObject("payload"));
                    } else {
                        Gdx.app.log("Server error", data.getString("message"));
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        });
        socket.on("joinRoomRes", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    int code = data.getInt("code");
                    if (code == 202) {
                        room = new Room(data.getJSONObject("payload"));
                    } else {
                        Gdx.app.log("Server error", data.getString("message"));
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        });
        socket.on("updateRooms", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    rooms = data.getJSONArray("rooms");
                } catch (JSONException e) {
                    System.out.println(e);
                }
                // Gdx.app.log("Rooms", rooms.toString());
            }
        });
        socket.on("updateFromServer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                dataFromServer = (JSONObject) args[0];
                try {
                    JSONArray data = dataFromServer.getJSONArray("gameObjects");
                    // If this client is the creator of the room
                    // we will do all object calculations on this client
                    // and send the data to the other so,
                    // we should only set the identifiers of the otherPlayer ~brtcrt
                    if (room.getPlayerIDs()[0].equals(clientID)) {
                        JSONObject identifier = (JSONObject) data.get(0);
                        otherPlayer.setIdentifiers(identifier);
                    } else {
                        JSONObject identifier = (JSONObject) data.get(0);
                        otherPlayer.setIdentifiers(identifier);
                        for (int i = 2; i < data.length(); i++) {
                            // This will cause a lot of problems later on... End me. ~brtcrt
                            identifier = (JSONObject) data.get(i);
                            GameObject g = gameObjects.get(i);
                            g.setIdentifiers(identifier);
                        }
                    }

                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        });
    }
}
