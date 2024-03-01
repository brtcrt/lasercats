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


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ArrayList<GameObject> gameObjects;
	private Client client;

	// test JSON data delete later ~brtcrt
	private JSONObject testJSON;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		Player cat = new Player(32, 32, 32, 32);
		Player otherCat = new Player(-300, -300, 32, 32); // initially out of screen
		gameObjects = new ArrayList<>();
		gameObjects.add(cat);
		gameObjects.add(otherCat);
		client = new Client(otherCat);
		testJSON = new JSONObject();
		try {
			testJSON.put("x", 100f);
			testJSON.put("y", 100f);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}

	@Override
	public void render () {
		for (GameObject object : gameObjects)
		{
			object.process();
		}

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

		client.sendUpdate(this.testJSON);

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
