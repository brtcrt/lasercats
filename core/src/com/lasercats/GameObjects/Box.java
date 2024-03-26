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

public class Box extends Empty implements GameObject{
    private Texture boxImage;
    private Sprite sprite;
    public Vector2 velocity;
    private Player[] players = new Player[2];
    protected final float walkSpeed = 150f;
    private final static float WIDTH = 128 , HEIGHT = 128;

    public Box(int x, int y, Player p1, PlayerNonMain p2){
        super(x, y, WIDTH, HEIGHT);
        boxImage = new Texture(Gdx.files.internal("Box.png"));
        sprite = new Sprite(boxImage);
        velocity = new Vector2();
        players[0] = p1;
        players[1] = p2;
    }

    public void process(){
        // Movement
        velocity.x = 0;
        velocity.y = 0;
        for(Player p: players){
            if (p.overlaps(this)) {
                velocity.x = p.velocity.x;
                velocity.y = p.velocity.y;
            }
        }   
        velocity.nor();
        move();

    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y, width, height);
    }

    public void destroy(){
        boxImage.dispose();
    }


    public float getY() {
        return y;
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
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
    }
}

