package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ArrayList<GameObject> gameObjects;
	private Client client;

	// TODO test JSON data delete later ~brtcrt
	private Player cat; // TODO move this down to create() later probably ~brtcrt
	private JSONObject testJSON;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		Player cat = new Player(32, 32, 32, 32);
		gameObjects = new ArrayList<>();
		gameObjects.add(cat);
		gameObjects.add(otherCat);
		client = new Client(otherCat);
	}

	@Override
	public void render () {
		for (GameObject object : gameObjects)
		{
			object.process();
		}

		// TODO for testing, remove later ~brtcrt
		HashMap<String, HashMap<String, Float>> playerMap = new HashMap<String, HashMap<String, Float>>();
		HashMap<String, Float> velocityMap = new HashMap<String, Float>();
		velocityMap.put("x", this.cat.velocity.x);
		velocityMap.put("y", this.cat.velocity.y);
		HashMap<String, Float> directionMap = new HashMap<String, Float>();
		directionMap.put("x", this.cat.direction.x);
		directionMap.put("y", this.cat.direction.y);
		HashMap<String, Float> positionMap = new HashMap<String, Float>();
		positionMap.put("x", this.cat.x);
		positionMap.put("y", this.cat.y);
		playerMap.put("position", positionMap);
		playerMap.put("velocity", velocityMap);
		playerMap.put("direction", directionMap);
		this.testJSON = new JSONObject(playerMap);

		client.sendUpdate(this.testJSON);

		ScreenUtils.clear(1, 1, 1, 1);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (GameObject object : gameObjects)
		{
			object.render(batch);
		}
		// TODO make the height and width dynamic as well
		batch.end();

	}

	
	@Override
	public void dispose () {
		for (GameObject object : gameObjects)
		{
			object.destroy();
		}
		batch.dispose();
		client.close();
	}
}
