package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lasercats.GameObjects.Player;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Player cat;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		this.cat = new Player(32, 32, 32, 32);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();
		cat.process();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// TODO make the height and width dynamic as well
//		batch.draw(catSprite, cat.x, cat.y, 128, 128);
		cat.render(batch);
		batch.end();

	}

	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
