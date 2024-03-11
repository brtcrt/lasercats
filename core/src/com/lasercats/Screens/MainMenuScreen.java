package com.lasercats.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends LaserCatsScreen {
    
    private ImageButton tutorialButton;
    private ImageButton exitButton;
    private ImageButton optionsButton;
    private TextButton createLobbyButton;
    private TextButton joinLobbyButton; 
    private TextButton levelEditorButton;

    private Table buttonTable;

    private Texture laserPointer;
    private Texture catImageOne;
    private Texture catImageTwo;
    private Texture title;

    //A general note about screen implementation. Some of the code might be redundant here because of how libGDX'S classes internally handle things.
    //Feel free to remove the unnecessary parts.
    public MainMenuScreen(Game game) {
        super(game);
        this.genericViewport = new ScreenViewport(camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        Gdx.input.setInputProcessor(stage);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());

        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        this.createActors();
        this.createTextures();
    }
    @Override
    public void show() {
        this.positionActors();
        this.setListeners();
    }
    @Override
    public void resize(int width, int height) {
        this.genericViewport.update(width, height, true);
        this.root.setHeight(Gdx.graphics.getHeight());
        this.root.setWidth(Gdx.graphics.getWidth());
    }
    @Override
    public void render(float delta) {
        //Background can be something else. Feel free to change this.
        ScreenUtils.clear(Color.ORANGE);
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
    public void hide() {
        //The screen should dispose all of its resources when it is no longer the current screen.
        this.dispose();
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
    public void createActors() {
        //TODO Placeholder style names. Change later.
        this.tutorialButton = new ImageButton(skin, "default");
        this.optionsButton = new ImageButton(skin, "default");
        this.exitButton = new ImageButton(skin, "default");
        this.createLobbyButton = new TextButton("Create Lobby", skin, "default");
        this.joinLobbyButton = new TextButton("Join Lobby", skin, "default");
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
        this.root.add(createLobbyButton).width(200).height(50).expandX().colspan(3);
        this.root.row();
        this.root.add(new Image(catImageOne)).expandX().align(Align.left);
        this.root.add(joinLobbyButton).width(200).padTop(50).height(50).padRight(100);
        this.root.add(new Image(catImageTwo)).expandX().align(Align.right);
        this.root.row();
        this.root.add(levelEditorButton).width(200).padTop(50).height(50).colspan(3);
        this.stage.setRoot(root);
        //this.stage.setDebugAll(true);
    }
    @Override
    //Organization is very messy here. Feel free to seperate this part into different classes. 
    public void setListeners() {
        this.exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (exitButton.isChecked()) {
                    //When the exit button is pressed, the screen should dispose all its sources and close the application.
                    Gdx.app.exit();
                }
            }
        });
        this.createLobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (createLobbyButton.isChecked()) {
                    //TODO Change to lobbyCreationScreen.
                    game.setScreen(null);
                }
            }
        });
        this.joinLobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (joinLobbyButton.isChecked()) {
                    //TODO Change to joinLobbyScreen.
                    game.setScreen(null);
                }
            }
        });
        this.levelEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (levelEditorButton.isChecked()) {
                    //TODO Change to levelEditorScreen.
                    game.setScreen(null);
                }
            }
        });
        this.optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (optionsButton.isChecked()) {
                    //TODO Change to optionsScreen.
                    game.setScreen(null);
                }
            }
        });
        this.tutorialButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (tutorialButton.isChecked()) {
                    //TODO Change to tutorialScreen.
                    game.setScreen(null);
                }
            }
        });
    }
}
