package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player extends Empty implements  GameObject{
    private Texture playerTexture;
    private Texture playerAnimations;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;


    private float stateTime;

    public Vector2 velocity;

    public Vector2 direction;

    public boolean walking;

    private float walkSpeed;


    public Player (float x, float y, float width, float height, Texture playerTexture, Texture playerAnimations) {
        super(x, y, width, height);
        this.playerTexture = playerTexture;
        this.playerAnimations = playerAnimations;
        this.playerTexture = playerTexture;
        TextureRegion[][] tmp = TextureRegion.split(playerAnimations, (int)width, (int)height);
		TextureRegion[] idleFrames = new TextureRegion[2];
		TextureRegion[] walkFrames = new TextureRegion[2];
        
        walkFrames[0] = tmp[1][0];
		walkFrames[1] = tmp[1][1];

		idleFrames[0] = tmp[0][0];
		idleFrames[1] = tmp[0][1];

		this.idleAnimation = new Animation<TextureRegion>(0.14f, idleFrames);
		this.walkAnimation = new Animation<TextureRegion>(0.14f, walkFrames);

        this.stateTime = 0f;

        this.velocity = new Vector2();
        this.direction = new Vector2();

        this.walkSpeed = 150f;
    }

    public void process()
    {
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
    }

    public void render()
    {
        
    }

    public Sprite getSprite() {
        
		this.stateTime += Gdx.graphics.getDeltaTime();
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
