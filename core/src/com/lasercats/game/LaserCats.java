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

		// TextureRegion[][] tmp = TextureRegion.split(whiteCat, 32, 32);
		// TextureRegion[] idleFrames = new TextureRegion[2];
		// TextureRegion[] walkFrames = new TextureRegion[2];
		// walkFrames[0] = tmp[1][0];
		// walkFrames[1] = tmp[1][1];

		// idleFrames[0] = tmp[0][0];
		// idleFrames[1] = tmp[0][1];

		// idleCat = new Animation<TextureRegion>(0.14f, idleFrames);
		// walkCat = new Animation<TextureRegion>(0.14f, walkFrames);

		// cat = new Rectangle();
		// cat.x = 32;
		// cat.y = 32;
		// cat.width = 128;
		// cat.height = 128;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		// stateTime = 0f;
		// walking = false;
		// direction = -1;
		this.cat = new Player(32, 32, 32, 32, catTexture, catAnimationSheet);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();


		// 
		Vector2 moveVector = new Vector2();
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			// player.move(Input.Keys.D); 
			moveVector.add(1, 0);
		} if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			// player.move(Input.Keys.A);
			moveVector.add(-1, 0);
		} if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			// player.move(Input.Keys.W);
			moveVector.add(0, 1);
		} if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			// player.move(Input.Keys.S);
			moveVector.add(0, -1);
		} 

		cat.move(moveVector);

		Sprite catSprite = cat.getSprite();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
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
