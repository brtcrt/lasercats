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
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.GameObjects.Player;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class OptionsScreen extends LaserCatsScreen {

    private ButtonGroup buttonGroup;
    private Button audioButton;
    private Button controlsButton;
    private Button customizeCatButton;

    private Texture audioButtonTexture;
    private Texture audioButtonTextureDown;
    private Texture controlsButtonTexture;
    private Texture controlsButtonTextureDown;
    private Texture customizeCatButtonTexture;
    private Texture customizeCatButtonTextureDown;

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
    private SelectBox furColorDropdown;
    private String[] furColors;
    private static String selectedColor;
    private FileHandle colorBin;

    private MainMenuScreen menu;

    private Button goBackButton;
    private Texture goBackTexture;

    private static int[] keybinds;
    private FileHandle keybindsBin;

    private KeybindProcessor processor;
    private InputMultiplexer multiplexer;

    private static float sfxVolume;
    private static float musicVolume;
    private FileHandle audioBin;

    public OptionsScreen(Game game, MainMenuScreen menu) {
        super(game);
        this.menu = menu;
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();

        processor = new KeybindProcessor();
        //Using a multiplexer for handling inputs via this way is really dumb btw
        //If you think about a better solution feel free to change this part
        multiplexer = new InputMultiplexer();

        //We can change this part if you guys prefer to have a color changing system that has more options.
        this.furColors = new String[] {"White", "Red", "Green", "Blue"};
        this.stage = new Stage(genericViewport, batch);
        multiplexer.addProcessor(stage);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);;
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
        ScreenUtils.clear(Color.LIGHT_GRAY);
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
        audioButton = new Button(new TextureRegionDrawable(new TextureRegion(audioButtonTexture)), new TextureRegionDrawable(new TextureRegion(audioButtonTextureDown)), new TextureRegionDrawable(new TextureRegion(audioButtonTextureDown)));
        audioButton.setName("audio");
        controlsButton = new Button(new TextureRegionDrawable(new TextureRegion(controlsButtonTexture)), new TextureRegionDrawable(new TextureRegion(controlsButtonTextureDown)), new TextureRegionDrawable(new TextureRegion(controlsButtonTextureDown)));
        controlsButton.setName("controls");
        customizeCatButton = new Button(new TextureRegionDrawable(new TextureRegion(customizeCatButtonTexture)), new TextureRegionDrawable(new TextureRegion(customizeCatButtonTextureDown)), new TextureRegionDrawable(new TextureRegion(customizeCatButtonTextureDown)));
        customizeCatButton.setName("customize");
        displayTable = new Table();
        buttonTable = new Table();

        sfxSlider = new Slider(0, 100, 1, false, skin);
        sfxSlider.setValue(sfxVolume);
        musicSlider = new Slider(0, 100, 1, false, skin);
        musicSlider.setValue(musicVolume);
        sfxLabel = new Label("Sound Effects", skin);
        musicLabel = new Label("Music", skin);
        musicLabel.setColor(Color.WHITE);

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

        goBackButton = new Button(new TextureRegionDrawable(new TextureRegion(goBackTexture)));

        buttonGroup = new ButtonGroup<>(audioButton, controlsButton, customizeCatButton);
    }
    @Override
    public void positionActors() {
        //Once again feel free to change alignment and sizes
        buttonTable.add(goBackButton).height((float) ((Gdx.graphics.getHeight() * 0.3 / 3.3))).align(Align.left).growX();
        buttonTable.row();
        buttonTable.add(audioButton).height((float) (Gdx.graphics.getHeight() / 3.3)).growX();
        buttonTable.row();
        buttonTable.add(controlsButton).height((float) (Gdx.graphics.getHeight() / 3.3)).growX();
        buttonTable.row();
        buttonTable.add(customizeCatButton).height((float) (Gdx.graphics.getHeight() / 3.3)).growX();
        root.add(buttonTable);
        root.add(displayTable).expand().fill();

        stage.setRoot(root);
        //stage.setDebugAll(true);
    }
    private void positionAudioOptions() {
        displayTable.clear();
        displayTable.add(sfxLabel).expandX();
        displayTable.add(sfxSlider).expandX();
        displayTable.row();
        displayTable.add(musicLabel).expandX();
        displayTable.add(musicSlider).expandX();;
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
    private void createTextures() {
        audioButtonTexture = new Texture(Gdx.files.internal("AudioButtonUnpressed.png"));
        audioButtonTextureDown = new Texture(Gdx.files.internal("AudioButtonPressed.png"));

        controlsButtonTexture = new Texture(Gdx.files.internal("ControlsButtonUnpressed.png"));
        controlsButtonTextureDown = new Texture(Gdx.files.internal("ControlsButtonPressed.png"));

        customizeCatButtonTexture = new Texture(Gdx.files.internal("CustomizeCatButtonUnpressed.png"));
        customizeCatButtonTextureDown = new Texture(Gdx.files.internal("CustomizeCatButtonPressed.png"));

        goBackTexture = new Texture(Gdx.files.internal("GoBackIcon.jpg"));
    }
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        audioButtonTexture.dispose();
        controlsButtonTexture.dispose();
        customizeCatButtonTexture.dispose();
        audioButtonTextureDown.dispose();
        controlsButtonTextureDown.dispose();
        customizeCatButtonTextureDown.dispose();
        goBackTexture.dispose();
    }
    @Override
    public void resume() {}
    public class OptionsButtonListener extends ChangeListener  {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((Button) actor).isChecked()) {
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
            //TODO handle duplicate keybinds.
            if (keycode != Input.Keys.ESCAPE) {
               //Change the keycode in the specified index with the keycode of the pressed key 
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
    //Absolute garbage implementation, please change this.
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
        // load from binary data
        keybindsBin = Gdx.files.local("bins/keybinds.bin");
        byte[] keybytes = keybindsBin.readBytes();

        keybinds = new int[7];
        for (int i = 0; i < keybinds.length; i++) {
            keybinds[i] = (int) keybytes[i];
        }
        // This is fucking disgusting kill me ~brtcrt
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
}
