package com.lasercats.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.json.JSONObject;


public interface GameObject {
    public void process();
    public void render(SpriteBatch batch);

    public void destroy();

    public JSONObject getIdentifiers();

    public void setIdentifiers(JSONObject json);

    public float getY();
}
