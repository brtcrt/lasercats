package com.lasercats.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.lasercats.Tiles.Tile;
import org.json.JSONObject;


public interface GameObject extends Tile {
    public void process();
    public void render(SpriteBatch batch);
    public void destroy();

    public JSONObject getIdentifiers();

    public void setIdentifiers(JSONObject json);

    public float getX();
    public float getY();
    public String getID();

    public Rectangle setX(float x);
    public Rectangle setY(float y);


}
