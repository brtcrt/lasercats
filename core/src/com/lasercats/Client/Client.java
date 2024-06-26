package com.lasercats.Client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lasercats.GameObjects.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class Client {
    private final String uri;
    private Socket socket;
    private Player otherPlayer;
    public  Viewport viewport;
    public ArrayList<GameObject> gameObjects;
    public ArrayList<PhysicsObject> physicsObjects;
    public JSONObject dataFromServer;
    private String clientID = java.util.UUID.randomUUID().toString();
    private Room room;
    public JSONArray rooms;
    public boolean inGame = false;

    public Client (ArrayList<GameObject> gameObjects, ArrayList<PhysicsObject> physicsObjects, Viewport v) {
        this.uri = "https://lasercats.fly.dev";
        // this.uri = "http://localhost:8080";
        this.room = new Room();
        this.connectSocket();
        this.configSocketEvents();
        this.gameObjects = gameObjects;
        this.physicsObjects = physicsObjects;
        this.viewport = v;
        this.otherPlayer = (Player) gameObjects.get(1);
        this.rooms = new JSONArray();
    }

    public void sendUpdate(JSONObject data) {
        try {
            data.put("clientID", clientID);
            data.put("roomId", room.getId());
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("updateFromPlayer", data);
    }

    public void createRoom(String roomName) {
        JSONObject data = new JSONObject();
        try {
            data.put("clientID", clientID);
            if (!this.room.isEmpty()) {
                data.put("currentRoom", this.room.getJSON());
            }
            data.put("roomName", roomName);
            data.put("passwordHash", "");
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("newRoomReq", data);
    }

    public void createRoom(String roomName, String password) {
        JSONObject data = new JSONObject();
        try {
            data.put("clientID", clientID);
            if (!this.room.isEmpty()) {
                data.put("currentRoom", this.room.getJSON());
            }
            data.put("roomName", roomName);
            data.put("passwordHash", hashPassword(password));
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("newRoomReq", data);
    }

    public static String hashPassword(String p) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(p.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void updateSelfRoom(Room r) {
        this.room = r;
    }

    public void joinRoom(Room r) {
        JSONObject data = new JSONObject();
        try {
            data.put("clientID", clientID);
            data.put("roomId", r.getId());
            data.put("roomName", r.getName());
            data.put("players", r.getPlayerIDs());
            data.put("currentRoom", this.room.getJSON());
            data.put("passwordHash", "");
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("joinRoomReq", data);
    }

    public void joinRoom(Room r, String password) {
        JSONObject data = new JSONObject();
        try {
            data.put("clientID", clientID);
            data.put("roomId", r.getId());
            data.put("roomName", r.getName());
            data.put("players", r.getPlayerIDs());
            data.put("currentRoom", this.room.getJSON());
            data.put("passwordHash", hashPassword(password));
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.emit("joinRoomReq", data);
    }

    public Room getRoom() {
        return room;
    }
    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }
    public void updateRooms() {
        this.socket.emit("getRooms");
    }

    public void close() {
        try {
            JSONObject data = new JSONObject();
            data.put("clientID", clientID);
            socket.emit("closeClient", data);
        } catch (JSONException e) {
            System.out.println(e);
        }
        this.socket.close();
    }
    private void connectSocket() {
        try {
            this.socket = IO.socket(uri);
            socket.connect();
            try {
                JSONObject data = new JSONObject();
                data.put("clientID", clientID);
                socket.emit("newPlayer", data);
            } catch (JSONException e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getID(){
        return clientID;
    }

    private void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = new JSONObject();
                try {
                    data.put("clientID", clientID);
                } catch (JSONException e) {
                    System.out.println(e);
                }
                socket.emit("newPlayer", data);
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
            }
        });
        socket.on("updateFromServer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (!inGame) return;
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
                        gameObjects.get(2).setIdentifiers((JSONObject) data.get(2));
                        int boxIndex = 3;
                        // This really should not work ~brtcrt
                        for (int i = 3; i < gameObjects.size(); i++) {
                            GameObject o = gameObjects.get(i);
                            if (o instanceof Box) {
                                o.setIdentifiers(data.getJSONObject(boxIndex));
                                boxIndex++;
                            }
                        }
                    }

                } catch (JSONException e) {
                    System.out.println(e);
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Disconnected " + clientID);
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connect Error");
            }
        });
    }
}