package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

public class Player extends Empty implements  GameObject{

    protected Texture animationSheet;
    protected Sprite sprite;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> currentAnimation;

    protected final float walkSpeed = 150f;
    protected final float animationPeriod = 0.14f;
    private final static float WIDTH = 128 , HEIGHT = 128;
    protected float stateTime;

    public Vector2 velocity;
    public Vector2 direction;

    public int[] controlScheme;



    public Player (float x, float y, float width, float height) {
        super(x, y, width, height);
        animationSheet = new Texture(Gdx.files.internal("CatAnimationSheet.png"));

        TextureRegion[][] tmp = TextureRegion.split(animationSheet, 32, 32);
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

        controlScheme = new int[]{Input.Keys.D, Input.Keys.A, Input.Keys.W, Input.Keys.S};
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
        batch.draw(sprite, x, y, WIDTH, HEIGHT);
    }

    public void move()
    {
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
    }

    public JSONObject getIdentifiers()
    {
        // Fuck libgdx's json library I'm changing it to org.json ~brtcrt
        JSONObject json = new JSONObject();
        try {
            json.put("direction.x", direction.x);
            json.put("direction.y", direction.y);
            json.put("velocity.x", velocity.x);
            json.put("velocity.y", velocity.y);
            json.put("x", x);
            json.put("y", y);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json)
    {
        try {
            direction.x = (float)json.getDouble("direction.x");
            direction.y = (float)json.getDouble("direction.y");
            velocity.x = (float)json.getDouble("velocity.x");
            velocity.y = (float)json.getDouble("velocity.y");
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
        } catch (JSONException e) {
            System.out.println(e);
        }

    }

    public boolean is_walking()
    {
        return !velocity.isZero();
    }

    public float getY() {
        return y;
    }

    public void destroy()
    {
        animationSheet.dispose();
    }


}
