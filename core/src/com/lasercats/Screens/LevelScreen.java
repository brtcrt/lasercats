package com.lasercats.Screens;

import java.util.ArrayList;

import com.lasercats.GameObjects.*;
import com.lasercats.Tiles.FloorTile;
import com.lasercats.Tiles.Tile;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lasercats.Client.Client;

public class LevelScreen extends LaserCatsScreen {

    private ArrayList<GameObject> gameObjects;
    private ArrayList<PhysicsObject> physicsObjects;
	private ArrayList<Tile> tiles;
	private ArrayList<GameObject> renderQueue;
	private Client client;
	private JSONObject dataToServer;

    public LevelScreen(Game game, Client client) {
        super(game);
        this.client = client;
        this.genericViewport = new ExtendViewport(1024, 720, camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        gameObjects = client.getGameObjects();
		// OK this is incredibly retarded. I'm actually going to kill myself. ~brtcrt
        physicsObjects = new ArrayList<PhysicsObject>();
		physicsObjects.add((Player)gameObjects.get(0));
		physicsObjects.add((Player)gameObjects.get(1));
		// Create the empty tiles ArrayList here and fill it up later
		tiles = new ArrayList<Tile>();
        renderQueue = new ArrayList<GameObject>(gameObjects);
		dataToServer = new JSONObject();

		createTestLevel();
    }
    @Override
    public void render(float delta) {
		
        Gdx.input.setInputProcessor(stage);
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();

        ArrayList<JSONObject> identifiers = new ArrayList<JSONObject>();
		calculatePhysics();
		for (GameObject object : gameObjects)
		{
			object.process();
			identifiers.add(object.getIdentifiers());
		}

		createDataJSON(identifiers);
		client.sendUpdate(dataToServer);

		ScreenUtils.clear(1, 1, 1, 1);
		// Ok wtf this might be the single worst piece of code I have ever written. jk I fucked up harder before. ~brtcrt
		camera.position.x = gameObjects.get(0).getX();
		camera.position.y = gameObjects.get(0).getY();
		camera.update();

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
		batch.end();
    }
    @Override
    public void dispose() {
        for (GameObject object : gameObjects)
		{
			object.destroy();
		}
        batch.dispose();
		stage.dispose();
		client.close();
    }
    @Override
    public void pause() {}
    @Override
    public void createActors() {}
    @Override
    public void setListeners() {}
    @Override
    public void positionActors() {}
    @Override
    public void resume() {}

	protected void calculatePhysics () {
		// this is a dumb solution in O(n^2) but should be fine in our case.
		for (int i = 0; i < physicsObjects.size(); i++) {
			PhysicsObject o = physicsObjects.get(i);
			ArrayList<PhysicsObject> passedObjects = new ArrayList<PhysicsObject>(physicsObjects);
			passedObjects.remove(i);
			o.calculatePhysics(passedObjects);
		}
	}

    /**
	 * For testing purposes. Remove later ~brtcrt
	 */
	private void createBoxes() {
		final int BOX_COUNT = 2;
		for (int i = 0; i < BOX_COUNT; i++) {
			Box b;
			boolean goodBox;
			do {
				goodBox = true;
				b = new Box(40 + (int)Math.round(Math.random() * 600), 40 + (int)Math.round(Math.random() * 600));
				for (PhysicsObject o : physicsObjects) {
					if (o.getCollider().overlaps(b)) {
						goodBox = false;
					}
				}
			} while (!goodBox);
			gameObjects.add(b);
			physicsObjects.add(b);
		}
	}
	/**
	 * For testing purposes. Remove later ~brtcrt
	 * Please help me.
	 */
	private void createWalls() {
		//By the way we really shouldn't position objects with manually entered coordinates, this works terribly with different aspect ratios. ~Doruk
		// Yeah I guess, but I don't really care atm ~brtcrt
		ArrayList<Wall> walls = new ArrayList<Wall>();
		final int startX = -640;
		final int startY = -640;
		walls.add(new Wall(startX, -startY, 64, 64, 1));
		walls.add(new Wall(-startX, -startY, 64, 64, 3));
		walls.add(new Wall(startX, startY, 64, 64, 6));
		walls.add(new Wall(-startX, startY, 64, 64, 8));
		for (int i = 1; i < 20; i++) {
			walls.add(new Wall(startX + i * 64, startY, 64,64, 7));
			walls.add(new Wall(startX + i * 64, -startY, 64,64, 2));
		}
		for (int i = 1; i < 20; i++) {
			walls.add(new Wall(startX, startY + i * 64, 64,64, 4));
			walls.add(new Wall(-startX , startY + i * 64, 64,64, 5));
		}
		// Make room for the door
		// honestly this should remove a wall from both the top and the bottom
		// but it doesn't for some reason??
		walls.remove(25);
		walls.remove(26);
		walls.remove(28);
		walls.remove(29);

		// Make a box for the pressure plate
		walls.add(new Wall(startX + 64, -startY - 196, 64, 64, 7));
		walls.add(new Wall(startX + 256, -startY - 196, 64, 64, 7));
		walls.add(new Wall(startX + 320, -startY - 64, 64, 64, 5));
		walls.add(new Wall(startX + 320, -startY - 128, 64, 64, 5));
		walls.add(new Wall(startX + 320, -startY - 196, 64, 64, 8));

		gameObjects.addAll(walls);
		physicsObjects.addAll(walls);

	}

	/**
	 * Also for testing purposes. Remove later ~brtcrt
	 */
	public void createLevelElements() {
		ArrayList<PressurePlate> pps = new ArrayList<>();
		ArrayList<Gate> gates = new ArrayList<>();
		Gate g = new Gate(64, 640, 128, 64);
		PressurePlate p = new PressurePlate(-500, 550, 64, 64, g);
		gates.add(g);
		pps.add(p);

		g = new Gate(256, -640, 128, 64);
		p = new PressurePlate(800, 800, 64, 64, g);
		gates.add(g);
		pps.add(p);

		g = new Gate(-640 + 128, 640 - 196, 128, 64);
		p = new PressurePlate(-500, -400, 64, 64, g);
		gates.add(g);
		pps.add(p);

		gameObjects.addAll(gates);
		gameObjects.addAll(pps);
		physicsObjects.addAll(gates);
		physicsObjects.addAll(pps);
		createBoxes();
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

	public void createTestLevel() {
		createWalls();
		createLevelElements();
		fillTiles();
	}
	private void ySort() {
		renderQueue.sort((o1, o2) -> {
			return -1 * Float.compare(o1.getY(), o2.getY());
		});
	}

	private void createDataJSON(ArrayList<JSONObject> identifiers) {
		try {
			dataToServer.put("gameObjects", identifiers);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}
}
