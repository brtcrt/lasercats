package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private Rectangle cat;
	private OrthographicCamera camera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("1.png");
		cat = new Rectangle();
		cat.x = 32;
		cat.y = 32;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 256, 256);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, cat.x, cat.y);
		batch.end();

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			cat.x += 50 * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			cat.x -= 50 * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			cat.y += 50 * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			cat.y -= 50 * Gdx.graphics.getDeltaTime();
		}



	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
