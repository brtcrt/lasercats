package com.lasercats.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lasercats.GameObjects.*;
import com.lasercats.Tiles.FloorTile;
import com.lasercats.Tiles.Tile;
import com.badlogic.gdx.Game;
import java.util.ArrayList;

public class LevelEditor extends LaserCatsScreen{

    private ArrayList<GameObject> gameObjects;
    private ArrayList<PhysicsObject> physicsObjects;
    private ArrayList<Tile> tiles;
    private ArrayList<GameObject> renderQueue;

    //private GameObject[][] grid;
    //private GameObject[][] floatingGrid;
    private GameObject holding;

    //private int gridStart;
    private int tileSize = 64;

    private Table buttonTable;

    private TextButton goBackButton;
    private TextButton saveButton;
    private TextButton importButton;

    private MainMenuScreen menuScreen;

    private ExtendViewport levelViewport;
    private ScreenViewport UIViewport;

    private Table objectsTable;

    private TextButton objectTab1, objectTab2;
    private TextButton wallButtonOne;
    private TextButton wallButtonTwo;
    private TextButton wallButtonThree;
    private TextButton wallButtonFour;
    private TextButton wallButtonFive;
    private TextButton wallButtonSix;
    private TextButton gateButton;
    private TextButton laserTargetButton;
    private TextButton pressurePlateButton;
    private TextButton mirrorButton;
    private TextButton glassButton;
    private TextButton boxButton;
    private Pixmap bgPixmap;

    private TextButton entranceGateOneButton;
    private ChangeListener entranceGateOneButtonListener;
    private TextButton entranceGateTwoButton;
    private ChangeListener entranceGateTwoButtonListener;
    private TextButton exitGateButton;
    private ChangeListener exitGateButtonListener;

    private int[] controlScheme = Player.controlScheme;
    private int speed = 10;
    private Vector2 velocity = new Vector2();
    private Vector3 position;
    private Vector2 mousePoint;

    private VerticalGroup requiredButtonGroup;
    private VerticalGroup otherButtonGroup;

    private InputMultiplexer multiplexer;
    private ActivatableInputHandler detectorProcessor;
    private ShapeRenderer drawer;

    public LevelEditor(Game game, MainMenuScreen menuScreen)
    {
        super(game);
        levelViewport = new ExtendViewport(1024, 720, camera);
        camera.setToOrtho(false, levelViewport.getScreenWidth(), levelViewport.getScreenHeight());

        UIViewport = new ScreenViewport();
        stage = new Stage(levelViewport, batch);
        drawer = new ShapeRenderer();

        stage.setViewport(UIViewport);
        detectorProcessor = new ActivatableInputHandler();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);

//        genericViewport = new ExtendViewport(1024, 720, camera);
//        genericViewport.apply();
        gameObjects = new ArrayList<>();
        tiles = new ArrayList<Tile>();
        fillTiles();
        root.setFillParent(true);

        this.menuScreen = menuScreen;
        physicsObjects = new ArrayList<PhysicsObject>();
        //grid = new GameObject[1000][1000];
        //grid = LevelScreen.mergeMatrices(grid,LevelScreen.generateRectangleWall(0,0,10,10) );
        //gameObjects.addAll(LevelScreen.linearizeMatrix(grid));

//        gameObjects.addAll(LevelScreen.linearizeMatrix(LevelScreen.generateRectangleWall(0,0,10,10)));

        createActors();
        positionActors();
        setListeners();
    }
    @Override
    public void resize(int width, int height)
    {
        //TODO currently when you resize the window the positioning mechanism of objects breaks.
        //Other things break as well such as lines between detectors and activators, deletion hitboxes etc.
        levelViewport.update(width, height, false);
        UIViewport.update(width, height, true);
        root.setHeight(height);
        root.setWidth(width);
    }
    @Override
    public void render(float delta) {

        levelViewport.apply();
        Gdx.input.setInputProcessor(multiplexer);

        velocity.x = 0;
        velocity.y = 0;
        if (Gdx.input.isKeyPressed(controlScheme[2])) {
            velocity.x = 1;
        } if (Gdx.input.isKeyPressed(controlScheme[3])) {
            velocity.x = -1;
        } if (Gdx.input.isKeyPressed(controlScheme[0])) {
            velocity.y = 1;
        } if (Gdx.input.isKeyPressed(controlScheme[1])) {
            velocity.y = -1;
        }
        velocity.nor();
        camera.translate(velocity.x * speed, velocity.y * speed, 0);
        
        batch.setProjectionMatrix(levelViewport.getCamera().combined);
        batch.begin();

        renderQueue = new ArrayList<GameObject>(gameObjects);
        ySort();

        for (Tile tile : tiles)
        {
            tile.render(batch);
        }

        for (GameObject o : renderQueue) {
            o.render(batch);
        }
        position = camera.project(new Vector3(camera.direction.x * speed, camera.direction.y * speed, 0));
        mousePoint = new Vector2(Gdx.input.getX() - position.x, levelViewport.getScreenHeight() - Gdx.input.getY() - position.y);
        if (holding != null) {
            holding.setX((int) ((Gdx.input.getX() - position.x)/tileSize) * tileSize);
            holding.setY((int) ((levelViewport.getScreenHeight() - Gdx.input.getY() - position.y)/tileSize) * tileSize);
            holding.render(batch);
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                boolean collides = false;
                for (GameObject object : gameObjects) {
                    if (((Empty)object).overlaps(((Empty)holding).getCollider())) {
                        collides = true;
                    }
                }
                if (!collides) {
                    gameObjects.add(holding);
                    if (holding instanceof PhysicsObject) {
                        physicsObjects.add((PhysicsObject) holding);
                    }
                    if (holding instanceof Gate) {
                        if (((Gate)holding).getIsLaserCatEntranceGate()) {
                            entranceGateOneButton.removeListener(entranceGateOneButtonListener);
                        }
                        else if (((Gate)holding).getIsReflectiveCatEntranceGate()) {
                            entranceGateTwoButton.removeListener(entranceGateTwoButtonListener);
                        }
                        else if (((Gate)holding).getIsExitGate()) {
                            exitGateButton.removeListener(exitGateButtonListener);
                        }
                    }

                        holding = null;
                }
            }
        }
        else {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                for (GameObject object : gameObjects) {
                    if (((Empty)object).contains(mousePoint)) {
                        holding = object;
                        gameObjects.remove(object);
                        break;
                    }
                }
            }
            else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                for (GameObject object : gameObjects) {
                    if (((Empty)object).contains(mousePoint) && object instanceof Detector) {
                        Detector detectorObject = (Detector) object;
                        detectorProcessor.setDetector(detectorObject);
                        multiplexer.removeProcessor(stage);
                        multiplexer.addProcessor(detectorProcessor);
                        break;
                    }
                } 
            }
        }
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject object = gameObjects.get(i);
                if (((Empty)object).contains(mousePoint)) {
                    gameObjects.remove(object);
                    if (object instanceof Gate) {
                        if (((Gate)object).getIsLaserCatEntranceGate()) {
                            entranceGateOneButton.addListener(entranceGateOneButtonListener);
                        }
                        else if (((Gate)object).getIsReflectiveCatEntranceGate()) {
                            entranceGateTwoButton.addListener(entranceGateTwoButtonListener);
                        }
                        else if (((Gate)object).getIsExitGate()) {
                            exitGateButton.addListener(exitGateButtonListener);
                        }
                    }
                    if (object instanceof Activatable) {
                        for (GameObject object2 : gameObjects) {
                            if (object2 instanceof Detector) {
                                //Since addActivatable method only adds an activatable that is not in the list of activatables of a detector
                                //These method calls ensure that we remove this object no matter if it is in the list of activatables
                                ((Detector)object2).addActivatable((Activatable) object);
                                ((Detector)object2).getActivatables().remove((Activatable) object);
                            }
                        }
                    }
                    break;
                }
            }
        }

//        UIViewport.apply();S
        stage.act(delta);
        stage.draw();
        drawer.begin(ShapeType.Line);
        drawer.setColor(Color.RED);
        //O(n^2) solution but should not be an issue in most cases.
        for (GameObject object : gameObjects) {
            if (object instanceof Detector) {
                for (Activatable activatable : ((Detector)object).getActivatables()) {
                    drawer.line(object.getX() + ((Empty)object).getWidth() / 2 + position.x, object.getY() + ((Empty)object).getHeight() / 2 + position.y, ((Empty)activatable).getX() + ((Empty)activatable).getWidth() / 2 + position.x, ((Empty)activatable).getY() + ((Empty)activatable).getHeight() / 2 + + position.y);
                }
            }
        }
        drawer.end();
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        bgPixmap.dispose();
        for (GameObject object : gameObjects) {
            object.destroy();
        }
        drawer.dispose();
        for (Tile tile : tiles) {
            tile.destroy();
        }
    }
    @Override
    public void createTextures() {}
    @Override
    public void createActors() {
        buttonTable = new Table();

        goBackButton = new TextButton("Back", skin, "dark");
        saveButton = new TextButton("Save", skin, "dark");
        importButton = new TextButton("Import", skin, "dark");

        objectsTable = new Table();
        // Color objects table
        bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(new Color(0.82745098039215686274509803921569f, 0.82745098039215686274509803921569f, 0.82745098039215686274509803921569f, 1));
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        objectsTable.setBackground(textureRegionDrawableBg);

        objectTab1 = new TextButton("Required", skin, "dark");
        objectTab2 = new TextButton("Other", skin, "dark");

        wallButtonOne = new TextButton("Wall Top", skin);
        wallButtonTwo = new TextButton("Wall Bottom", skin);
        wallButtonThree = new TextButton("Wall Right", skin);
        wallButtonFour = new TextButton("Wall Left", skin);
        wallButtonFive = new TextButton("Wall Right Corner", skin);
        wallButtonSix = new TextButton("Wall Left Corner", skin);
        mirrorButton = new TextButton("Mirror", skin);
        laserTargetButton = new TextButton("Laser Target", skin);
        pressurePlateButton = new TextButton("Pressure Plate", skin);
        gateButton = new TextButton("Gate", skin);
        glassButton = new TextButton("Glass", skin);
        boxButton = new TextButton("Box", skin);

        entranceGateOneButton = new TextButton("Entrance One", skin);
        entranceGateTwoButton = new TextButton("Entrance Two", skin);
        exitGateButton = new TextButton("Exit", skin);

        requiredButtonGroup = new VerticalGroup();
        otherButtonGroup = new VerticalGroup();
    }
    @Override
    public void positionActors() {
        otherButtonGroup.addActor(wallButtonOne);
        otherButtonGroup.addActor(wallButtonTwo);
        otherButtonGroup.addActor(wallButtonThree);
        otherButtonGroup.addActor(wallButtonFour);
        otherButtonGroup.addActor(wallButtonFive);
        otherButtonGroup.addActor(wallButtonSix);
        otherButtonGroup.addActor(mirrorButton);
        otherButtonGroup.addActor(laserTargetButton);
        otherButtonGroup.addActor(pressurePlateButton);
        otherButtonGroup.addActor(gateButton);
        otherButtonGroup.addActor(glassButton);
        otherButtonGroup.addActor(boxButton);

        requiredButtonGroup.addActor(entranceGateOneButton);
        requiredButtonGroup.addActor(entranceGateTwoButton);
        requiredButtonGroup.addActor(exitGateButton);

        buttonTable.add(goBackButton);
        buttonTable.row();
        buttonTable.add(saveButton);
        buttonTable.row();
        buttonTable.add(importButton);
        
        objectsTable.add(objectTab1).growX();
        objectsTable.add(objectTab2).growX();
        objectsTable.row();
        objectsTable.add(requiredButtonGroup).expandY().align(Align.top);
        objectsTable.add(otherButtonGroup).expandY().align(Align.top);

        root.add(buttonTable).align(Align.topLeft).expand();
        root.add(objectsTable).growY().expand().align(Align.topRight);

        stage.setRoot(root);
        //stage.setDebugAll(true);
    }
    @Override
    public void setListeners() {
        entranceGateOneButtonListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (entranceGateOneButton.isPressed()) {
                    GameObject gate = new Gate(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    ((Gate)gate).setAsLaserCatEntranceGate();
                    holding = gate;
                }
            };
        };
        entranceGateTwoButtonListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (entranceGateTwoButton.isPressed()) {
                    GameObject gate = new Gate(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    ((Gate)gate).setAsReflectiveCatEntranceGate();
                    holding = gate;
                }
            };
        };
        exitGateButtonListener = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (exitGateButton.isPressed()) {
                    GameObject gate = new Gate(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    ((Gate)gate).setAsExitGate();
                    holding = gate;
                }
            };
        };
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (goBackButton.isPressed()) {
                    game.setScreen(menuScreen);
                }
            }
        });
        wallButtonOne.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonOne.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 2);
                    holding = wall;
                }
            }
        });
        wallButtonTwo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonTwo.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 7);
                    holding = wall;
                }
            }
        });
        wallButtonThree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonThree.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 5);
                    holding = wall;
                }
            }
        });
        wallButtonFour.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonFour.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 4);
                    holding = wall;
                }
            }
        });
        wallButtonFive.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonFive.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 8);
                    holding = wall;
                }
            }
        });
        wallButtonSix.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonSix.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 6);
                    holding = wall;
                }
            }
        });
        mirrorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mirrorButton.isPressed()) {
                    GameObject mirror = new Mirror(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    holding = mirror;
                }
            }
        });
        laserTargetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (laserTargetButton.isPressed()) {
                    GameObject laserTarget = new LaserTarget(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    holding = laserTarget;
                }
            }
        });
        pressurePlateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (pressurePlateButton.isPressed()) {
                    //Same here.
                    GameObject pressurePlate = new PressurePlate(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, new ArrayList<Activatable>());
                    holding = pressurePlate;
                }
            }
        });
        glassButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (glassButton.isPressed()) {
                    GameObject glass = new Glass(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    holding = glass;
                }
            }
        });
        gateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (gateButton.isPressed()) {
                    GameObject gate = new Gate(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize);
                    holding = gate;
                }
            }
        });
        boxButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (boxButton.isPressed()) {
                    GameObject box = new Box(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize);
                    holding = box;
                }
            }
        });
        boxButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (boxButton.isPressed()) {
                    GameObject box = new Box(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize);
                    holding = box;
                }
            }
        });
        entranceGateOneButton.addListener(entranceGateOneButtonListener);
        entranceGateTwoButton.addListener(entranceGateTwoButtonListener);
        exitGateButton.addListener(exitGateButtonListener);
    }

    private void ySort() {
        renderQueue.sort((o1, o2) -> {
            if(o1 instanceof PressurePlate){return -1;}
            if(o1 instanceof CatLaser)return -1;
            return -1 * Float.compare(o1.getY(), o2.getY());
        });
    }

    public void fillTiles() {
        // For testing purposes I'll just fill a portion of the level with FloorTiles here ~brtcrt
        final int startX = -1280;
        final int startY = -1280;
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                tiles.add(new FloorTile(startX + 64 * i, startY + 64 * j, 64, 64, ((int)Math.round(Math.random() * 11)) + 1));
            }
        }
    }
    private class ActivatableInputHandler extends InputAdapter {
        private Detector detectorObject; 
        public ActivatableInputHandler() {
            this.detectorObject = null;
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (button == Buttons.RIGHT) {
                mousePoint.set(Gdx.input.getX() - position.x, levelViewport.getScreenHeight() - Gdx.input.getY() - position.y);
                for (GameObject object : gameObjects) {
                    if (((Empty)object).contains(mousePoint) && (object instanceof Activatable)) {
                        if (!(object instanceof Gate && ((Gate)object).getIsExitGate() || ((Gate)object).getIsLaserCatEntranceGate() || ((Gate)object).getIsReflectiveCatEntranceGate())) {
                            detectorObject.addActivatable((Activatable) object);
                            multiplexer.removeProcessor(this);
                            multiplexer.addProcessor(stage);
                            return true;
                        }
                    }
                }
            }
            multiplexer.removeProcessor(this);
            multiplexer.addProcessor(stage);
            return false;
        }
        public void setDetector(Detector detector) {
            detectorObject = detector;
        }
    }
}
