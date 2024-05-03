package com.lasercats.Screens;

import java.util.ArrayList;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TutorialScreen extends LaserCatsScreen {
   
   private ArrayList<Tutorial> tutorials; 
   private ImageButton goBackButton;
   private VerticalGroup tutorialList;
   private Table tutorialDisplayTable;
   private MainMenuScreen menu;
   //Feel free to change this number.
   private final static int TUTORIAL_COUNT = 6;

   private String[] tutorialNames;
   private String[] tutorialDescriptions;

   public TutorialScreen(Game game, MainMenuScreen menu) {
    super(game);
    this.menu = menu;
    genericViewport = new ScreenViewport(camera);
    genericViewport.apply();
    stage = new Stage(genericViewport, batch);
    camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
    root.setFillParent(true);

    tutorials = new ArrayList<Tutorial>();
    tutorialNames = new String[TUTORIAL_COUNT];
    tutorialDescriptions = new String[TUTORIAL_COUNT];

    createTutorialNames();
    createTutorialDescriptions();
    createActors();
    positionActors();
    setListeners();
   }
   @Override
   public void createActors() {
       goBackButton = new ImageButton(skin);
       tutorialList = new VerticalGroup();
       for (int i  = 0; i < TUTORIAL_COUNT; i++) {
            addTutorial(tutorialNames[i], tutorialDescriptions[i]);
       }
       tutorialDisplayTable = new Table();  
   }
   private void addTutorial(String name, String description) {
        Tutorial tutorial = new Tutorial(skin);
        tutorial.setTutorialName(name);
        tutorial.setTutorialDescription(description);
        tutorials.add(tutorial);
        tutorialList.addActor(tutorial.getTutorialButton());
   }
   private void addTutorial(String name, String description, Drawable image) {
        Tutorial tutorial = new Tutorial(skin);
        tutorial.setTutorialName(name);
        tutorial.setTutorialDescription(description);
        tutorial.setTutorialImage(image);
        tutorials.add(tutorial);
        tutorialList.addActor(tutorial.getTutorialButton());
   }
   private void createTutorialNames() {
        //TODO Change later, currently placeholder
        tutorialNames[0] = "Lobby Creation";
        tutorialNames[1] = "Level Editor";
        tutorialNames[2] = "Level Editor Showcase";
        tutorialNames[3] = "Required Puzzle Elements";
        tutorialNames[4] = "Other Puzze Elements";
        tutorialNames[5] = "Options and Keybinds";
   }
   private void createTutorialDescriptions() {
        //TODO Change later, currently placeholder
        tutorialDescriptions[0] = "To create a lobby, enter the name of your lobby. If you want, create a password for that lobby.";
        tutorialDescriptions[1] = "You can access the level editor of the game through the main menu and create your own levels!";
        //TODO will complete description later after level editor is done.
        tutorialDescriptions[2] = "";
        tutorialDescriptions[3] = "The main objective of every level is getting the two cats to the end door of a level.";
        tutorialDescriptions[4] = "Whether that be laser targets, pressure plates, buttons, mirrors and additional gates, every level consists of multiple obstacles for players to get through.";
        tutorialDescriptions[5] = "If you are not happy with the current audio levels and keybinds, you can access the options menu to change these anytime. Furthermore, you can change the color of your cat as well!";
   }
   private void createTutorialDisplayTable(Tutorial tutorial) {
        tutorialDisplayTable.clear();
        tutorialDisplayTable.add(tutorial.getTutorialImage());
        tutorialDisplayTable.row();
        tutorialDisplayTable.add(tutorial.getTutorialDescription());    
   }
   @Override
   public void positionActors() {
       root.add(goBackButton).align(Align.topLeft).width(60).height(60);
       root.row();
       root.add(tutorialList).expandY().fillY().align(Align.left);
       root.add(tutorialDisplayTable).expandX().fillX();
       stage.setRoot(root);
       //stage.setDebugAll(true);
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
}
