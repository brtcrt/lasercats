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
	private Texture catTexture;
	private OrthographicCamera camera;
	private Texture catAnimationSheet;
	private Player cat;

	@Override
	public void create () {
		batch = new SpriteBatch();
		catTexture = new Texture(Gdx.files.internal("Cat.png"));
		catAnimationSheet =  new Texture(Gdx.files.internal("CatAnimationSheet.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		this.cat = new Player(32, 32, 32, 32, catTexture, catAnimationSheet);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();


		// TODO This if chain is dumb change this ~brtcrt
		cat.process();
		Sprite catSprite = cat.getSprite();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// TODO make the height and width dynamic as well
		batch.draw(catSprite, cat.x, cat.y, 128, 128); 
		batch.end();

	}

	
	@Override
	public void dispose () {
		batch.dispose();
		catTexture.dispose();
		catAnimationSheet.dispose();
	}
}
