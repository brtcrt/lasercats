package com.lasercats.Screens;

import java.util.ArrayList;

import com.lasercats.Levels.LevelLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.Client.Client;
import com.lasercats.Client.Room;

public class LobbyScreen extends LaserCatsScreen {
    private ArrayList<TextButton> roomButtons;
    private VerticalGroup roomList;
    private Table roomTable;
    private Label roomListLabel;
    private ArrayList<Room> rooms;
    private long roomUpdateTime;

    private TextButton goBackButton;

    private Label newLobbyLabel;
    private Label roomNameLabel;
    private Label passwordLabel;
    private TextField roomCreateField;
    private TextButton roomCreateButton;
    private TextField passwordField;
    private Table roomCreationTable;
    private Pixmap tableBackgroundColor;

    private Label currentRoom;

    private TextButton startGameButton;
    private TextField passwordCreateField;

    private MainMenuScreen menuScreen;
    private Client client;

    private Dialog passwordEnterDialog;
    private TextButton passwordEnterButton;
    private TextField passwordEnterField;
    private TextButton passwordDialogCloseButton;

    private Texture background;

    private LobbyScreen lobby;

    private static final float FONT_SCALING = 1.75f;

    public LobbyScreen(Game game, MainMenuScreen menuScreen) {
        super(game);
        this.menuScreen = menuScreen;
        this.client = menuScreen.getClient();
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();

        rooms = new ArrayList<Room>();

        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.root.setFillParent(true);
        lobby = this;

        createTextures();
        createActors();
        positionActors();
        setListeners();
    }
    @Override
    public void pause() {}
    @Override
    public void positionActors() {
        //Once again, feel free to play around with alignment and sizes
        passwordEnterDialog.getTitleTable().add(passwordDialogCloseButton).height(20).width(45);
        passwordEnterDialog.add(passwordEnterField);
        passwordEnterDialog.add(passwordEnterButton);
        passwordEnterDialog.setVisible(false);

        roomTable.add(roomListLabel).expand().align(Align.top);
        roomTable.row();
        for (TextButton roomButton : roomButtons) {
            roomList.addActor(roomButton);
        }
        roomTable.add(roomList).expand().align(Align.top);

        roomCreationTable.add(newLobbyLabel).colspan(2);
        roomCreationTable.row();
        roomCreationTable.add(roomNameLabel).expand().align(Align.right).padRight(Gdx.graphics.getWidth() / 24);
        roomCreationTable.add(roomCreateField).expand().align(Align.left);
        roomCreationTable.row();
        roomCreationTable.add(passwordLabel).expand().align(Align.right).padRight(Gdx.graphics.getWidth() / 24);
        roomCreationTable.add(passwordCreateField).expand().align(Align.left);
        roomCreationTable.row();
        roomCreationTable.add(roomCreateButton).align(Align.center).colspan(2).padBottom(Gdx.graphics.getHeight() / 8);

        root.add(goBackButton).expandX().align(Align.topLeft).width(Gdx.graphics.getWidth() / 12).height(Gdx.graphics.getHeight() / 12).padBottom(Gdx.graphics.getHeight() / 16);
        root.row();
        root.add(roomTable).expand().fillY().width(Gdx.graphics.getWidth() / 3);
        root.add(roomCreationTable).expand().width(Gdx.graphics.getWidth() / 3).fillY();
        root.row();
        root.add(currentRoom).expand();
        root.add(startGameButton).expand().align(Align.center).height(Gdx.graphics.getHeight() / 10).width(Gdx.graphics.getWidth() / 9);

        root.setBackground(new TextureRegionDrawable(background));
        stage.setRoot(root);
    }
    @Override
    public void render(float delta) {
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
                        game.setScreen(new LevelLoader(game, client, lobby, 4));
                }
            } 
        });
        passwordDialogCloseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent arg0, Actor arg1) {
                if (passwordDialogCloseButton.isPressed()) {
                    passwordEnterDialog.setVisible(false);
                    passwordEnterDialog.hide();
                }    
            }
        });
    }
    @Override
    public void createActors() {
       
        roomButtons = new ArrayList<TextButton>();
        roomList = new VerticalGroup();
        roomList.setWidth(Gdx.graphics.getWidth() / 2f);
        roomList.setHeight(Gdx.graphics.getHeight() / 2f);
        roomTable = new Table(skin);
        roomTable.setBackground(new TextureRegionDrawable(new Texture(tableBackgroundColor)));
        roomListLabel = new Label("Lobbies", skin);
        roomListLabel.setFontScale(FONT_SCALING);

        goBackButton = new TextButton("Back", skin, "dark");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Create password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        startGameButton = new TextButton("Start", skin);
        startGameButton.getLabel().setFontScale(FONT_SCALING);
        passwordCreateField = new TextField("", skin);
        passwordCreateField.setPasswordMode(true);
        passwordCreateField.setPasswordCharacter('*');

        passwordEnterDialog = new Dialog("Enter password", skin, "c2");
        passwordEnterField = new TextField("", skin);
        passwordEnterField.setPasswordMode(true);
        passwordEnterField.setPasswordCharacter('*');
        passwordEnterButton = new TextButton("Confirm", skin);
        passwordDialogCloseButton = new TextButton("Close", skin, "dark");

        roomCreationTable = new Table(skin);
        roomCreationTable.setBackground(new TextureRegionDrawable(new Texture(tableBackgroundColor)));
        roomCreateField = new TextField("", skin);
        roomCreateField.setMessageText("Enter room name");
        roomNameLabel = new Label("Name", skin);
        roomNameLabel.setFontScale(FONT_SCALING);
        roomCreateButton = new TextButton("Create Room", skin);
        roomCreateButton.getLabel().setFontScale(FONT_SCALING);
        passwordLabel = new Label("Password", skin);
        passwordLabel.setFontScale(FONT_SCALING);
        newLobbyLabel = new Label("New Lobby", skin);
        newLobbyLabel.setFontScale(FONT_SCALING);

        currentRoom = new Label("Currently in no room", skin);
        currentRoom.setFontScale(FONT_SCALING);
    }
    @Override
    public void createTextures() {
        background = new Texture(Gdx.files.internal("TitleScreenBackground.jpg"));
        tableBackgroundColor = new Pixmap(1, 1, Pixmap.Format.RGB565);
        tableBackgroundColor.setColor(Color.SLATE);
        tableBackgroundColor.fill();
    }
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        background.dispose();
        tableBackgroundColor.dispose();
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
            if (r.getName().equals(client.getRoom().getName())) {
                client.updateSelfRoom(r);
            }
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
            this.currentRoom.setText("Currently in no Room");
        } else {
            this.currentRoom.setText("Currently in Room " + currentRoom.getName());
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
                if (button.isPressed() && getRoomClicked().getPlayerCount() < 2 && !getRoomClicked().equals(client.getRoom()) && !passwordEnterDialog.isVisible()) {
                    if (room.hasPassword()) {
                        passwordEnterDialog.setVisible(true);
                        passwordEnterDialog.show(stage);
                        setPasswordEnterButtonListener(room);
                    }
                    else {
                        client.joinRoom(room);
                    }   
                }
            }
        });
    }
    private void setPasswordEnterButtonListener(Room room) {
        passwordEnterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (passwordEnterButton.isPressed()) {
                    if (Client.hashPassword(passwordEnterField.getText()).equals(room.getPasswordHash())) {
                        client.joinRoom(room, passwordEnterField.getText());
                        passwordEnterDialog.setVisible(false);
                        passwordEnterDialog.hide();
                    }   
                }
            }
        });
    }
}