package com.lasercats.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;
import com.lasercats.GameObjects.PlayerNonMain;

public class MainMenuScreen extends LaserCatsScreen {
    
    private ImageButton tutorialButton;
    private ImageButton exitButton;
    private ImageButton optionsButton;
    private TextButton playButton;
    private TextButton levelEditorButton;

    private Table buttonTable;

    private Texture laserPointer;
    private Texture catImageOne;
    private Texture catImageTwo;
    private Texture title;

    private Client client;
    //Includes just the players initially
    private ArrayList<GameObject> initialGameObjects;
    private Player player;
    private PlayerNonMain otherPlayer;

    //A general note about screen implementation. Some of the code might be redundant here because of how libGDX'S classes internally handle things.
    //Feel free to remove the unnecessary parts.
    public MainMenuScreen(Game game) {
        super(game);
        this.player = new Player(32, 32, 128, 80);
		this.otherPlayer = new PlayerNonMain(-300, -300, 128, 80);
        initialGameObjects = new ArrayList<GameObject>();
        initialGameObjects.add(player);
        initialGameObjects.add(otherPlayer);
        client = new Client(initialGameObjects);
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        this.createActors();
        this.createTextures();
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
        ScreenUtils.clear(Color.RED);
        Gdx.input.setInputProcessor(stage);
        this.camera.update();
        delta = Gdx.graphics.getDeltaTime();
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();
        //One interesting thing about using stage is that because all resources of the main menu are part of the stage, when we call stage.draw() it draws all the resources.
        //So no need for batch.begin(), batch.end()
        //In fact, stage.draw() internally calls this sequence.
    }
    @Override
    public void dispose() {
        this.laserPointer.dispose();
        this.catImageOne.dispose();
        this.catImageTwo.dispose();
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
        //TODO Placeholder style names. Change later.
        this.tutorialButton = new ImageButton(skin, "default");
        this.optionsButton = new ImageButton(skin, "default");
        this.exitButton = new ImageButton(skin, "default");
        this.playButton = new TextButton("Play", skin, "default");
        this.levelEditorButton = new TextButton("Level Editor", skin, "default");
        this.buttonTable = new Table();
        this.root.setOrigin(0, 0);
        this.root.setHeight(Gdx.graphics.getHeight());
        this.root.setWidth(Gdx.graphics.getWidth());
    }
    public void createTextures() {
        //TODO Find and set assets later. Work with placeholders for testing purposes.
        this.laserPointer = new Texture(Gdx.files.internal("Cat.png"));
        this.catImageOne = new Texture(Gdx.files.internal("Cat.png"));
        this.catImageTwo = new Texture(Gdx.files.internal("Cat.png"));
        this.title = new Texture(Gdx.files.internal("Cat.png"));
    }
    @Override
    public void positionActors() {
        //Feel free to play around with alignment and cell sizes.
        this.buttonTable.add(optionsButton).align(Align.topLeft).padRight(10).width(60).height(60);
        this.buttonTable.add(tutorialButton).align(Align.topLeft).width(60).height(60);
        this.root.add(buttonTable).align(Align.topLeft).expand();
        this.root.add(exitButton).align(Align.topRight).width(60).height(60).expand().colspan(2);
        this.root.row();
        this.root.add(new Image(laserPointer)).colspan(3).padBottom(20);
        this.root.row();
        this.root.add(new Image(title)).padBottom(175).colspan(3);
        this.root.row();
        this.root.add(playButton).width(200).height(50).expandX().colspan(3);
        this.root.row();
        this.root.add(new Image(catImageOne)).expandX().align(Align.left).padRight(120);
        this.root.add(new Image(catImageTwo)).expandX().align(Align.right);
        this.root.row();
        this.root.add(levelEditorButton).width(200).height(50).colspan(3).padBottom(100);
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
        this.tutorialButton.addListener(new ScreenListener(null, game));

    }
    public Client getClient() {
        return this.client;
    }
}
