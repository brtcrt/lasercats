package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PressurePlate extends Empty implements GameObject, Detector, PhysicsObject {
    private Texture image;
    private Sprite sprite;
    private ArrayList<Activatable> activatables;
    private boolean isTriggered;

    public PressurePlate(float x, float y, float width, float height, ArrayList<Activatable> activatables){
        super(x, y, width, height);
        velocity = new Vector2();
        image = new Texture(Gdx.files.internal("plate.png"));
        sprite = new Sprite(image);
        isTriggered = false;
        setActivatables(activatables);
    }

    public PressurePlate(float x, float y, float width, float height, Activatable a){
        super(x, y, width, height);
        velocity = new Vector2();
        image = new Texture(Gdx.files.internal("plate.png"));
        sprite = new Sprite(image);
        isTriggered = false;
        ArrayList<Activatable> arr = new ArrayList<Activatable>();
        arr.add(a);
        setActivatables(arr);
    }

    public void process(){
        for (Activatable a : activatables) {
            if (isTriggered) {
                a.activate();
            } else {
                a.deactivate();
            }
        }
    }

    @Override
    public void setActivatables(ArrayList<Activatable> activatables) {
        this.activatables = activatables;
    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {
        isTriggered = false;
        for (PhysicsObject o : objects) {
            if (o.getCollider().overlaps(this)) {
                this.isTriggered = true;
            }
        }
    }

    @Override
    public boolean isStatic() {
        return true;
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
    public boolean isTriggered() {
        return isTriggered;
    }

    @Override
    public boolean canCollide() {
        return false;
    }
}
