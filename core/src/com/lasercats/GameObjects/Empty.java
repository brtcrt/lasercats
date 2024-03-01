package com.lasercats.GameObjects;


import com.badlogic.gdx.math.Rectangle;

public class Empty extends Rectangle {
    //We might use other shapes like circles
    //If this is the case, we need other constructor (there might be "Shape superclass" in the engine)-Doruk
    public Empty(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
}
