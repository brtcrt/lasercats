package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends Empty implements  GameObject{

    private Sprite sprite;

    Texture animationSheet;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;

    private final float walkSpeed = 150f;
    private final float animationPeriod = 0.14f;
    private float stateTime;

    public Vector2 velocity;
    public Vector2 direction;
    public boolean walking;




    public Player (float x, float y, float width, float height) {
        super(x, y, width, height);
        animationSheet = new Texture(Gdx.files.internal("CatAnimationSheet.png"));

        TextureRegion[][] tmp = TextureRegion.split(animationSheet, (int) width, (int) height);
        TextureRegion[] idleFrames = new TextureRegion[2];
        TextureRegion[] walkFrames = new TextureRegion[2];

        walkFrames[0] = tmp[1][0];
        walkFrames[1] = tmp[1][1];

        idleFrames[0] = tmp[0][0];
        idleFrames[1] = tmp[0][1];

        idleAnimation = new Animation<TextureRegion>(animationPeriod, idleFrames);
        walkAnimation = new Animation<TextureRegion>(animationPeriod, walkFrames);

        velocity = new Vector2();
        direction = new Vector2();
        stateTime = 0;
    }

    public void process()
    {
        // Movement
        velocity.x = 0;
        velocity.y = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.x = 1;
            velocity.x = 1;
        } if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x = -1;
            velocity.x = -1;
        } if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y = 1;
        } if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -1;
        }
        velocity.nor();
        move();

        // Animation
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public void render(SpriteBatch batch)
    {
        sprite = getSprite();
        batch.draw(sprite, x, y, 128, 128);
    }

    public Sprite getSprite() {
        
		TextureRegion currFrame;
		if (walking) {
			currFrame = this.walkAnimation.getKeyFrame(stateTime, true);
			
		} else {
			currFrame = this.idleAnimation.getKeyFrame(stateTime, true);
		}

        Sprite playerSprite = new Sprite(currFrame);

        if(direction.x > 0) {
			playerSprite.flip(true, false);
		}        

        return playerSprite;

    }

    public void move()
    {
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
        walking = !velocity.isZero();
    }

    public void move(Vector2 v) {
        if (v.isZero()) {
            this.walking = false;
        } else {
            this.walking = true;
            this.x += v.x * this.walkSpeed;
            this.y += v.y * this.walkSpeed;
            this.direction.x = v.x < 0 ? -1 : 1;
            this.direction.y = v.y < 0 ? -1 : 1;
        }
    }

}
