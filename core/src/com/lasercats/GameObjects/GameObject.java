package com.lasercats.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONObject;


public interface GameObject {
    public void process();
    public void render(SpriteBatch batch);

    public void destroy();

    public JSONObject getIdentifiers();

    public void setIdentifiers(JSONObject json);

    public float getX();
    public float getY();
    public Vector2 getVelocity();

    public Rectangle getCollider();
}
