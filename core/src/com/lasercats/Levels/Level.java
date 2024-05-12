package com.lasercats.Levels;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.CatLaser;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Laser;
import com.lasercats.GameObjects.PhysicsObject;
import com.lasercats.GameObjects.Player;
import com.lasercats.Screens.LaserCatsScreen;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Tiles.Tile;

public class Level extends LaserCatsScreen {

    protected ArrayList<GameObject> gameObjects;
    protected ArrayList<PhysicsObject> physicsObjects;
	private ArrayList<Tile> tiles;
	private ArrayList<GameObject> renderQueue;
	private Client client;
	private JSONObject dataToServer;
	private int reflectable;

    public Level(Game game, Client client) {
        super(game);

        this.client = client;
        this.genericViewport = new ExtendViewport(1024, 720, camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        this.root.setFillParent(true);
        gameObjects = client.getGameObjects();
		// OK this is incredibly retarded. I'm actually going to kill myself. ~brtcrt
        physicsObjects = new ArrayList<PhysicsObject>();
		physicsObjects.add((Player)gameObjects.get(0));
		physicsObjects.add((Player)gameObjects.get(1));
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
        LevelEditor.fillTiles(tiles);
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
			identifiers.add(object.getIdentifiers());
		}

		createDataJSON(identifiers);
		client.sendUpdate(dataToServer);

		// Ok wtf this might be the single worst piece of code I have ever written. jk I fucked up harder before. ~brtcrt
		camera.position.x = gameObjects.get(0).getX();
		camera.position.y = gameObjects.get(0).getY();
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
}
