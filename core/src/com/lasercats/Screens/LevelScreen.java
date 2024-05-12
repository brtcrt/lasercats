package com.lasercats.Screens;

import java.util.ArrayList;

import com.lasercats.GameObjects.*;
import com.lasercats.Tiles.FloorTile;
import com.lasercats.Tiles.Tile;
import com.lasercats.GameObjects.Laser;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.Box;
import com.lasercats.GameObjects.GameObject;

public class LevelScreen extends LaserCatsScreen {

    private ArrayList<GameObject> gameObjects;
    private ArrayList<PhysicsObject> physicsObjects;
	private ArrayList<Tile> tiles;
	private ArrayList<GameObject> renderQueue;
	private Client client;
	private JSONObject dataToServer;
	private GameObject[][] gameObjectMatrix;

    public LevelScreen(Game game, Client client) {
        super(game);
        this.client = client;
        this.genericViewport = new ExtendViewport(1024, 720, camera);
        this.genericViewport.apply();
		client.viewport = genericViewport;
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.root.setFillParent(true);
        gameObjects = client.getGameObjects();
		// OK this is incredibly retarded. I'm actually going to kill myself. ~brtcrt
        physicsObjects = client.physicsObjects;
		String creatorID = client.getRoom().getPlayerIDs()[0];
		Player lasercat;
		int reflectable;
		if(this.client.getID().equals(creatorID)){
			lasercat = (Player) gameObjects.get(0);
			reflectable = 1;
		}
		else{
			lasercat = (Player) gameObjects.get(1);
			reflectable = 0;
		}
		((Player) gameObjects.get(reflectable)).setIsReflective(true);
		Laser cl = new CatLaser(lasercat, genericViewport, physicsObjects);
		gameObjects.add(cl);
		// Create the empty tiles ArrayList here and fill it up later
		tiles = new ArrayList<Tile>();
        renderQueue = new ArrayList<GameObject>(gameObjects);
		dataToServer = new JSONObject();

		createTestLevel();
		client.inGame = true;
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
	public void createTextures() {}
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
		final int startY = 640;

		gameObjectMatrix = generateRectangleWall(startX, startY, 21, 21);
		GameObject[][] glassMatrix = generateRectangleGlass(-264, -136, 3, 3);

		// Make a box for the pressure plate
		GameObject[][] smallerBox = generateRectangleWall(startX, startY, 6, 4);

		gameObjectMatrix = mergeMatrices(smallerBox, gameObjectMatrix);

		// Leave empty spots for the gates
		gameObjectMatrix[0][11] = null;
		gameObjectMatrix[0][12] = null;
		gameObjectMatrix[20][14] = null;
		gameObjectMatrix[20][15] = null;
		gameObjectMatrix[3][2] = null;
		gameObjectMatrix[3][3] = null;

		ArrayList<GameObject> linear = linearizeMatrix(gameObjectMatrix);
		ArrayList<GameObject> linearGlass = linearizeMatrix(glassMatrix);

		for (GameObject o : linear) {
			walls.add((Wall) o);
		}
		for (GameObject o : linearGlass) {
			gameObjects.add((Glass) o);
			physicsObjects.add((Glass) o);
		}

		gameObjects.addAll(walls);
		physicsObjects.addAll(walls);

	}

	/**
	 * Also for testing purposes. Remove later ~brtcrt
	 */
	public void createLevelElements() {
		ArrayList<PressurePlate> pps = new ArrayList<>();
		ArrayList<Gate> gates = new ArrayList<>();
		ArrayList<Mirror> mirrors = new ArrayList<>();
		ArrayList<LaserTarget> laserTargets = new ArrayList<>();
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
		LaserTarget t = new LaserTarget(-200, -200, 64, 64, g);
		laserTargets.add(t);
		gates.add(g);
		pps.add(p);

		mirrors.add(new Mirror(150, 50, 64, 128));

		gameObjects.addAll(gates);
		gameObjects.addAll(pps);
		gameObjects.addAll(mirrors);
		gameObjects.addAll(laserTargets);
		physicsObjects.addAll(gates);
		physicsObjects.addAll(pps);
		physicsObjects.addAll(mirrors);
		physicsObjects.addAll(laserTargets);
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
			if(o1 instanceof PressurePlate){return -1;}
			if(o1 instanceof Glass) return -1;
			// if(o1 instanceof CatLaser)return -1;
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

	/**
	 *
	 * @param x top left corner x
	 * @param y top left corner y
	 * @param w width of rectangle
	 * @param h height of rectangle
	 * @return Matrix of walls and null objects
	 */
	public static GameObject[][] generateRectangleWall(float x, float y, int w, int h) {
		GameObject[][] matrix = new GameObject[h][w];

		final float wallWidth = 64;
		final float wallHeight = 64;

		// place the corner pieces
		matrix[0][0] = new Wall(x, y, wallWidth, wallHeight, 1);
		matrix[0][w - 1] = new Wall(x + wallWidth * (w - 1), y, wallWidth, wallHeight, 3);
		matrix[h - 1][0] = new Wall(x, y - wallHeight * (h - 1), wallWidth, wallHeight, 6);
		matrix[h - 1][w - 1] = new Wall(x + wallWidth * (w - 1), y - wallHeight * (h - 1), wallWidth, wallHeight, 8);


		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (matrix[i][j] == null) {
					if (i == 0) {
						matrix[i][j] = new Wall(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight, 2);
					}
					if (i == h - 1) {
						matrix[i][j] = new Wall(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight, 7);
					}
					if (j == 0) {
						matrix[i][j] = new Wall(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight, 4);
					}
					if (j == w - 1) {
						matrix[i][j] = new Wall(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight, 5);
					}
				}
			}
		}
		return matrix;
	}
	/**
	 *
	 * @param x top left corner x
	 * @param y top left corner y
	 * @param w width of rectangle
	 * @param h height of rectangle
	 * @return Matrix of walls and null objects
	 */
	private GameObject[][] generateRectangleGlass(float x, float y, int w, int h) {
		GameObject[][] matrix = new GameObject[h][w];

		final float wallWidth = 64;
		final float wallHeight = 64;

		// place the corner pieces
		matrix[0][0] = new Glass(x, y, wallWidth, wallHeight);
		matrix[0][w - 1] = new Glass(x + wallWidth * (w - 1), y, wallWidth, wallHeight);
		matrix[h - 1][0] = new Glass(x, y - wallHeight * (h - 1), wallWidth, wallHeight);
		matrix[h - 1][w - 1] = new Glass(x + wallWidth * (w - 1), y - wallHeight * (h - 1), wallWidth, wallHeight);


		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (matrix[i][j] == null) {
					if (i == 0) {
						matrix[i][j] = new Glass(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight);
					}
					if (i == h - 1) {
						matrix[i][j] = new Glass(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight);
					}
					if (j == 0) {
						matrix[i][j] = new Glass(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight);
					}
					if (j == w - 1) {
						matrix[i][j] = new Glass(x + j * wallWidth, y - i * wallHeight, wallWidth,wallHeight);
					}
				}
			}
		}
		return matrix;
	}

	public static GameObject[][] mergeMatrices(GameObject[][] a, GameObject[][] b) {
		// if two objects collide, the objects of a will be replaced
		GameObject[][] matrix = new GameObject[Math.max(a.length, b.length)][Math.max(a[0].length, b[0].length)];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] != null) {
					matrix[i][j] = a[i][j];
				}
			}
		}
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				if (b[i][j] != null) {
					matrix[i][j] = b[i][j];
				}
			}
		}
		return matrix;
	}

	public static ArrayList<GameObject> linearizeMatrix(GameObject[][] m) {
		ArrayList<GameObject> linear = new ArrayList<GameObject>();
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				if (m[i][j] != null) {
					linear.add(m[i][j]);
				}
			}
		}
		return linear;
	}
}
