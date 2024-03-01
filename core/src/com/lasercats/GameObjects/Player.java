package com.lasercats.GameObjects;

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


public class Player extends Empty {
    private Texture playerTexture;
    private Texture playerAnimations;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;


    private float stateTime;

    public int direction;

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

        this.direction = -1;

        this.walkSpeed = 100f;
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

        if(direction == 1) {
			playerSprite.flip(true, false);
		}        

        return playerSprite;

    }

    public void move(int key) {
        switch (key) {
            case Input.Keys.D:
                x += walkSpeed * Gdx.graphics.getDeltaTime();
                direction = 1;
                walking = true; 
                break;

            case Input.Keys.A:
                x -= walkSpeed * Gdx.graphics.getDeltaTime();
                direction = -1;
                walking = true; 
                break;

            case Input.Keys.W:
                y += walkSpeed * Gdx.graphics.getDeltaTime();
                walking = true; 
                break;

            case Input.Keys.S:
                y -= walkSpeed * Gdx.graphics.getDeltaTime();
                walking = true; 
                break;

            default:
                walking = false;
                break;
        }
    }

}
