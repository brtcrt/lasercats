package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private ArrayList<GameObject> gameObjects;
	private Player cat;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		Player cat = new Player(32, 32, 32, 32);
		gameObjects = new ArrayList<>();
		gameObjects.add(cat);
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

	}

	
	@Override
	public void dispose () {
		for (GameObject object : gameObjects)
		{
			object.dispose();
		}
		batch.dispose();
	}
}
