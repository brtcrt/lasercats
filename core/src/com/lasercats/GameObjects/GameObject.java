package com.lasercats.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface GameObject {
    public void process();
    public void render(SpriteBatch batch);

    public void destroy();
}
