package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;


public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private Rectangle cat;
	private OrthographicCamera camera;
	private Texture whiteCat;
	private Animation<TextureRegion> idleCat;
	private Animation<TextureRegion> walkCat;
	private float stateTime;
	private boolean walking;
	private int direction;
	boolean changedDirection;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("1.png"));
		whiteCat =  new Texture(Gdx.files.internal("WhiteCat.png"));
		TextureRegion[][] tmp = TextureRegion.split(whiteCat, 32, 32);
		TextureRegion[] idleFrames = new TextureRegion[2];
		TextureRegion[] walkFrames = new TextureRegion[2];
		walkFrames[0] = tmp[1][0];
		walkFrames[1] = tmp[1][1];

		idleFrames[0] = tmp[0][0];
		idleFrames[1] = tmp[0][1];

		idleCat = new Animation<TextureRegion>(0.14f, idleFrames);
		walkCat = new Animation<TextureRegion>(0.14f, walkFrames);

		cat = new Rectangle();
		cat.x = 32;
		cat.y = 32;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 256, 256);

		stateTime = 0f;
		walking = false;
		direction = -1;
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();


		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			cat.x += 50 * Gdx.graphics.getDeltaTime();
			direction = 1;
			walking = true;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			cat.x -= 50 * Gdx.graphics.getDeltaTime();
			direction = -1;
			walking = true;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			cat.y += 50 * Gdx.graphics.getDeltaTime();
			walking = true;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			cat.y -= 50 * Gdx.graphics.getDeltaTime();
			walking = true;
		}


		stateTime += Gdx.graphics.getDeltaTime();
		TextureRegion currFrame;
		if (walking) {
			currFrame = walkCat.getKeyFrame(stateTime, true);
			
		} else {
			currFrame = idleCat.getKeyFrame(stateTime, true);
		}

		Sprite catSprite = new Sprite(currFrame);
		if(direction == 1) {
			catSprite.flip(true, false);
			changedDirection = false;
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(catSprite, cat.x, cat.y);
		batch.end();

		walking = false;

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		whiteCat.dispose();
	}
}
