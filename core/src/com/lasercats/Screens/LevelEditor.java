package com.lasercats.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private GameObject[][] grid;
    private GameObject[][] floatingGrid;
    private GameObject holding;

    private int gridStart;
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
    private TextButton gateButton;
    private TextButton laserTargetButton;
    private TextButton pressurePlateButton;
    private TextButton mirrorButton;
    private TextButton glassButton;
    private TextButton boxButton;
    private Pixmap bgPixmap;

    private TextButton entranceGateOneButton;
    private TextButton entranceGateTwoButton;
    private TextButton exitGateButton;

    private int[] controlScheme = Player.controlScheme;
    private int speed = 10;
    private Vector2 velocity = new Vector2();
    private Vector2 position = new Vector2(100,100);

    private VerticalGroup requiredButtonGroup;
    private VerticalGroup otherButtonGroup;

    public LevelEditor(Game game, MainMenuScreen menuScreen)
    {
        super(game);
        levelViewport = new ExtendViewport(1024, 720, camera);
        camera.setToOrtho(false, levelViewport.getScreenWidth(), levelViewport.getScreenHeight());

        UIViewport = new ScreenViewport();
        stage = new Stage(levelViewport, batch);

        stage.setViewport(UIViewport);

//        genericViewport = new ExtendViewport(1024, 720, camera);
//        genericViewport.apply();
        gameObjects = new ArrayList<>();
        tiles = new ArrayList<Tile>();
        fillTiles();
        root.setFillParent(true);
        position = new Vector2();

        this.menuScreen = menuScreen;
        physicsObjects = new ArrayList<PhysicsObject>();
        grid = new GameObject[1000][1000];
        grid = LevelScreen.mergeMatrices(grid,LevelScreen.generateRectangleWall(0,0,10,10) );
        gameObjects.addAll(LevelScreen.linearizeMatrix(grid));

//        gameObjects.addAll(LevelScreen.linearizeMatrix(LevelScreen.generateRectangleWall(0,0,10,10)));

        createActors();
        positionActors();
        setListeners();
    }
    @Override
    public void resize(int width, int height)
    {
        levelViewport.update(width, height, false);
        UIViewport.update(width, height, true);
        root.setHeight(height);
        root.setWidth(width);
    }
    @Override
    public void render(float delta) {

        levelViewport.apply();
        Gdx.input.setInputProcessor(stage);

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
        for (GameObject[] objectList : grid) for (GameObject o : objectList)
        {
            if (o == null) continue;
            o.render(batch);
        }
        if (holding != null)
        {
            Vector3 position = camera.project(new Vector3(camera.direction.x * speed, camera.direction.y * speed, 0));
            holding.setX(Gdx.input.getX() - position.x);
            holding.setY(levelViewport.getScreenHeight() - Gdx.input.getY() - position.y);
            holding.render(batch);
        }
        if (holding != null && Gdx.input.isTouched()) {
            boolean collides = false;
            for (GameObject object : gameObjects) {
                if (((Empty)object).overlaps(((Empty)holding).getCollider())) {
                    collides = true;
                }
            }
            if (!collides) {
                addGameObject((int) holding.getX(), (int) holding.getY(), holding);
                if (holding instanceof PhysicsObject) {
                    physicsObjects.add((PhysicsObject) holding);
                }
                holding = null;
            }
        }
        batch.end();

//        UIViewport.apply();
        stage.act(delta);
        stage.draw();
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

    private void addWall(int i, int j)
    {
        addGameObject(i, j, new Wall(i * tileSize, i * tileSize,tileSize, tileSize, 2));
    }

    private void addGameObject(int i, int j, GameObject object)
    {
        grid[i][j] = object;
        gameObjects.add(object);
    }


    @Override
    public void positionActors() {
        otherButtonGroup.addActor(wallButtonOne);
        otherButtonGroup.addActor(wallButtonTwo);
        otherButtonGroup.addActor(wallButtonThree);
        otherButtonGroup.addActor(wallButtonFour);
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
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 4);
                    holding = wall;
                }
            }
        });
        wallButtonFour.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (wallButtonFour.isPressed()) {
                    GameObject wall = new Wall(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, 5);
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
                    //Initially laserTarget will have an empty arrayList of activatables.
                    //However, there is no way to do something like this from the constructor.
                    //Therefore, the texture of the target becomes the entire animation sheet.
                    //Applicable to every detector object.
                    GameObject laserTarget = new LaserTarget(Gdx.input.getX()/tileSize, Gdx.input.getY()/tileSize, tileSize, tileSize, new ArrayList<Activatable>());
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
}
