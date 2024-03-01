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
import com.lasercats.GameObjects.Player;
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
	private Player player;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("1.png"));
		whiteCat =  new Texture(Gdx.files.internal("WhiteCat.png"));

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
		this.player = new Player(32, 32, 32, 32, img, whiteCat);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		camera.update();


		// TODO This if chain is dumb change this ~brtcrt
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.move(Input.Keys.D);
		} if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.move(Input.Keys.A);
		} if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			player.move(Input.Keys.W);
		} if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			player.move(Input.Keys.S);
		} 
		Sprite catSprite = player.getSprite();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(catSprite, player.x, player.y, 128, 128);
		batch.end();

		player.move(-1);
	}

	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		whiteCat.dispose();
	}
}
