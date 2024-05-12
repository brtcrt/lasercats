package com.lasercats.Screens;

import java.util.ArrayList;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.PhysicsObject;
import com.lasercats.GameObjects.Player;

public class MainMenuScreen extends LaserCatsScreen {
    
    private ImageButton tutorialButton;
    private ImageButton exitButton;
    private ImageButton optionsButton;
    private ImageButton playButton;
    private ImageButton levelEditorButton;

    private Table buttonTable;

    private Texture laserPointerOne;
    private Texture laserPointerTwo;
    private Texture title;
    private Texture background;
    private Texture optionsTexture;
    private Texture tutorialTexture;
    private Texture exitButtonTexture;
    private Texture playButtonTexture;
    private Texture levelEditorButtonTexture;
    private Texture catImageOne;
    private Texture catImageTwo;

    private Client client;
    //Includes just the players initially
    private ArrayList<GameObject> initialGameObjects;
    private ArrayList<PhysicsObject> initialPhysicsObjects;
    private Player player;
    private Player otherPlayer;

    private static int width = Gdx.graphics.getWidth();
    private static int height = Gdx.graphics.getHeight();

    //A general note about screen implementation. Some of the code might be redundant here because of how libGDX'S classes internally handle things.
    //Feel free to remove the unnecessary parts.
    public MainMenuScreen(Game game) {
        super(game);
        this.player = new Player(32, 32, 64, 64, true);
		this.otherPlayer = new Player(-300, -300, 64, 64, false);
        initialGameObjects = new ArrayList<GameObject>();
        initialPhysicsObjects = new ArrayList<PhysicsObject>();
        initialGameObjects.add(player);
        initialGameObjects.add(otherPlayer);
        initialPhysicsObjects.add(player);
        initialPhysicsObjects.add(otherPlayer);
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
        client = new Client(initialGameObjects, initialPhysicsObjects, genericViewport);
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
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
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        genericViewport.apply();
        this.stage.draw();
        //One interesting thing about using stage is that because all resources of the main menu are part of the stage, when we call stage.draw() it draws all the resources.
        //So no need for batch.begin(), batch.end()
        //In fact, stage.draw() internally calls this sequence.
    }
    @Override
    public void dispose() {
        this.laserPointerOne.dispose();
        laserPointerTwo.dispose();
        catImageOne.dispose();
        catImageTwo.dispose();
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
    }
    @Override
    public void createTextures() {
        this.laserPointerOne = new Texture(Gdx.files.internal("laser_pointer-1-long.png"));
        laserPointerTwo = new Texture(Gdx.files.internal("laser_pointer-2-long.png"));
        this.title = new Texture(Gdx.files.internal("Title.png"));
        background = new Texture(Gdx.files.internal("TitleScreenBackground.jpg"));
        optionsTexture = new Texture(Gdx.files.internal("OptionsIcon.png"));
        tutorialTexture = new Texture(Gdx.files.internal("TutorialIcon.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("ExitButtonIcon.png"));
        playButtonTexture = new Texture(Gdx.files.internal("PlayButton.png"));
        levelEditorButtonTexture = new Texture(Gdx.files.internal("LevelEditor.png"));
        //This is the best I can do in terms of picture quality.
        catImageOne = new Texture(Gdx.files.internal("cat-256x256-Flipped.png"));
        catImageTwo = new Texture(Gdx.files.internal("cat-256x256.png"));

    }
    @Override
    public void positionActors() {
        //Feel free to play around with alignment and cell sizes.
        this.buttonTable.add(optionsButton).align(Align.topLeft).padLeft(width / 32).width(width / 32).height(height / 18).padTop(height / 18).expand();
        this.buttonTable.add(tutorialButton).align(Align.topRight).width(width / 16).height(height / 9).padLeft(width / 32).expand().padTop(height / 36);
        this.root.add(buttonTable).align(Align.topLeft).expand();
        this.root.add(exitButton).align(Align.topRight).width(width / 32).height(height / 18).expand().colspan(2).padTop(height / 18).padRight(width / 32);
        this.root.row();
        this.root.add(new Image(laserPointerOne)).colspan(3);
        this.root.row();
        this.root.add(new Image(title)).colspan(3).height(height / 7).width((width / 3));
        this.root.row();
        root.add(new Image(laserPointerTwo)).colspan(3);
        root.row();
        root.add(new Image(catImageOne)).expandX().align(Align.left).width((float) (width / 4.8));
        this.root.add(playButton).expandX().height(height / 8);
        root.add(new Image(catImageTwo)).expandX().align(Align.right).width((float) (width / 4.8));
        this.root.row();
        this.root.add(levelEditorButton).width(width / 3).height(height / 5).colspan(3);
        this.stage.setRoot(root);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(background)));
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
        this.playButton.addListener(new ScreenListener(new LobbyScreen(game, this), game));
        this.levelEditorButton.addListener(new ScreenListener(new LevelEditor(game, this), game));
        this.optionsButton.addListener(new ScreenListener(new OptionsScreen(game, this), game));
        this.tutorialButton.addListener(new ScreenListener(new TutorialScreen(game, this), game));

    }
    public Client getClient() {
        return this.client;
    }
}
