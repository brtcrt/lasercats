package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Laser implements GameObject {
    private int x1, y1;
    private Vector2 velocity, initialDirection, finalDirection;
    private ArrayList<Vector2> vertices;
    private static ShapeRenderer debugRenderer = new ShapeRenderer();
    private Viewport viewport;
    private ArrayList<GameObject> gameObjects;

    public Laser(int x, int y, Vector2 direction, Viewport viewport, ArrayList<GameObject> gameObjects)
    {
        x1 = 0;
        y1 = 0;
        this.initialDirection = direction;
        this.initialDirection.nor();
        this.viewport = viewport;
        velocity = new Vector2(x1, y1);
        Gdx.gl.glLineWidth(3);
        vertices = new ArrayList<>();
        vertices.add(new Vector2(0,0));
        this.gameObjects = gameObjects;
    }


    @Override
    public void process() {
        Vector2 firstVertex = vertices.get(0);
        vertices.clear();
        vertices.add(firstVertex);
        finalDirection = new Vector2(initialDirection);

        int reflections = 0;

        boolean finishedTravelling = false;
        while (!finishedTravelling) {
            if (reflections > 10)
            {
                finishedTravelling = true;
                break;
            }

            Vector2 endpoint = new Vector2(vertices.get(vertices.size() - 1));
            Vector2 lastStationaryVertex = vertices.get(vertices.size() - 1);
            vertices.add(endpoint);

            boolean hasReflected = false;
            while (!hasReflected) {
                endpoint.x += finalDirection.x;
                endpoint.y += finalDirection.y;

                Rectangle viewportBox = new Rectangle(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
                if (!viewportBox.contains(endpoint)) {
                    finishedTravelling = true;
                    break;
                }

                for (GameObject object : gameObjects) {

                    Rectangle collider = object.getCollider();
                    Polygon colliderPolygon = new Polygon(new float[] {
                            0,0,
                            0, collider.height,
                            collider.width, collider.height,
                            collider.width, 0,});
                    colliderPolygon.setPosition(collider.x, collider.y);

                    if (Intersector.intersectSegmentPolygon(endpoint, lastStationaryVertex, colliderPolygon))
                    {
                        Vector2 closestVertex1 = new Vector2();
                        Vector2 closestVertex2 = new Vector2();
                        float shortestDistance = Float.POSITIVE_INFINITY;

                        float[] colliderVertices = colliderPolygon.getTransformedVertices();
                        for (int i = 0; i < colliderVertices.length; i+=2) {
                            Vector2 intersection = new Vector2();
                            boolean intersects = Intersector.intersectSegments(colliderVertices[i], colliderVertices[i + 1],
                                    colliderVertices[(i + 2) % colliderVertices.length], colliderVertices[(i + 3) % colliderVertices.length],
                                    endpoint.x, endpoint.y, lastStationaryVertex.x, lastStationaryVertex.y, intersection);
                            if (intersects) {
                                vertices.get(vertices.size() - 1).set(intersection);

                                endpoint = new Vector2(intersection);

                                Vector2 normal = new Vector2(closestVertex1.y - closestVertex2.y, closestVertex1.x - closestVertex2.x);
                                reflect(intersection, normal);
                                reflections++;
                                hasReflected = true;
                            }
//                            float distance = Intersector.distanceSegmentPoint(vertex1, vertex2, endpoint);
//                            if (distance < shortestDistance) {
//                                closestVertex1 = vertex1;
//                                closestVertex2 = vertex2;
//                            }
//                        }
//
//                        Vector2 intersection = new Vector2(); // intersectLines changes this value
//                        Intersector.intersectLines(closestVertex1, closestVertex2, endpoint,
//                                lastStationaryVertex, intersection);

                        }
                    }
                }
            }
        }
    }

    private void reflect(Vector2 position, Vector2 normal)
    {
        normal.nor();
        vertices.add(position);
        finalDirection.x = finalDirection.x - 2*(finalDirection.x * normal.x + finalDirection.y * normal.y) * normal.x;
        finalDirection.y = finalDirection.y - 2*(finalDirection.x * normal.x + finalDirection.y * normal.y) * normal.y;
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
