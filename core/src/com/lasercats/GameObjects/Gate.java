package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Gate extends Empty implements PhysicsObject, Activatable {
    // current Texture is 37x50 for some fucking reason idk it said it was 32x32 on itch.io
    private Texture gateClosed;
    private Texture gateOpen;
    private boolean isActive;
    private int activationCount = 0;
    private Sprite sprite;


    public Gate(float x, float y, float width, float height){
        super(x, y, width, height);
        velocity = new Vector2();
        gateClosed = new Texture(Gdx.files.internal("gate_closed.png"));
        gateOpen = new Texture(Gdx.files.internal("gate_open.png"));
        sprite = new Sprite(gateClosed);
    }

    public void process(){
        if (activationCount > 0) {
            isActive = true;
        } else {
            isActive = false;
        }
        if (isActive) {
            sprite = new Sprite(gateOpen);
        } else {
            sprite = new Sprite(gateClosed);
        }
        activationCount = 0;
    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {

    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y , width, height);
    }

    public void destroy(){
        gateClosed.dispose();
        gateOpen.dispose();
    }


    @Override
    public void deactivate() {
    }

    @Override
    public void activate() {
        this.activationCount++;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean canCollide() {
        return !isActive;
    }

    public JSONObject getIdentifiers(){
        JSONObject json = new JSONObject();
        try {
            json.put("type", this.getClass().getName());
            json.put("x", x);
            json.put("y", y);
            json.put("width", width);
            json.put("height", height);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json){
        try {
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
            width = (float)json.getDouble("width");
            height = (float)json.getDouble("height");
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    public int getActivationCount() {
        return activationCount;
    }

    @Override
    public boolean isStatic() {
        return true;
    }
}
