package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Mirror extends Empty implements PhysicsObject {
    private TextureRegion[][] dungeonTextures;
    private Texture image;
    private TextureRegion[] textures;
    private Sprite sprite;

    public Mirror(float x, float y, float width, float height){
        super(x, y, width, height);
        image = new Texture(Gdx.files.internal("mirror.png"));
        velocity = new Vector2();
        sprite = new Sprite(image);
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
        image.dispose();
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

    @Override
    public boolean canCollide() {
        return true;
    }
}

