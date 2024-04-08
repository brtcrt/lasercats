package com.lasercats.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class OptionsScreen extends LaserCatsScreen {

    private Table optionsTable;
    private TextButton audioButton;
    private TextButton controlsButton;
    private TextButton customizeCatButton;

    private Window audioWindow;
    private Window controlsWindow;
    private Window customizeCatWindow;

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

    private ImageButton goBackButton;

    public OptionsScreen(Game game) {
        super(game);
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
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
        ScreenUtils.clear(Color.RED);
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();
    }
    @Override
    public void setListeners() {
        audioButton.addListener(new OptionsButtonListener(audioWindow));
        controlsButton.addListener(new OptionsButtonListener(controlsWindow));
        customizeCatWindow.addListener(new OptionsButtonListener(customizeCatWindow));


    }
    @Override
    public void createActors() {
        optionsTable = new Table();
        audioButton = new TextButton("Audio", skin);
        controlsButton = new TextButton("Controls", skin);
        customizeCatButton = new TextButton("Customize Cat", skin);

        audioWindow = new Window("", skin);
        controlsWindow = new Window("", skin);
        customizeCatWindow = new Window("", skin);

        sfxSlider = new Slider(0, 100, 1, false, skin);
        musicSlider = new Slider(0, 100, 1, false, skin);
        sfxLabel = new Label("Sound Effects", skin);
        musicLabel = new Label("Music", skin);
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

        optionsTable.setSize((float) (root.getWidth()), (float) (root.getHeight()));
        optionsTable.add(audioButton).expand().fill();
        optionsTable.row();
        optionsTable.add(controlsButton).expand().fill();
        optionsTable.row();
        optionsTable.add(customizeCatButton).expand().fill();

        audioWindow.add(sfxLabel).expandX();
        audioWindow.add(sfxSlider).expandX();
        audioWindow.add(sfxValue).expandX();
        audioWindow.row();
        audioWindow.add(musicLabel).expandX();
        audioWindow.add(musicSlider).expandX();
        audioWindow.add(musicValue).expandX();

        controlsWindow.add(moveUpLabel).expandX();
        controlsWindow.add(moveUpKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(moveDownLabel).expandX();
        controlsWindow.add(moveDownKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(moveRightLabel).expandX();
        controlsWindow.add(moveRightKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(moveLeftLabel).expandX();
        controlsWindow.add(moveLeftKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(shootLaserLabel).expandX();
        controlsWindow.add(shootLaserKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(interactLabel).expandX();
        controlsWindow.add(interactKeybind).expandX();
        controlsWindow.row();
        controlsWindow.add(meowLabel).expandX();
        controlsWindow.add(meowKeybind).expandX();

        customizeCatWindow.add(furColorLabel).expandX();
        customizeCatWindow.add(furColorDropdown).expandX();

        //No idea if this is right as well.
        audioWindow.setVisible(false);
        controlsWindow.setVisible(false);
        customizeCatWindow.setVisible(false);
        
        root.add(goBackButton).align(Align.topLeft).width(60).padBottom(120);
        root.row();
        root.add(optionsTable);
        stage.setRoot(root);
        stage.setDebugAll(true);
    }
    @Override
    public void hide() {
        
    }
    @Override
    public void dispose() {
        
    }
    @Override
    public void resume() {
        
    }
    public class OptionsButtonListener extends ChangeListener  {
        private Window window;
        public OptionsButtonListener(Window window) {
            this.window = window;
        }
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            resetWindows();
            if (((Button) actor).isPressed()) {
                window.setVisible(true);
                optionsTable.add(window);
            }
        }
        private void resetWindows() {
            //Just in case.
            audioWindow.setVisible(false);
            controlsWindow.setVisible(false);
            customizeCatWindow.setVisible(false);

            optionsTable.removeActor(audioWindow);
            optionsTable.removeActor(controlsWindow);
            optionsTable.removeActor(customizeCatWindow);
        }
    }
    public class KeybindListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            
        }
    }
}
