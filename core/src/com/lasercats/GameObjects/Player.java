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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;

public class Player extends Empty implements  GameObject{

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
        batch.draw(sprite, x, y, 128, 128);
    }

    public void move()
    {
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
    }

    public String getIdentifiers()
    {
        Json json = new Json();
        StringWriter jsonText = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(jsonText);

        json.setOutputType(JsonWriter.OutputType.json);
        json.setWriter(jsonWriter);
        json.writeObjectStart();
        json.writeValue("direction.x", direction.x);
        json.writeValue("direction.y", direction.y);
        json.writeValue("velocity.x", velocity.x);
        json.writeValue("velocity.y", velocity.y);
        json.writeObjectEnd();

        return json.getWriter().getWriter().toString();
    }

    public void setIdentifiers(String json)
    {
        JsonReader reader = new JsonReader();
        JsonValue jsonValue = reader.parse(json);
        direction.x = jsonValue.getFloat("direction.x");
        direction.y = jsonValue.getFloat("direction.y");
        velocity.x = jsonValue.getFloat("velocity.x");
        velocity.y = jsonValue.getFloat("velocity.y");
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
