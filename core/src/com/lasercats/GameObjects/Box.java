package com.lasercats.GameObjects;

import com.badlogic.gdx.Game;
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

import java.util.ArrayList;

public class Box extends Empty implements PhysicsObject {
    private Texture boxImage;
    private Sprite sprite;
    private ArrayList<GameObject> gameObject;
    protected final float moveSpeed = 150f;
    private final static float WIDTH = 128 , HEIGHT = 128;

    public Box(int x, int y, ArrayList<GameObject> gameObjects){
        super(x, y, WIDTH - 36, HEIGHT - 4);
        boxImage = new Texture(Gdx.files.internal("Box.png"));
        sprite = new Sprite(boxImage);
        // changed this to GameObject ArrayList so boxes can also interact with each other.
        this.gameObject = gameObjects;
    }

    public void process(){

    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {
        velocity.x = 0;
        velocity.y = 0;

        for(PhysicsObject object : objects){
            if (object.getCollider().overlaps(this)) {
                Vector2 center = object.getCollider().getCenter(new Vector2(object.getX(), object.getY()));
                Vector2 moveVector = this.getCenter(new Vector2(x, y)).sub(center);
                moveVector.nor();
                velocity.x += moveVector.x;
                velocity.y += moveVector.y;

            }
        }

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            velocity.y = 0;
        } else {
            velocity.x = 0;
        }
        velocity.nor();
        move();
    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x - 16, y , WIDTH, HEIGHT);
    }

    public void destroy(){
        boxImage.dispose();
    }


    public JSONObject getIdentifiers(){
        JSONObject json = new JSONObject();
        try {
            json.put("velocity.x", velocity.x);
            json.put("velocity.y", velocity.y);
            json.put("x", x);
            json.put("y", y);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json){
        try {
            velocity.x = (float)json.getDouble("velocity.x");
            velocity.y = (float)json.getDouble("velocity.y");
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public void move()
    {
        x += velocity.x * moveSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * moveSpeed * Gdx.graphics.getDeltaTime();
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}

