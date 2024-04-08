package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.sun.tools.javac.code.Scope;

import java.awt.*;


import com.badlogic.gdx.graphics.Color;

public abstract class Laser implements GameObject {
    int x1, x2, y1, y2;
    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    private static Matrix4 projectionMatrix = new Matrix4();
    public Laser()
    {
        x1 = 0;
        y1 = 0;
        x2 = 100;
        y2 = 100;
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.line(new Vector2(x1,y1), new Vector2(x2,y2));
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }


}
