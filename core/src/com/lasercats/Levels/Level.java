package com.lasercats.Levels;

import java.util.ArrayList;

import com.lasercats.GameObjects.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LaserCatsScreen;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;
import com.lasercats.Tiles.Tile;

public class Level extends LaserCatsScreen {

    protected ArrayList<GameObject> gameObjects;
    protected ArrayList<PhysicsObject> physicsObjects;
	private ArrayList<Tile> tiles;
	private ArrayList<GameObject> renderQueue;
	protected Client client;
	private JSONObject dataToServer;
	protected int reflectable;
	protected Gate exitGate;
	protected MainMenuScreen menu;

    public Level(Game game, Client client, MainMenuScreen menuScreen) {
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
		menu = menuScreen;
        LevelEditor.fillTiles(tiles);
		exitGate = null;
		client.inGame = true;
    }
    
    @Override
    public void createActors() {}
    @Override
    public void positionActors() {}
    @Override
    public void createTextures() {}
    @Override
    public void setListeners() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
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
			// identifiers.add(object.getIdentifiers());
		}

		for (int i = 0; i < 3; i++) {
			identifiers.add(gameObjects.get(i).getIdentifiers());
		}

		createDataJSON(identifiers);
		client.sendUpdate(dataToServer);

		// Ok wtf this might be the single worst piece of code I have ever written. jk I fucked up harder before. ~brtcrt
		camera.position.x = gameObjects.get(0).getX();
		camera.position.y = gameObjects.get(0).getY();
		if (gameObjects.get(0).getX() + ((Empty)gameObjects.get(0)).width > 1280) {
			gameObjects.get(0).setX(1280 - ((Empty)gameObjects.get(0)).width);
		}
		else if (gameObjects.get(0).getX() < -1280) {
			gameObjects.get(0).setX(-1280);
		}
		if (gameObjects.get(0).getY() + ((Empty)gameObjects.get(0)).height > 1280) {
			gameObjects.get(0).setY(1280- ((Empty)gameObjects.get(0)).height);
		}
		else if (gameObjects.get(0).getY() < -1280) {
			gameObjects.get(0).setY(-1280);
		}
		if (camera.position.x + camera.viewportWidth / 2 > 1280) {
            camera.position.x = 1280 - camera.viewportWidth / 2;
        }
        else if (camera.position.x - camera.viewportWidth / 2 < -1280) {
            camera.position.x = -1280 + camera.viewportWidth / 2;
        }
        if (camera.position.y + camera.viewportHeight / 2 > 1280) {
            camera.position.y = 1280 - camera.viewportHeight / 2;
        }
        else if (camera.position.y - camera.viewportHeight / 2 < -1280) {
            camera.position.y = -1280 + camera.viewportHeight / 2;
        }
		camera.update();

		batch.begin();

		renderQueue = new ArrayList<GameObject>(gameObjects);
		LevelEditor.ySort(renderQueue);

		for (Tile tile : tiles)
		{
			tile.render(batch);
		}

		for (GameObject o : renderQueue) {
			o.render(batch);
		}
		displayLevelEnding();
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
    protected void calculatePhysics () {
		// this is a dumb solution in O(n^2) but should be fine in our case.
		for (int i = 0; i < physicsObjects.size(); i++) {
			PhysicsObject o = physicsObjects.get(i);
			ArrayList<PhysicsObject> passedObjects = new ArrayList<PhysicsObject>(physicsObjects);
			passedObjects.remove(i);
			o.calculatePhysics(passedObjects);
		}
	}
    private void createDataJSON(ArrayList<JSONObject> identifiers) {
		try {
			dataToServer.put("gameObjects", identifiers);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}

	public void setPlayerStarts() {
		Player p1 = (Player) gameObjects.get(0);
		Player p2 = (Player) gameObjects.get(1);
		for (GameObject o : gameObjects) {
			if (o instanceof Gate) {
				Gate g = (Gate) o;
				if (g.getIsLaserCatEntranceGate()) {
					g.activate();
					g.process();
					if (reflectable == 0) {
						p2.x = g.x;
						p2.y = g.y;
					} else {
						p1.x = g.x;
						p1.y = g.y;
					}
				}
				if (g.getIsReflectiveCatEntranceGate()) {
					g.activate();
					g.process();
					if (reflectable == 1) {
						p2.x = g.x;
						p2.y = g.y;
					} else {
						p1.x = g.x;
						p1.y = g.y;
					}
				}
			}
		}
	}
	protected boolean isGameOver() {
		if (exitGate.contains(gameObjects.get(0).getX(), gameObjects.get(0).getY()) && exitGate.contains(gameObjects.get(1).getX(), gameObjects.get(1).getY()) && exitGate.isActive()) {
			return true;
		}
		return false;
	}
	protected void displayLevelEnding() {}
	protected Gate findExitGate() {
		for (GameObject object : gameObjects) {
			if (object instanceof Gate && ((Gate)object).getIsExitGate()) {
				return (Gate) object;
			}
		}
		return null;
	}
}
