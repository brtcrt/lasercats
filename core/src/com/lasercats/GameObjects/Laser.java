package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Intersector;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.json.JSONObject;

import java.util.ArrayList;

public class Laser implements GameObject {
    private int x1, y1;
    private Vector2 velocity, initialDirection, finalDirection;
    private ArrayList<Vector2> vertices;
    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    private Viewport viewport;
    private ArrayList<GameObject> gameObjects;

    public Laser(int x, int y, Vector2 direction, Viewport viewport, ArrayList<GameObject> gameObjects)
    {
        this.initialDirection = direction;
        this.initialDirection.nor();
        this.viewport = viewport;
        velocity = new Vector2(x1, y1);
        Gdx.gl.glLineWidth(10);
        vertices = new ArrayList<>();
        vertices.add(new Vector2(10,10));
        this.gameObjects = gameObjects;
    }


    @Override
    public void process() {
        Vector2 firstVertex = vertices.get(0);
        vertices.clear();
        vertices.add(firstVertex);
        finalDirection = new Vector2(initialDirection);

        int reflections = 0;

        Rectangle viewportBox = new Rectangle(viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight());


        boolean finishedTraveling = false;
        boolean justReflected = false;
        boolean firstLoop = true;

        Vector2 lastStationaryVertex = null;
        Vector2 movingEndVertex = null;
        while (!finishedTraveling) {

            if (justReflected || firstLoop) {
                justReflected = false;
                lastStationaryVertex = vertices.get(vertices.size() - 1);
                movingEndVertex = new Vector2(lastStationaryVertex);
                vertices.add(movingEndVertex);
            }

            // if incrementing the position is here, the last stationary vertex collides with the polygon
            // do not put   movingEndVertex.x/y += finalDirection.x/y; here


            for (GameObject object : gameObjects) {

                Rectangle collider = object.getCollider();
                Polygon colliderPolygon = new Polygon(new float[]{
                        0, 0,
                        0, collider.height,
                        collider.width, collider.height,
                        collider.width, 0,});
                colliderPolygon.setPosition(collider.x, collider.y);

                if (!Intersector.intersectSegmentPolygon(movingEndVertex, lastStationaryVertex, colliderPolygon)) continue;

                float[] colliderVertices = colliderPolygon.getTransformedVertices();
                for (int i = 0; i < colliderVertices.length; i += 2) {
                    Vector2 vertex1 = new Vector2(colliderVertices[i], colliderVertices[i + 1]);
                    Vector2 vertex2 = new Vector2(colliderVertices[(i + 2) % colliderVertices.length],
                            colliderVertices[(i + 3) % colliderVertices.length]);

                    Vector2 intersection = new Vector2();
                    boolean intersects = Intersector.intersectSegments(vertex1, vertex2, movingEndVertex, lastStationaryVertex, intersection);
                    if (!intersects) continue;

                    movingEndVertex.set(intersection);

                    Vector2 normal = new Vector2( vertex2.y - vertex1.y, vertex2.x - vertex1.x);
                    reflect(normal);

                    reflections++;
                    justReflected = true;
                    break;
                }
                if (justReflected) break;
            }

            movingEndVertex.x += finalDirection.x;
            movingEndVertex.y += finalDirection.y;

            if (!viewportBox.contains(movingEndVertex) || reflections > 10) finishedTraveling = true;

            firstLoop = false;
        }
    }

    private void reflect(Vector2 normal)
    {
        normal.nor();
        finalDirection.x = finalDirection.x - 2*(finalDirection.x * normal.x + finalDirection.y * normal.y) * normal.x;
        finalDirection.y = finalDirection.y - 2*(finalDirection.x * normal.x + finalDirection.y * normal.y) * normal.y;
        finalDirection.nor();
        // finalDirection keeps its magnitude
        System.out.println(finalDirection);
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
        debugRenderer.setProjectionMatrix(viewport.getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        for (int i = 0; i < vertices.size() - 1; i ++)
        {
            debugRenderer.line(vertices.get(i), vertices.get(i + 1));
        }
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
