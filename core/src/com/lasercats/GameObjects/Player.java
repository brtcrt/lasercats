package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Scanner;

public class Player extends Empty implements  GameObject{

    static int count = 0;
    private  Texture animationSheet;
    private Sprite sprite;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> currentAnimation;

    private final float walkSpeed = 150f;
    private final float animationPeriod = 0.14f;
    private float stateTime;

    public Vector2 velocity;
    public Vector2 direction;

    public int[] controlScheme;



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
        currentAnimation = idleAnimation;

        velocity = new Vector2();
        direction = new Vector2();
        stateTime = 0;

        controlScheme = new int[4];
        if (count % 2 == 0)
        {
            controlScheme = new int[]{Input.Keys.D, Input.Keys.A, Input.Keys.W, Input.Keys.S};
        } else {
            controlScheme = new int[]{Input.Keys.L, Input.Keys.J, Input.Keys.I, Input.Keys.K};
        }
        count ++;
    }

    public void process()
    {
        // Movement
        velocity.x = 0;
        velocity.y = 0;
        if (Gdx.input.isKeyPressed(controlScheme[0])) {
            direction.x = 1;
            velocity.x = 1;
        } if (Gdx.input.isKeyPressed(controlScheme[1])) {
            direction.x = -1;
            velocity.x = -1;
        } if (Gdx.input.isKeyPressed(controlScheme[2])) {
            velocity.y = 1;
        } if (Gdx.input.isKeyPressed(controlScheme[3])) {
            velocity.y = -1;
        }
        velocity.nor();
        move();

        // Animation
        stateTime += Gdx.graphics.getDeltaTime();

        if (is_walking()) {
            currentAnimation = walkAnimation;
        } else {
            currentAnimation = idleAnimation;
        }
        sprite = new Sprite(currentAnimation.getKeyFrame(stateTime, true));

        if(direction.x > 0) {
            sprite.flip(true, false);
        }
    }

    public void render(SpriteBatch batch)
    {
        batch.draw(sprite, x, y, 128, 128);
    }

    public void move()
    {
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
    }

    public String getIdentifiers()
    {
        String s = "";
        s += direction.x;
        s += ",";
        s += direction.y;
        s += ",";
        s += velocity.x;
        s += ",";
        s += velocity.y;
        return s;
    }

    public void setIdentifiers(String identifiers)
    {
        Scanner scanner = new Scanner(identifiers);
        scanner.useDelimiter(",");
        direction.x = scanner.nextFloat();
        direction.y = scanner.nextFloat();
        velocity.x = scanner.nextFloat();
        velocity.y = scanner.nextFloat();
    }

    public boolean is_walking()
    {
        return !velocity.isZero();
    }

    public void destroy()
    {
        animationSheet.dispose();
    }


}
