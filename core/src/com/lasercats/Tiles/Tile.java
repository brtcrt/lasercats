package com.lasercats.Tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Basically just background objects that don't interact with GameObjects
public interface Tile {
    public void render(SpriteBatch batch);
    public void destroy();

}
