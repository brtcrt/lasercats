package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PlayerNonMain extends Player {

    public PlayerNonMain (float x, float y, float width, float height, boolean isMainPlayer) {
        super(x, y, width, height, isMainPlayer);
    }

    @Override
    public void process() {
        super.process();
//        velocity.nor();
//        move();
//
//        // Animation
//        stateTime += Gdx.graphics.getDeltaTime();
//
//        if (is_walking()) {
//            currentAnimation = walkAnimation;
//        } else {
//            currentAnimation = idleAnimation;
//        }
//        sprite = new Sprite(currentAnimation.getKeyFrame(stateTime, true));
//
//        if(direction.x > 0) {
//            sprite.flip(true, false);
//        }
    }
}
