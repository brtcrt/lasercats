package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestObject extends Empty implements GameObject, Activatable{
    private Texture imageOpen;
    private Texture imageClosed;
    private Sprite sprite;
    private boolean isActive;

    public TestObject(float x, float y, float width, float height){
        super(x, y, width, height);
        velocity = new Vector2();
        imageOpen = new Texture(Gdx.files.internal("box_open.png"));
        imageClosed = new Texture(Gdx.files.internal("box_closed.png"));
        sprite = new Sprite(imageClosed);
    }

    public void process(){
        if (isActive) {
            sprite = new Sprite(imageOpen);
        } else {
            sprite = new Sprite(imageClosed);
        }
    }

    @Override
    public void activate() {
        this.isActive = true;
    }

    @Override
    public void deactivate() {
        this.isActive = false;
    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y , width, height);
    }

    public void destroy(){
        imageOpen.dispose();
        imageClosed.dispose();
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
    public int getActivationCount() {
        return 0;
    }
}
