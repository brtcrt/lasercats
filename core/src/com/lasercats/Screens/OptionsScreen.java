package com.lasercats.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.GameObjects.Player;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class OptionsScreen extends LaserCatsScreen {

    private TextButton audioButton;
    private TextButton controlsButton;
    private TextButton customizeCatButton;
    private TextButton goBackButton;

    private Table displayTable;
    private Table buttonTable;

    private Slider sfxSlider;
    private Slider musicSlider;
    private Label sfxLabel;
    private Label musicLabel;

    private Label moveUpLabel;
    private Label moveDownLabel;
    private Label moveRightLabel;
    private Label moveLeftLabel;
    private Label shootLaserLabel;
    private Label interactLabel;
    private Label meowLabel;

    private TextButton moveUpKeybind;
    private TextButton moveDownKeybind;
    private TextButton moveRightKeybind;
    private TextButton moveLeftKeybind;
    private TextButton shootLaserKeybind;
    private TextButton interactKeybind;
    private TextButton meowKeybind;

    private Label furColorLabel;
    private SelectBox<String> furColorDropdown;
    private String[] furColors;
    private static String selectedColor;
    private FileHandle colorBin;

    private MainMenuScreen menu;

    private static int[] keybinds;
    private FileHandle keybindsBin;

    private KeybindProcessor processor;
    private InputMultiplexer multiplexer;

    private static float sfxVolume;
    private static float musicVolume;
    private FileHandle audioBin;

    private Texture background;

    private static final float FONT_SCALING = 2;
    private static final float LABEL_FONT_SCALING = 3;
    private static final float SELECTBOX_SCALING = 1.2f;

    public OptionsScreen(Game game, MainMenuScreen menu) {
        super(game);
        this.menu = menu;
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();

        processor = new KeybindProcessor();
        multiplexer = new InputMultiplexer();

        this.furColors = new String[] {"White", "Red", "Green", "Blue"};
        this.stage = new Stage(genericViewport, batch);
        multiplexer.addProcessor(stage);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.root.setFillParent(true);
        createTextures();
        loadVolumeLevels();
        createActors();
        setListeners();
        positionActors();
        loadKeybinds();
        loadColor();
    }
    @Override
    public void pause() {}
    @Override
    public void show() {}
    @Override
    public void render(float delta) {
        Gdx.input.setInputProcessor(multiplexer);
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
        moveUpKeybind.addListener(new KeybindListener(0, moveUpKeybind));
        moveDownKeybind.addListener(new KeybindListener(1, moveDownKeybind));
        moveRightKeybind.addListener(new KeybindListener(2, moveRightKeybind));
        moveLeftKeybind.addListener(new KeybindListener(3, moveLeftKeybind));
        shootLaserKeybind.addListener(new KeybindListener(4, shootLaserKeybind));
        interactKeybind.addListener(new KeybindListener(5, interactKeybind));
        meowKeybind.addListener(new KeybindListener(6, meowKeybind));

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfxVolume = sfxSlider.getValue();
                byte[] volumeBytes = new byte[2];
                volumeBytes[0] = (byte) sfxVolume;
                volumeBytes[1] = (byte) musicVolume;
                audioBin.writeBytes(volumeBytes, false);
            }
        });
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolume = musicSlider.getValue();
                byte[] volumeBytes = new byte[2];
                volumeBytes[0] = ((byte)sfxVolume);
                volumeBytes[1] = ((byte)musicVolume);
                audioBin.writeBytes(volumeBytes, false);
            }
        });
        furColorDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedColor = (String) furColorDropdown.getSelected();
                colorBin.writeString(selectedColor, false);        
            }
        });
    }
    @Override
    public void createActors() {
        //The downside of scaling the font size is that the writing gets blurry, especially in higher scaling.
        //But otherwise the font size is way too small
        //This is the best comprimise I could find
        audioButton = new TextButton("Audio", skin);
        audioButton.setName("audio");
        audioButton.getLabel().setFontScale(FONT_SCALING);
        controlsButton = new TextButton("Controls", skin);
        controlsButton.setName("controls");
        controlsButton.getLabel().setFontScale(FONT_SCALING);
        customizeCatButton = new TextButton("Customize Cat", skin);
        customizeCatButton.setName("customize");
        customizeCatButton.getLabel().setFontScale(FONT_SCALING);
        goBackButton = new TextButton("Back", skin, "dark");
        goBackButton.getLabel().setFontScale(FONT_SCALING);
        displayTable = new Table();
        buttonTable = new Table();

        sfxSlider = new Slider(0, 100, 1, false, skin);
        sfxSlider.setValue(sfxVolume);
        musicSlider = new Slider(0, 100, 1, false, skin);
        musicSlider.setValue(musicVolume);
        sfxLabel = new Label("Sound Effects", skin, "font", Color.WHITE);
        sfxLabel.setFontScale(LABEL_FONT_SCALING);
        musicLabel = new Label("Music", skin, "font", Color.WHITE);
        musicLabel.setFontScale(LABEL_FONT_SCALING);

        moveUpLabel = new Label("Move Up", skin, "font", Color.WHITE);
        moveUpLabel.setFontScale(LABEL_FONT_SCALING);
        moveDownLabel = new Label("Move Down", skin, "font", Color.WHITE);
        moveDownLabel.setFontScale(LABEL_FONT_SCALING);
        moveRightLabel = new Label("Move Right", skin, "font", Color.WHITE);
        moveRightLabel.setFontScale(LABEL_FONT_SCALING);
        moveLeftLabel = new Label("Move Left", skin, "font", Color.WHITE);
        moveLeftLabel.setFontScale(LABEL_FONT_SCALING);
        shootLaserLabel = new Label("Shoot Laser", skin, "font", Color.WHITE);
        shootLaserLabel.setFontScale(LABEL_FONT_SCALING);
        interactLabel = new Label("Interact", skin, "font", Color.WHITE);
        interactLabel.setFontScale(LABEL_FONT_SCALING);
        meowLabel = new Label("Meow", skin, "font", Color.WHITE);
        meowLabel.setFontScale(LABEL_FONT_SCALING);

        moveUpKeybind = new TextButton("W", skin);
        moveDownKeybind = new TextButton("S", skin);
        moveRightKeybind = new TextButton("D", skin);
        moveLeftKeybind = new TextButton("A", skin);
        shootLaserKeybind = new TextButton("Space", skin);
        interactKeybind = new TextButton("E", skin);
        meowKeybind = new TextButton("M", skin);

        furColorLabel = new Label("Fur Color", skin, "font", Color.WHITE);
        furColorLabel.setFontScale(LABEL_FONT_SCALING);
        furColorDropdown = new SelectBox<String>(skin);
        furColorDropdown.setItems(furColors);
        furColorDropdown.setAlignment(Align.center);
        furColorDropdown.getStyle().listStyle.font.getData().scale(SELECTBOX_SCALING);     
    }
    @Override
    public void positionActors() {
        //Once again feel free to change alignment and sizes
        buttonTable.add(audioButton).height(Gdx.graphics.getHeight() * 7 / 24).align(Align.left).width(Gdx.graphics.getWidth() / 3).expand();
        buttonTable.row();
        buttonTable.add(controlsButton).height(Gdx.graphics.getHeight() * 7 / 24).align(Align.left).width(Gdx.graphics.getWidth() / 3).expand();
        buttonTable.row();
        buttonTable.add(customizeCatButton).height(Gdx.graphics.getHeight() * 7 / 24).align(Align.left).width(Gdx.graphics.getWidth() / 3).expand();
        root.add(goBackButton).align(Align.topLeft).height(Gdx.graphics.getHeight() / 8).expand().width(Gdx.graphics.getWidth() / 3);     
        root.row();
        root.add(buttonTable).expand().fill();
        root.add(displayTable).expand().align(Align.left).fill();
        root.setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        stage.setRoot(root);
    }
    private void positionAudioOptions() {
        displayTable.clear();
        displayTable.add(sfxLabel).expandX().align(Align.left);
        displayTable.add(sfxSlider).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(musicLabel).expandX().align(Align.left);
        displayTable.add(musicSlider).expandX().align(Align.left);
    }
    private void positionControlsOptions() {
        displayTable.clear();
        displayTable.add(moveUpLabel).expandX().align(Align.left);
        displayTable.add(moveUpKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(moveDownLabel).expandX().align(Align.left);
        displayTable.add(moveDownKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(moveRightLabel).expandX().align(Align.left);
        displayTable.add(moveRightKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(moveLeftLabel).expandX().align(Align.left);
        displayTable.add(moveLeftKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(shootLaserLabel).expandX().align(Align.left);
        displayTable.add(shootLaserKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(interactLabel).expandX().align(Align.left);
        displayTable.add(interactKeybind).expandX().align(Align.left);
        displayTable.row();
        displayTable.add(meowLabel).expandX().align(Align.left);
        displayTable.add(meowKeybind).expandX().align(Align.left);
    }
    private void positionCustomizeCatOptions() {
        displayTable.clear();
        displayTable.add(furColorLabel).expandX().align(Align.left).fill();
        displayTable.add(furColorDropdown).expandX().align(Align.left).fillY();
    }
    @Override
    public void createTextures() {
        background = new Texture(Gdx.files.internal("TitleScreenBackground.jpg"));
    }
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        background.dispose();
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
    public static int[] getKeybinds() {
        return keybinds;
    }
    public static float getSFXVolume() {
        return sfxVolume;
    }
    public static float getMusicVolume() {
        return musicVolume;
    }
    public class KeybindListener extends ChangeListener {
        private int index;
        private TextButton button;
        public KeybindListener (int index, TextButton button) {
            this.index = index;
            this.button = button;
        }
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((Button) actor).isPressed()) {
                //After the button is pressed, let the keybind processor handle inputs for a single key press
                multiplexer.removeProcessor(stage);
                multiplexer.addProcessor(processor);
                processor.setIndex(index);
                processor.setButton(button);
            }
        }
    }
    public class KeybindProcessor extends InputAdapter {
        private int index;
        private TextButton button;
        public KeybindProcessor() {
            this.index = 0;
            this.button = null;
        }
        @Override
        public boolean keyDown(int keycode) {
            if (keycode != Input.Keys.ESCAPE) {
               //Change the keycode in the specified index with the keycode of the pressed key 
               for (int i = 0; i < keybinds.length; i++) {
                    if (keybinds[i] == keycode) {
                        multiplexer.removeProcessor(processor);
                        multiplexer.addProcessor(stage);
                        return false; 
                    }
               }
               keybinds[index] = keycode;
               //Also change button's text
               button.setText(Input.Keys.toString(keycode));
               //Update player's control scheme with the new keybinds
               Player.setControlScheme(keybinds);
               // serialize to bytes
                byte[] keybytes = new byte[keybinds.length];
                for (int i = 0; i < keybinds.length; i++) {
                    keybytes[i] = (byte) keybinds[i];
                }
                keybindsBin.writeBytes(keybytes, false);
            }
            //Make it so that the stage handles input after mapping of keybind
            multiplexer.removeProcessor(processor);
            multiplexer.addProcessor(stage);
            return true; 
        }
        /**
         * Helper method for specifying the index to replace in the keybind array
         * @param index
         */
        private void setIndex(int index) {
            this.index = index;
        }
        /**
         * Helper method for specifying the button that should have its text replaced
         * @param button
         */
        private void setButton(TextButton button) {
            this.button = button;
        }
    }
    public static Color getSelectedColor() {
        if (selectedColor.equals("White")) {
            return Color.WHITE;
        }
        else if (selectedColor.equals("Red")) {
            return Color.RED;
        }
        else if (selectedColor.equals("Green")) {
            return Color.GREEN;
        }
        else if (selectedColor.equals("Blue")) {
            return Color.BLUE;
        }
        return null;
    }
    private void loadKeybinds() {
        // Load from binary data
        keybindsBin = Gdx.files.local("bins/keybinds.bin");
        byte[] keybytes = keybindsBin.readBytes();

        keybinds = new int[7];
        for (int i = 0; i < keybinds.length; i++) {
            keybinds[i] = (int) keybytes[i];
        }
        moveUpKeybind.setText(Input.Keys.toString(keybinds[0]));
        moveDownKeybind.setText(Input.Keys.toString(keybinds[1]));
        moveRightKeybind.setText(Input.Keys.toString(keybinds[2]));
        moveLeftKeybind.setText(Input.Keys.toString(keybinds[3]));
        shootLaserKeybind.setText(Input.Keys.toString(keybinds[4]));
        interactKeybind.setText(Input.Keys.toString(keybinds[5]));
        meowKeybind.setText(Input.Keys.toString(keybinds[6]));
        //Update player's control scheme with the loaded keybinds
        Player.setControlScheme(keybinds);
    }
    private void loadColor() {
        colorBin = Gdx.files.local("bins/color.bin");
        selectedColor = colorBin.readString();
        furColorDropdown.setSelected(selectedColor);
    }
    private void loadVolumeLevels() {
        audioBin = Gdx.files.local("bins/volume.bin");
        byte[] volumeBytes = audioBin.readBytes();
        sfxVolume = (float) volumeBytes[0];
        musicVolume = (float) volumeBytes[1];
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        buttonTable.clear();
        root.clear();
        positionActors();
    }
}