package com.lasercats.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.lasercats.Client.Client;
import com.lasercats.Client.Room;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainMenu {
    private Stage stage;
    private Table table;

    private TextButton[] gameModeButtons;
    private ArrayList<TextButton> roomButtons;
    private Skin gameModeButtonsConfig;
    private TextButtonStyle gameModeButtonStyle;

    private ImageButton optionsButton;
    private Skin optionsButtonConfig;
    private ImageButtonStyle optionsButtonStyle;
    private Texture optionsIcon;

    private ImageButton exitButton;
    private Skin exitButtonConfig;
    private ImageButtonStyle exitButtonStyle;
    private Texture exitButtonIcon;

    private Skin skin;
    private TextField roomCreateField;
    private TextField roomPasswordField;
    private TextButton roomCreateButton;

    private TextField gameTitle;
    private TextFieldStyle gameTitleStyle;

    private Pixmap red;

    private Client client;

    private ArrayList<Room> rooms;

    private VerticalGroup roomList;
    private Label roomNameLabel;

    public MainMenu(Client client) {
        stage = new Stage();
        table = new Table();
        table.setWidth(1024);
        table.setHeight(720);
        table.setOrigin(0,0);
        Gdx.input.setInputProcessor(stage);

        red = new Pixmap(64, 64, Format.RGB888);
        red.setColor(Color.RED);

        this.client = client;
        rooms = new ArrayList<Room>();

        roomList = new VerticalGroup();

        roomButtons = new ArrayList<TextButton>();


        skin = new Skin(Gdx.files.internal("flatearthui/flat-earth-ui.json"));
        roomCreateField = new TextField("", skin);

        roomPasswordField = new TextField("", skin);
        roomPasswordField.setPasswordMode(true);
        roomPasswordField.setPasswordCharacter('*');
        roomPasswordField.setMessageText("Enter password or leave empty");

        roomNameLabel = new Label("Currently in no Room", skin);

        gameModeButtonsConfig = new Skin();
        gameModeButtonStyle = new TextButtonStyle();
        gameModeButtonsConfig.add("default", new BitmapFont());
        setSkinColor(gameModeButtonsConfig, red);
        gameModeButtonStyle.font = gameModeButtonsConfig.getFont("default");
		gameModeButtonsConfig.add("default", gameModeButtonStyle);
        gameModeButtons = new TextButton[3];
        gameModeButtons[0] = new TextButton("Main Story", gameModeButtonStyle);
        gameModeButtons[1] = new TextButton("Time Attack", gameModeButtonStyle);
        gameModeButtons[2] = new TextButton("Level Editor", gameModeButtonStyle);
        for (int i = 0; i < gameModeButtons.length; i++) {
            setListeners(gameModeButtons[i]);
        }


        roomCreateButton = new TextButton("Create Room", gameModeButtonStyle);
        setListeners(roomCreateButton);

        optionsButtonConfig = new Skin();
        optionsButtonStyle = new ImageButtonStyle();
        optionsIcon = new Texture(Gdx.files.internal("OptionsIcon.png"));
        setSkinColor(optionsButtonConfig, red);
        setButtonImage(optionsIcon, optionsButtonStyle, optionsButtonConfig);
        optionsButtonConfig.add("default", optionsButtonStyle);
        optionsButton = new ImageButton(optionsButtonConfig);
        setListeners(optionsButton);

        exitButtonConfig = new Skin();
        exitButtonStyle = new ImageButtonStyle();
        exitButtonIcon = new Texture(Gdx.files.internal("ExitButtonIcon.png"));
        setSkinColor(optionsButtonConfig, red);
        setButtonImage(exitButtonIcon, exitButtonStyle, exitButtonConfig);
        exitButtonConfig.add("default", exitButtonStyle);
        exitButton = new ImageButton(exitButtonConfig);
        setListeners(exitButton);
        exitButton.setHeight(100);
        exitButton.setWidth(100);

        gameTitleStyle = new TextFieldStyle();
        gameTitleStyle.font = new BitmapFont();
        gameTitleStyle.fontColor = Color.RED;
        gameTitle = new TextField("Laser Cats", gameTitleStyle);

        stage.addActor(table);
        table.add(exitButton).align(Align.topRight).expand();

        // list of rooms hopefully
        table.row();
        table.add(roomNameLabel);
        table.row();
        table.add(roomList).align(Align.center);
        table.row();
        table.add(roomCreateField);
        table.add(roomCreateButton).align(Align.center);
        table.row();
        table.add(roomPasswordField);


        table.row();
        table.add(gameTitle).align(Align.center).expandX().padLeft(76);
        table.row();
        for (int i = 0; i < gameModeButtons.length; i++) {
            table.add(gameModeButtons[i]).align(Align.center);
            table.row();
        }
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

    public void updateRoomList() {
        getRooms();

        roomList.setWidth(Gdx.graphics.getWidth() / 2f);
        roomList.setHeight(Gdx.graphics.getHeight() / 2f);

        roomList.clear();

        roomButtons.clear();

        for (Room r : rooms) {
            TextButton roomButton = new TextButton(r.toString(), skin);
            setListeners(roomButton);
            if (!(r.getPlayerCount() < 2)) {
                roomButton.setDisabled(true);
            }
            roomButtons.add(roomButton);
            roomList.addActor(roomButton);
        }
    }

    public void updateCurrentRoom() {
        Room currentRoom = this.client.getRoom();
        if (currentRoom.isEmpty()) {
            this.roomNameLabel.setText("Currently in No Room");
        } else {
            this.roomNameLabel.setText("Current in Room " + currentRoom.getName());
        }
    }
    private void setSkinColor (Skin skin, Pixmap pix) {
        skin.add("red", new Texture(pix));
    }
    private void setButtonImage(Texture texture, ImageButtonStyle buttonStyle, Skin buttonSkin) {
        buttonStyle.imageUp = buttonSkin.newDrawable(new TextureRegionDrawable(texture));
        buttonStyle.imageDown = buttonSkin.newDrawable(new TextureRegionDrawable(texture));
        buttonStyle.imageChecked = buttonSkin.newDrawable(new TextureRegionDrawable(texture));
        buttonStyle.imageOver = buttonSkin.newDrawable(new TextureRegionDrawable(texture));
    }
    private void setListeners(Button button) {
        button.addListener(new ClickListener(Input.Buttons.LEFT));
    }
    public TextButton getGameModeButton() {
        return this.gameModeButtons[0];
    }

    public TextButton getRoomCreateButton() { return this.roomCreateButton; }
    public TextField getRoomCreateField() { return this.roomCreateField; }
    public TextField getRoomPasswordField() { return this.roomPasswordField; }

    public Room getRoomClicked() {
        for (int i = 0; i < roomButtons.size(); i++) {
            TextButton btn = roomButtons.get(i);
            if (btn.isPressed()) {
                return rooms.get(i);
            }
        }
        return new Room();
    }
    public void disposeMenu() {
        stage.dispose();
        red.dispose();
    }
    public Stage getStage() {
        return stage;
    }

}
