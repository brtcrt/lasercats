package com.lasercats.GameObjects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public interface PhysicsObject extends GameObject{
    public Vector2 getVelocity();

    public Rectangle getCollider();

    public boolean isStatic();

    public void calculatePhysics(ArrayList<PhysicsObject> objects);

}
