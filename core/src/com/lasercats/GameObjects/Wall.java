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

public class Wall extends Empty implements PhysicsObject {
    private Texture wallImage;
    private Sprite sprite;

    public Wall(float x, float y, float width, float height){
        super(x, y, width, height);
        velocity = new Vector2();
        wallImage = new Texture(Gdx.files.internal("wall.png"));
        sprite = new Sprite(wallImage);
    }

    public void process(){

    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {

    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y , width, height);
    }

    public void destroy(){
        wallImage.dispose();
    }


    public JSONObject getIdentifiers(){
        JSONObject json = new JSONObject();
        try {
            json.put("x", x);
            json.put("y", y);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json){
        try {
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean isStatic() {
        return true;
    }
}

