package com.lasercats.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;

public class MainMenuScreen extends LaserCatsScreen {
    
    private ImageButton tutorialButton;
    private ImageButton exitButton;
    private ImageButton optionsButton;
    private ImageButton playButton;
    private ImageButton levelEditorButton;

    private Table buttonTable;

    //I decided against using cat images in the title screen because 32x32 cat image looks horrible in higher resolutions.
    //If we find another cat image we can use that.
    private Texture laserPointerOne;
    private Texture laserPointerTwo;
    private Texture title;
    private Texture background;
    private Sprite backgroundSprite;
    private Texture optionsTexture;
    private Texture tutorialTexture;
    private Texture exitButtonTexture;
    private Texture playButtonTexture;
    private Texture levelEditorButtonTexture;

    private Client client;
    //Includes just the players initially
    private ArrayList<GameObject> initialGameObjects;
    private Player player;
    private Player otherPlayer;

    //Since the game is played in fullscreen, these values will never change
    private final static int WIDTH = Gdx.graphics.getWidth();
    private final static int HEIGHT = Gdx.graphics.getHeight();

    //A general note about screen implementation. Some of the code might be redundant here because of how libGDX'S classes internally handle things.
    //Feel free to remove the unnecessary parts.
    public MainMenuScreen(Game game) {
        super(game);
        this.player = new Player(32, 32, 128, 80, true);
		this.otherPlayer = new Player(-300, -300, 128, 80, false);
        initialGameObjects = new ArrayList<GameObject>();
        initialGameObjects.add(player);
        initialGameObjects.add(otherPlayer);
        client = new Client(initialGameObjects);
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        this.createTextures();
        this.createActors();
        positionActors();
        setListeners();
    }
        public class ScreenListener extends ChangeListener {
        private Screen screen;
        private Game game;
        public ScreenListener(Screen screen, Game game) {
            this.screen = screen;
            this.game = game;
        }
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (((Button) actor).isPressed()) {
                game.setScreen(screen);
            }
        }
    }
    @Override
    public void render(float delta) {
        //Background can be something else. Feel free to change this.
        ScreenUtils.clear(Color.LIGHT_GRAY);
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        batch.begin();
        backgroundSprite.setCenter(WIDTH / 2, HEIGHT / 2);
        backgroundSprite.draw(batch);
        batch.end();
        this.stage.draw();
        //One interesting thing about using stage is that because all resources of the main menu are part of the stage, when we call stage.draw() it draws all the resources.
        //So no need for batch.begin(), batch.end()
        //In fact, stage.draw() internally calls this sequence.
    }
    @Override
    public void dispose() {
        this.laserPointerOne.dispose();
        laserPointerTwo.dispose();
        optionsTexture.dispose();
        exitButtonTexture.dispose();
        tutorialTexture.dispose();
        playButtonTexture.dispose();
        levelEditorButtonTexture.dispose();
        background.dispose();
        this.batch.dispose();
        this.title.dispose();
        this.stage.dispose();
    }
    @Override
    public void resume() {}
    @Override
    public void pause() {}
    @Override
    public void createActors() {
        this.tutorialButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(tutorialTexture)));
        this.optionsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(optionsTexture)));
        this.exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitButtonTexture)));
        this.playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playButtonTexture)));
        this.levelEditorButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(levelEditorButtonTexture)));
        this.buttonTable = new Table();
        this.root.setOrigin(0, 0);
        this.root.setHeight(Gdx.graphics.getHeight());
        this.root.setWidth(Gdx.graphics.getWidth());
    }
    public void createTextures() {
        //I think the assets could be heavily improved however I am going to be honest I didn't want to spend a few hours for finding assets.
        this.laserPointerOne = new Texture(Gdx.files.internal("laser_pointer-1.png"));
        laserPointerTwo = new Texture(Gdx.files.internal("laser_pointer-2.png"));
        this.title = new Texture(Gdx.files.internal("Title.png"));
        background = new Texture(Gdx.files.internal("TitleScreenBackground.jpg"));
        backgroundSprite = new Sprite(background);
        backgroundSprite.scale((float) 0.25);
        optionsTexture = new Texture(Gdx.files.internal("OptionsIcon.png"));
        tutorialTexture = new Texture(Gdx.files.internal("TutorialIcon.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("ExitButtonIcon.png"));
        playButtonTexture = new Texture(Gdx.files.internal("PlayButton.png"));
        levelEditorButtonTexture = new Texture(Gdx.files.internal("LevelEditor.png"));
    }
    @Override
    public void positionActors() {
        //Feel free to play around with alignment and cell sizes.
        this.buttonTable.add(optionsButton).align(Align.topLeft).padLeft(WIDTH / 32).width(WIDTH / 32).height(HEIGHT / 18).padTop(HEIGHT / 18).expand();
        this.buttonTable.add(tutorialButton).align(Align.topRight).width(WIDTH / 16).height(HEIGHT / 9).padLeft(WIDTH / 32).expand().padTop(HEIGHT / 36);
        this.root.add(buttonTable).align(Align.topLeft).expand();
        this.root.add(exitButton).align(Align.topRight).width(WIDTH / 32).height(HEIGHT / 18).expand().colspan(2).padTop(HEIGHT / 18).padRight(WIDTH / 32);
        this.root.row();
        this.root.add(new Image(laserPointerOne)).colspan(3).height((int) (HEIGHT / 3.4));
        this.root.row();
        this.root.add(new Image(title)).colspan(3).height(HEIGHT / 7).width(WIDTH / 4);
        this.root.row();
        root.add(new Image(laserPointerTwo)).colspan(3).height((int) (HEIGHT / 3.4));
        root.row();
        this.root.add(playButton).width(WIDTH / 6).height(HEIGHT / 15).expandX().colspan(3);
        this.root.row();
        this.root.add(levelEditorButton).width(WIDTH / 6).height(HEIGHT / 8).expandX().colspan(3).padBottom(HEIGHT / 36);
        this.stage.setRoot(root);
        //this.stage.setDebugAll(true);
    }
    @Override
    public void setListeners() {
        this.exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (exitButton.isPressed()) {
                    //When the exit button is pressed, the screen should dispose all its sources and close the application.
                    Gdx.app.exit();
                }
            }
        });
        //TODO change screens later.
        this.playButton.addListener(new ScreenListener(new LobbyScreen(game, this), game));
        this.levelEditorButton.addListener(new ScreenListener(null, game));
        this.optionsButton.addListener(new ScreenListener(new OptionsScreen(game, this), game));
        this.tutorialButton.addListener(new ScreenListener(new TutorialScreen(game, this), game));

    }
    public Client getClient() {
        return this.client;
    }
}
