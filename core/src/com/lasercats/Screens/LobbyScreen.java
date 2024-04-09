package com.lasercats.Screens;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.Client.Client;
import com.lasercats.Client.Room;

public class LobbyScreen extends LaserCatsScreen {
    
    private ArrayList<TextButton> roomButtons;
    private VerticalGroup roomList;
    private ArrayList<Room> rooms;
    private long roomUpdateTime;

    private ImageButton goBackButton;

    private Label roomNameLabel;
    private TextField roomCreateField;
    private TextButton roomCreateButton;
    private TextField passwordField;
    private SelectBox gameModeDropBox;
    private TextButton startGameButton;

    private MainMenuScreen menuScreen;
    private Client client;

    private String[] gameModes;

    private Window passwordEnterWindow;
    private TextField passwordEnterField;

    public LobbyScreen(Game game, MainMenuScreen menuScreen) {
        super(game);
        this.menuScreen = menuScreen;
        this.client = menuScreen.getClient();
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
        rooms = new ArrayList<Room>();
        gameModes = new String[] {"Story Mode", "Time Attack Mode"};
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);

        createActors();
        positionActors();
        setListeners();
    }
    @Override
    public void pause() {}
    @Override
    public void positionActors() {
        passwordEnterWindow.add(passwordEnterField);
        passwordEnterWindow.setVisible(false);
        this.root.add(goBackButton).expandX().align(Align.topLeft).width(60).height(60);
        this.root.row();
        for (TextButton roomButton : roomButtons) {
            roomList.addActor(roomButton);
        }
        root.add(roomList).expand().colspan(3);
        root.row();
        root.add(passwordEnterWindow).colspan(3).padBottom(60);
        root.row();
        root.add(roomNameLabel).colspan(3).padBottom(30);
        root.row();
        root.add(roomCreateField).expandX().align(Align.right).padRight(Gdx.graphics.getWidth() / 60).padBottom(Gdx.graphics.getHeight() / 20);
        root.add(passwordField).expandX().align(Align.center).padRight(Gdx.graphics.getWidth() / 60).padBottom(Gdx.graphics.getHeight() / 20);
        root.add(roomCreateButton).expandX().align(Align.left).padLeft(Gdx.graphics.getWidth() / 60).padBottom(Gdx.graphics.getHeight() / 20);
        root.row();
        root.add(gameModeDropBox).expandX().align(Align.right).padBottom(30);
        root.add(startGameButton).expandX().colspan(2).align(Align.center).padBottom(30);
        stage.setRoot(root);
        //stage.setDebugAll(true);
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.ORANGE);
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        if (TimeUtils.nanoTime() - this.roomUpdateTime > TimeUtils.millisToNanos(1000)) {
			updateRoomList();
			updateCurrentRoom();
			this.roomUpdateTime = TimeUtils.nanoTime();
		}
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();
    }
    @Override
    public void resume() {}
    @Override
    public void setListeners() {
        // for (TextButton roomButton : roomButtons) {
        //     setRoomButtonListener(roomButton);
        // }
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (goBackButton.isPressed()) {
                    game.setScreen(menuScreen);
                }
            }
        });
        roomCreateButton.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               if (roomCreateButton.isPressed()) {
                String roomName = roomCreateField.getText();
                String password = passwordField.getText();
                if (password.isEmpty()) {
                    client.createRoom(roomName);
                } else {
                    client.createRoom(roomName, password);
                }
                Gdx.app.log("New Room Request", roomName);
                updateRoomList();
               }
           } 
        });
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (startGameButton.isPressed() && client.getRoom().getPlayerCount() == 2) {
                    if (gameModeDropBox.getSelected().equals("Story Mode")) {
                        //TODO change to story mode related screen
                        game.setScreen(null);
                    }
                    else if (gameModeDropBox.getSelected().equals("Time Attack Mode")) {
                        //TODO change to time attack mode related screen
                        game.setScreen(null);
                    }
                } 
            }
        });
    }
    @Override
    public void createActors() {
        roomButtons = new ArrayList<TextButton>();
        roomCreateField = new TextField("", skin);
        roomCreateField.setMessageText("Enter room name");
        roomNameLabel = new Label("Currently in no Room", skin);
        roomCreateButton = new TextButton("Create Room", skin);
        roomList = new VerticalGroup();
        goBackButton = new ImageButton(skin);
        gameModeDropBox = new SelectBox<String[]>(skin);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Create password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        startGameButton = new TextButton("Start", skin);
        gameModeDropBox.setItems(gameModes);
        passwordEnterWindow = new Window("Enter password", skin);
        passwordEnterField = new TextField("", skin);
        passwordEnterField.setPasswordMode(true);
        passwordEnterField.setPasswordCharacter('*');
        roomList.setWidth(Gdx.graphics.getWidth() / 2f);
        roomList.setHeight(Gdx.graphics.getHeight() / 2f);
    }
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
    private void getRooms() {
        this.client.updateRooms();
        this.rooms = new ArrayList<Room>();
        for (int i = 0; i < client.rooms.length(); i++) {
            try {
                JSONObject room = client.rooms.getJSONObject(i);
                String id = room.getString("roomId");
                String name = room.getString("roomName");
                JSONArray players = room.getJSONArray("players");
                String passwordHash = room.getString("passwordHash");
                this.rooms.add(new Room(id, name, players, passwordHash));
            } catch (JSONException e) {
                System.out.println(e);
            }
        }
    }

    private void updateRoomList() {
        getRooms();

        roomList.setWidth(Gdx.graphics.getWidth() / 2f);
        roomList.setHeight(Gdx.graphics.getHeight() / 2f);

        roomList.clear();

        roomButtons.clear();

        for (Room r : rooms) {
            TextButton roomButton = new TextButton(r.toString(), skin);
            setRoomButtonListener(roomButton, r);
            if (!(r.getPlayerCount() < 2)) {
                roomButton.setDisabled(true);
            }
            roomButtons.add(roomButton);
            roomList.addActor(roomButton);
        }
    }

    private void updateCurrentRoom() {
        Room currentRoom = this.client.getRoom();
        if (currentRoom.isEmpty()) {
            this.roomNameLabel.setText("Currently in No Room");
        } else {
            this.roomNameLabel.setText("Current in Room " + currentRoom.getName());
        }
    }
    private Room getRoomClicked() {
        for (int i = 0; i < roomButtons.size(); i++) {
            TextButton btn = roomButtons.get(i);
            if (btn.isPressed()) {
                return rooms.get(i);
            }
        }
        return new Room();
    }
    private void setRoomButtonListener(TextButton button, Room room) {
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (button.isChecked() && getRoomClicked().getPlayerCount() < 2 ) {
                    if (room.hasPassword()) {
                        passwordEnterWindow.setVisible(true);
                        if (passwordEnterField.getText().equals(room.getPasswordHash())) {
                            client.joinRoom(getRoomClicked(), room.getPasswordHash());
                        }
                    }
                    else {
                        client.joinRoom(getRoomClicked());
                    }   
                }
            }
        });
    }
}
