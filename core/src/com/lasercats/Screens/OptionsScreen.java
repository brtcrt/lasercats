package com.lasercats.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class OptionsScreen extends LaserCatsScreen {

    private TextButton audioButton;
    private TextButton controlsButton;
    private TextButton customizeCatButton;

    private Table displayTable;
    private Table buttonTable;

    private Slider sfxSlider;
    private Slider musicSlider;
    private Label sfxLabel;
    private Label musicLabel;
    private Label sfxValue;
    private Label musicValue;

    private Label moveUpLabel;
    private Label moveDownLabel;
    private Label moveRightLabel;
    private Label moveLeftLabel;
    private Label shootLaserLabel;
    private Label interactLabel;
    private Label meowLabel;

    //No idea if this is the right way to do this.
    private TextButton moveUpKeybind;
    private TextButton moveDownKeybind;
    private TextButton moveRightKeybind;
    private TextButton moveLeftKeybind;
    private TextButton shootLaserKeybind;
    private TextButton interactKeybind;
    private TextButton meowKeybind;

    private Label furColorLabel;
    private SelectBox furColorDropdown;
    private String[] furColors;
    private MainMenuScreen menu;

    private ImageButton goBackButton;

    private int[] keybinds;

    public OptionsScreen(Game game, MainMenuScreen menu) {
        super(game);
        this.menu = menu;
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();

        this.keybinds = new int[7];
        keybinds[0] = Input.Keys.W;
        keybinds[1] = Input.Keys.S;
        keybinds[2] = Input.Keys.D;
        keybinds[3] = Input.Keys.A;
        keybinds[4] = Input.Keys.SPACE;
        keybinds[5] = Input.Keys.E;
        keybinds[6] = Input.Keys.M;
        
        this.furColors = new String[] {"White", "Red", "Green", "Blue"};
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        createActors();
        setListeners();
        positionActors();
    }

    @Override
    public void pause() {
        
    }
    @Override
    public void show() {
        
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.ORANGE);
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();
    }
    @Override
    public void setListeners() {
        audioButton.addListener(new OptionsButtonListener());
        controlsButton.addListener(new OptionsButtonListener());
        customizeCatButton.addListener(new OptionsButtonListener());
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (goBackButton.isPressed()) {
                    game.setScreen(menu);
                }
            }
        });

    }
    @Override
    public void createActors() {
        audioButton = new TextButton("Audio", skin);
        audioButton.setName("audio");
        controlsButton = new TextButton("Controls", skin);
        controlsButton.setName("controls");
        customizeCatButton = new TextButton("Customize Cat", skin);
        customizeCatButton.setName("customize");
        displayTable = new Table();
        buttonTable = new Table();

        sfxSlider = new Slider(0, 100, 1, false, skin);
        musicSlider = new Slider(0, 100, 1, false, skin);
        sfxLabel = new Label("Sound Effects", skin);
        musicLabel = new Label("Music", skin);
        musicLabel.setColor(Color.WHITE);
        sfxValue = new Label("", skin);
        musicValue = new Label("", skin);

        moveUpLabel = new Label("Move Up", skin);
        moveDownLabel = new Label("Move Down", skin);
        moveRightLabel = new Label("Move Right", skin);
        moveLeftLabel = new Label("Move Left", skin);
        shootLaserLabel = new Label("Shoot Laser", skin);
        interactLabel = new Label("Interact", skin);
        meowLabel = new Label("Meow", skin);

        moveUpKeybind = new TextButton("W", skin);
        moveDownKeybind = new TextButton("S", skin);
        moveRightKeybind = new TextButton("D", skin);
        moveLeftKeybind = new TextButton("A", skin);
        shootLaserKeybind = new TextButton("Space", skin);
        interactKeybind = new TextButton("E", skin);
        meowKeybind = new TextButton("M", skin);

        furColorLabel = new Label("Fur Color", skin);
        furColorDropdown = new SelectBox<String>(skin);
        furColorDropdown.setItems(furColors);

        goBackButton = new ImageButton(skin);
    }
    @Override
    public void positionActors() {

        buttonTable.add(goBackButton).align(Align.topLeft).width(60).padBottom(120);
        buttonTable.row();
        buttonTable.add(audioButton).expand().fillY().width(200).align(Align.left);
        buttonTable.row();
        buttonTable.add(controlsButton).expand().fillY().width(200).align(Align.left);
        buttonTable.row();
        buttonTable.add(customizeCatButton).expand().fillY().width(200).align(Align.left);
        root.add(buttonTable).expand().fill();
        root.add(displayTable).expand().fill();
        
        stage.setRoot(root);
        //stage.setDebugAll(true);
    }
    private void positionAudioOptions() {
        displayTable.clear();
        displayTable.add(sfxLabel).expandX();
        displayTable.add(sfxSlider).expandX();
        displayTable.add(sfxValue).expandX();
        displayTable.row();
        displayTable.add(musicLabel).expandX();
        displayTable.add(musicSlider).expandX();
        displayTable.add(musicValue).expandX();
    }
    private void positionControlsOptions() {
        displayTable.clear();
        displayTable.add(moveUpLabel).expandX();
        displayTable.add(moveUpKeybind).expandX();
        displayTable.row();
        displayTable.add(moveDownLabel).expandX();
        displayTable.add(moveDownKeybind).expandX();
        displayTable.row();
        displayTable.add(moveRightLabel).expandX();
        displayTable.add(moveRightKeybind).expandX();
        displayTable.row();
        displayTable.add(moveLeftLabel).expandX();
        displayTable.add(moveLeftKeybind).expandX();
        displayTable.row();
        displayTable.add(shootLaserLabel).expandX();
        displayTable.add(shootLaserKeybind).expandX();
        displayTable.row();
        displayTable.add(interactLabel).expandX();
        displayTable.add(interactKeybind).expandX();
        displayTable.row();
        displayTable.add(meowLabel).expandX();
        displayTable.add(meowKeybind).expandX();
    }
    private void positionCustomizeCatOptions() {
        displayTable.clear();
        displayTable.add(furColorLabel).expandX();
        displayTable.add(furColorDropdown).expandX();
    }
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        
    }
    @Override
    public void resume() {}
    public class OptionsButtonListener extends ChangeListener  {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((Button) actor).isPressed()) {
                Cell<Table> cell = root.getCell(displayTable);
                if (actor.getName().equals("audio")) {
                    positionAudioOptions();
                }
                else if (actor.getName().equals("controls")) {
                    positionControlsOptions();
                }
                else if (actor.getName().equals("customize")) {
                    positionCustomizeCatOptions();
                }
                cell.setActor(displayTable);
            }
        }
    }
    public class KeybindListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((Button) actor).isPressed()) {
                if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                    return;
                }
                else {

                }
            }
        }
    }
}
