package com.lasercats.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;


public interface GameObject {
    public void process();
    public void render(SpriteBatch batch);

    public void destroy();

    public String getIdentifiers();

    public void setIdentifiers(String json);
}
