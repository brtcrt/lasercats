package com.lasercats.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class MainMenu {
    private Stage stage;
    private Table table;

    private TextButton[] gameModeButtons;
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

    private TextField gameTitle;
    private TextFieldStyle gameTitleStyle;

    private Pixmap red;

    public MainMenu() {
        stage = new Stage();
        table = new Table();
        table.setWidth(1024);
        table.setHeight(720);
        table.setOrigin(0,0);
        Gdx.input.setInputProcessor(stage);

        red = new Pixmap(64, 64, Format.RGB888);
        red.setColor(Color.RED);

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
        table.row();
        table.add(gameTitle).align(Align.center).expandX().padLeft(76);
        table.row();
        for (int i = 0; i < gameModeButtons.length; i++) {
            table.add(gameModeButtons[i]).align(Align.center);
            table.row();
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
    public void disposeMenu() {
        stage.dispose();
        red.dispose();
    }
    public Stage getStage() {
        return stage;
    }
}
