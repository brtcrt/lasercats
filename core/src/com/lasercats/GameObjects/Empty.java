package com.lasercats.GameObjects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Empty extends Rectangle {

    public Vector2 velocity;
    public String ID = java.util.UUID.randomUUID().toString();

    public Empty(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
    }
    public Vector2 getVelocity() {
        return this.velocity;
    }
    public Rectangle getCollider() {
        return this;
    }
    public String getID() { 
        return this.ID; 
    }
}
