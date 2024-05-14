package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Glass extends Empty implements PhysicsObject {

    private Texture img;
    private Sprite sprite;

    public Glass(float x, float y, float width, float height) {
        super(x, y, width, height);
        img = new Texture(Gdx.files.internal("glass.png"));
        sprite = new Sprite(img);
    }
    public void process(){}
    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {}
    public void render(SpriteBatch batch) {
        batch.draw(sprite, x, y , width, height);
    }
    public void destroy(){
        img.dispose();
    }
    public JSONObject getIdentifiers() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", this.getClass().getName());
            json.put("x", x);
            json.put("y", y);
            json.put("width", width);
            json.put("height", height);
            json.put("id", getID());
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }
    public void setIdentifiers(JSONObject json) {
        try {
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
            width = (float)json.getDouble("width");
            height = (float)json.getDouble("height");
            this.ID = json.getString("id");
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