package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class Gate extends Empty implements PhysicsObject, Activatable {

    private Texture gateClosed;
    private Texture gateOpen;
    private boolean isActive;
    private int activationCount = 0;
    private Sprite sprite;
    private boolean isExitGate;
    private boolean isLaserCatEntranceGate;
    private boolean isReflectiveCatEntranceGate;

    public Gate(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
        gateClosed = new Texture(Gdx.files.internal("gate_closed.png"));
        gateOpen = new Texture(Gdx.files.internal("gate_open.png"));
        sprite = new Sprite(gateClosed);
        isExitGate = false;
        isLaserCatEntranceGate = false;
        isReflectiveCatEntranceGate = false;
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
        if (this.isLaserCatEntranceGate || this.isReflectiveCatEntranceGate) {
            return;
        }
        activationCount = 0;
    }
    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {}
    public void render(SpriteBatch batch){
        if (isExitGate) {batch.setColor(Color.GOLD);}
        else if (isLaserCatEntranceGate) {batch.setColor(Color.RED);}
        else if (isReflectiveCatEntranceGate) {batch.setColor(Color.BLUE);}
        batch.draw(sprite, x, y , width, height);
        batch.setColor(Color.WHITE);
    }
    public void destroy(){
        gateClosed.dispose();
        gateOpen.dispose();
    }
    @Override
    public void deactivate() {}
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
            json.put("id", getID());
            json.put("x", x);
            json.put("y", y);
            json.put("width", width);
            json.put("height", height);
            json.put("lasercatEntrance", isLaserCatEntranceGate);
            json.put("reflectiveCatEntrance", isReflectiveCatEntranceGate);
            json.put("exit", isExitGate);
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
            this.ID = json.getString("id");
            isLaserCatEntranceGate = json.getBoolean("lasercatEntrance");
            isReflectiveCatEntranceGate = json.getBoolean("reflectiveCatEntrance");
            isExitGate = json.getBoolean("exit");
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
    public void setAsExitGate() {
        isExitGate = true;
    }
    public void setAsLaserCatEntranceGate() {
        isLaserCatEntranceGate = true;
    }
    public void setAsReflectiveCatEntranceGate() {
        isReflectiveCatEntranceGate = true;
    }
    public boolean getIsExitGate() {
        return isExitGate;
    }
    public boolean getIsLaserCatEntranceGate() {
        return isLaserCatEntranceGate;
    }
    public boolean getIsReflectiveCatEntranceGate() {
        return isReflectiveCatEntranceGate;
    }
}