package com.lasercats.Screens;

import java.util.ArrayList;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
   private ShapeRenderer drawer;
   private Table tutorialDisplayTable;
   private MainMenuScreen menu;
   //Feel free to change this number.
   private final static int TUTORIAL_COUNT = 10;

   private String[] tutorialNames;
   private String[] tutorialDescriptions;

   public TutorialScreen(Game game, MainMenuScreen menu) {
    super(game);
    this.menu = menu;
    genericViewport = new ScreenViewport(camera);
    genericViewport.apply();
    stage = new Stage(genericViewport, batch);
    camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
    skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
    root.setFillParent(true);

    tutorials = new ArrayList<Tutorial>();
    drawer = new ShapeRenderer();
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
        for (int i = 0; i < TUTORIAL_COUNT; i++) {
            tutorialNames[i] = "placeholder";
        }
   }
   private void createTutorialDescriptions() {
        //TODO Change later, currently placeholder
        for (int i = 0; i < TUTORIAL_COUNT; i++) {
            tutorialDescriptions[i] = "placeholder";
        }
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
                        //Currently bugged, god ShapeRenderer is pure cancer.
                        drawer.setProjectionMatrix(camera.combined);
                        drawer.begin(ShapeType.Line);
                        drawer.setColor(Color.RED);
                        drawer.line(tutorial.getTutorialButton().getX() + tutorial.getTutorialButton().getWidth(), tutorial.getTutorialButton().getY(), tutorial.getTutorialButton().getX() + tutorial.getTutorialButton().getWidth(), tutorial.getTutorialButton().getY() + tutorial.getTutorialButton().getHeight());
                        drawer.line(tutorial.getTutorialButton().getX() + tutorial.getTutorialButton().getWidth(), tutorial.getTutorialButton().getY(), (float) (tutorial.getTutorialButton().getX() +  1.5 * tutorial.getTutorialButton().getWidth()), (float) (tutorial.getTutorialButton().getY() + 1.5 * tutorial.getTutorialButton().getHeight()));
                        drawer.line(tutorial.getTutorialButton().getX() + tutorial.getTutorialButton().getWidth(), tutorial.getTutorialButton().getY() + tutorial.getTutorialButton().getHeight(), (float) (tutorial.getTutorialButton().getX() +  1.5 * tutorial.getTutorialButton().getWidth()), (float) (tutorial.getTutorialButton().getY() + 0.5 * tutorial.getTutorialButton().getHeight()));
                        drawer.end();
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
       drawer.dispose();
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
   public void resize(int width, int height) {
       super.resize(width, height);
   }
}
