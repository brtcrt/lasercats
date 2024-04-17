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

    final private static int MAX_REFLECTIONS = 10;
    private int x1, y1;

    private Vector2 velocity, initialDirection;
    protected ArrayList<Vector2> vertices;
    private ShapeRenderer debugRenderer = new ShapeRenderer();
    private Viewport viewport;
    private ArrayList<PhysicsObject> physicsObjects;

    private ArrayList<PhysicsObject> ignoreAlways;
    private ArrayList<PhysicsObject> ignoreOnFirstReflection;

    public Laser(float x, float y, Vector2 direction, Viewport viewport, ArrayList<PhysicsObject> physicsObjects)
    {
        this.initialDirection = direction;
        this.initialDirection.nor();
        this.viewport = viewport;
        Gdx.gl.glLineWidth(10);
        vertices = new ArrayList<>();
        vertices.add(new Vector2(x,y));
        this.physicsObjects = physicsObjects;
        this.ignoreAlways = new ArrayList<>();
        this.ignoreOnFirstReflection = new ArrayList<>();
    }


    @Override
    public void process() {
        Vector2 firstVertex = vertices.get(0);
        vertices.clear();
        vertices.add(firstVertex);
        Vector2 finalDirection = new Vector2(initialDirection);

        int reflections = 0;

        Rectangle viewportBox = new Rectangle(-600, -600,
                3000, 3000);


        boolean finishedTraveling = false;

        Vector2 start = null;
        Vector2 end = null;
        while (!finishedTraveling) {

            start = vertices.get(vertices.size() - 1);
            end = new Vector2(start).mulAdd(finalDirection, 100_000);
            vertices.add(end);

            Vector2 closestIntersectionInDirection = new Vector2();
            float distanceOfClosestIntersectionInDirection = Float.POSITIVE_INFINITY;
            Vector2 normalOfClosestIntersectionInDirection = new Vector2();

            for (PhysicsObject object : physicsObjects) {
                if (reflections == 0 && ignoreOnFirstReflection.contains(object)) continue;
                if (ignoreAlways.contains(object)) continue;

                Rectangle collider = object.getCollider();
                Polygon colliderPolygon = new Polygon(new float[]{
                        0, 0,
                        0, collider.height,
                        collider.width, collider.height,
                        collider.width, 0,});
                colliderPolygon.setPosition(collider.x, collider.y);

                if (!Intersector.intersectLinePolygon(start, end, colliderPolygon)) continue;

                float[] colliderVertices = colliderPolygon.getTransformedVertices();
                for (int i = 0; i < colliderVertices.length; i += 2) {
                    Vector2 vertex1 = new Vector2(colliderVertices[i], colliderVertices[i + 1]);
                    Vector2 vertex2 = new Vector2(colliderVertices[(i + 2) % colliderVertices.length],
                            colliderVertices[(i + 3) % colliderVertices.length]);

                    Vector2 intersection = new Vector2();
                    boolean intersects = Intersector.intersectSegments(vertex1, vertex2, start, end, intersection);
                    if (!intersects) continue;

                    if (intersection.equals(start)) continue;


                    float distanceToIntersection = start.dst(intersection);

                    if (distanceToIntersection < distanceOfClosestIntersectionInDirection) {
                        distanceOfClosestIntersectionInDirection = distanceToIntersection;
                        closestIntersectionInDirection.set(intersection);
                        normalOfClosestIntersectionInDirection.set(vertex2.y - vertex1.y, vertex2.x - vertex1.x);
                    }

                }
            }

            end.set(closestIntersectionInDirection);
            finalDirection = reflect(finalDirection, normalOfClosestIntersectionInDirection);

            reflections++;

            if (!viewportBox.contains(end) || reflections > MAX_REFLECTIONS) finishedTraveling = true;
        }
    }

    protected void addObjectToIgnore(PhysicsObject physicsObject, boolean firstReflection)
    {
        if (firstReflection) ignoreOnFirstReflection.add(physicsObject);
        else ignoreAlways.add(physicsObject);
    }

    public void rotateLeft()
    {
        this.initialDirection.rotateDeg(45);
    }
    public void rotateRight()
    {
        this.initialDirection.rotateDeg(-45);
    }

    private Vector2 reflect(Vector2 direction, Vector2 normal)
    {
        normal.nor();
        direction.x = direction.x - 2*(direction.x * normal.x + direction.y * normal.y) * normal.x;
        direction.y = direction.y - 2*(direction.x * normal.x + direction.y * normal.y) * normal.y;
        direction.nor();
        return direction;
        // finalDirection keeps its magnitude
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
