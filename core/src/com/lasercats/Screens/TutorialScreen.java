package com.lasercats.Screens;

import java.util.ArrayList;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TutorialScreen extends LaserCatsScreen {

    private TextButton goBackButton;

    private ArrayList<Tutorial> tutorials; 
    private VerticalGroup tutorialList;
    private Table tutorialNameTable;
    private Texture background;
    private Pixmap tutorialTableBackgroundColor;
    private ArrayList<Image> tutorialImages;
    private ArrayList<Texture> tutorialTextures;
    private final static int TUTORIAL_COUNT = 9;
    private Table tutorialDisplayTable;
    private String[] tutorialNames;
    private String[] tutorialDescriptions;

    private MainMenuScreen menu;

    public TutorialScreen(Game game, MainMenuScreen menu) {
        super(game);
        this.menu = menu;
        genericViewport = new ScreenViewport(camera);
        genericViewport.apply();
        stage = new Stage(genericViewport, batch);
        camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        root.setFillParent(true);

        tutorials = new ArrayList<Tutorial>(TUTORIAL_COUNT);
        tutorialNames = new String[TUTORIAL_COUNT];
        tutorialDescriptions = new String[TUTORIAL_COUNT];
        tutorialImages = new ArrayList<Image>(TUTORIAL_COUNT);
        tutorialTextures = new ArrayList<Texture>(TUTORIAL_COUNT);

        createTextures();
        createTutorialNames();
        createTutorialDescriptions();
        createActors();
        positionActors();
        setListeners();
    }
    @Override
    public void createActors() {
        goBackButton = new TextButton("Back", skin, "dark");
        tutorialList = new VerticalGroup();
        for (int i  = 0; i < TUTORIAL_COUNT; i++) {
            addTutorial(tutorialNames[i], tutorialDescriptions[i], tutorialImages.get(i));
        }
        tutorialDisplayTable = new Table(skin);
        tutorialNameTable = new Table(skin);
        tutorialNameTable.setBackground(new TextureRegionDrawable(new Texture(tutorialTableBackgroundColor)));  
    }
    private void addTutorial(String name, String description, Image image) {
            Tutorial tutorial = new Tutorial(skin);
            tutorial.setTutorialName(name);
            tutorial.setTutorialDescription(description);
            tutorial.setTutorialImage(image);
            tutorials.add(tutorial);
            tutorialList.addActor(tutorial.getTutorialButton());
    }
    private void createTutorialNames() {
            tutorialNames[0] = "Lobby Creation";
            tutorialNames[1] = "Level Editor Showcase";
            tutorialNames[2] = "Level Editor Showcase 2";
            tutorialNames[3] = "Level Editor Showcase 3";
            tutorialNames[4] = "Level Editor Showcase 4";
            tutorialNames[5] = "Required Puzzle Elements";
            tutorialNames[6] = "Other Puzzle Elements";
            tutorialNames[7] = "Other Puzzle Elements 2";
            tutorialNames[8] = "Options and Keybinds";
    }
    private void createTutorialDescriptions() {
            tutorialDescriptions[0] = "To create a lobby, enter the name of your lobby. You can also create a password for your lobby if you want.";
            tutorialDescriptions[1] = "Click on the level elements to the right and place them on the screen. You can also move an element you placed before. Press Backspace on an element to delete it.";
            tutorialDescriptions[2] = "Right click on a detector element and right click on a gate to make it so that gate triggers from the changes in the detector element.";
            tutorialDescriptions[3] = "You can only place one of each required elements which are the entrances for the cats and the exit gate.";
            tutorialDescriptions[4] = "You can save and import levels by entering the necessary information on the window.";
            tutorialDescriptions[5] = "The main objective of every level is getting the two cats to the end door of a level.";
            tutorialDescriptions[6] = "Whether that be laser targets, pressure plates, buttons, mirrors and additional gates, every level consists of multiple obstacles for players to get through.";
            tutorialDescriptions[7] = "Pressure plates and laser targets are part of a special set of elements called detectors. Changes in detectors cause gates to open.";
            tutorialDescriptions[8] = "If you are not happy with the current audio levels and keybinds, you can access the options menu to change these anytime. Furthermore, you can change the color of your cat as well!";
    }
    private void createTutorialDisplayTable(Tutorial tutorial) {
            tutorialDisplayTable.clear();
            tutorialDisplayTable.add(tutorial.getTutorialImage());
            tutorialDisplayTable.row();
            tutorialDisplayTable.add(tutorial.getTutorialDescription()).width(Gdx.graphics.getWidth() * 4 / 5);    
    }
    @Override
    public void positionActors() {
        tutorialNameTable.add(tutorialList).align(Align.top).expand().fillX();
        root.add(goBackButton).align(Align.topLeft).width(root.getWidth() / 5);
        root.row();
        root.add(tutorialNameTable).expandY().fill().align(Align.left);
        root.add(tutorialDisplayTable).expandX().fillX();
        stage.setRoot(root);
        root.setBackground(new TextureRegionDrawable(background));
    }
    @Override
    public void setListeners() {
            goBackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (goBackButton.isPressed()) {
                        game.setScreen(menu);
                    }
                }
            });
            for (Tutorial tutorial : tutorials) {
                tutorial.getTutorialButton().addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (tutorial.getTutorialButton().isPressed()) {
                            tutorialDisplayTable.clear();
                            tutorialNameTable.clear();
                            createTutorialDisplayTable(tutorial);
                            root.clear();
                            positionActors();
                        }
                    }
                });
            }
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
        tutorialTableBackgroundColor.dispose();
        for (Texture texture : tutorialTextures) {
            texture.dispose();
        }
    }
    @Override
    public void createTextures() {
            background = new Texture(Gdx.files.internal("TitleScreenBackground.jpg"));
            tutorialTableBackgroundColor = new Pixmap(1, 1, Pixmap.Format.RGB565);
            tutorialTableBackgroundColor.setColor(0.82745098039215686274509803921569f, 0.82745098039215686274509803921569f, 0.82745098039215686274509803921569f, 1);
            tutorialTableBackgroundColor.fill();

            //An important note about this part: The reason I'm creating seperate textures instead of using them anonymously is because 
            //if you use them anonymously you can't dispose them (because Texture class manages disposing not the Image class) later which causes memory leaks.
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/LobbyCreation.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Level-Editor-Showcase.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Level-Editor-Showcase-2.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Level-Editor-Showcase-3.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Level-Editor-Showcase-4.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Door.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/OtherLevelElements.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/OtherLevelElements-2.png")));
            tutorialTextures.add(new Texture(Gdx.files.internal("Tutorial-Images/Cats.png")));

            for (Texture texture : tutorialTextures) {
                tutorialImages.add(new Image(texture));
            }
    }
    @Override
    public void render(float delta) {
            Gdx.input.setInputProcessor(stage);
            this.camera.update();
            delta = Gdx.graphics.getDeltaTime();
            this.stage.act(delta);
            this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
            this.stage.draw();
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        root.clear();
        tutorialNameTable.clear();
        positionActors();
    }
}
