package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.graphics.Color;
import org.json.JSONObject;

import java.util.ArrayList;

public class Laser implements GameObject {
    private int x1, y1;
    private Vector2 velocity, direction;
    private ArrayList<Vector2> refractions;
    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    private static Matrix4 projectionMatrix = new Matrix4();
    public Laser(int x, int y, Vector2 direction)
    {
        x1 = 0;
        y1 = 0;
        this.direction = direction;
        velocity = new Vector2(x1, y1);
        Gdx.gl.glLineWidth(1);
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix)
    {
        this.projectionMatrix = projectionMatrix;
    }
    @Override
    public void process() {

    }

    @Override
    public void destroy() {
        debugRenderer.dispose();
    }

    @Override
    public JSONObject getIdentifiers() {
        return null;
    }

    @Override
    public void setIdentifiers(JSONObject json) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.line(new Vector2(x1,y1), direction);
        debugRenderer.end();
        batch.begin();
    }

    @Override
    public float getX() {
        return x1;
    }

    @Override
    public float getY() {
        return y1;
    }

    public Vector2 getVelocity() { return this.velocity; }
    public Rectangle getCollider() {
        return new Rectangle(0,0,0,0);
    }
}
