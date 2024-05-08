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
    // current Texture is 37x50 for some fucking reason idk it said it was 32x32 on itch.io
    private Texture gateClosed;
    private Texture gateOpen;
    private boolean isActive;
    private Sprite sprite;
    private boolean isExitGate;
    private boolean isLaserCatEntranceGate;
    private boolean isReflectiveCatEntranceGate;

    public Gate(float x, float y, float width, float height){
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
        if (isActive) {
            sprite = new Sprite(gateOpen);
        } else {
            sprite = new Sprite(gateClosed);
        }
    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {

    }

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
    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public void activate() {
        this.isActive = true;
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
