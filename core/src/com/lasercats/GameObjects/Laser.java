package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import com.badlogic.gdx.utils.Array;

public class Laser implements GameObject {
    public String ID = java.util.UUID.randomUUID().toString();

    final private static int MAX_REFLECTIONS = 10;
    protected float x1, y1;

    public boolean isFiring;
    private Vector2 velocity, initialDirection;
//    protected ArrayList<Vector2> vertices;
//    protected CopyOnWriteArrayList<Vector2> vertices;
    protected Array<Vector2> vertices;
    protected ShapeRenderer debugRenderer = new ShapeRenderer();
    public Viewport viewport;
    public ArrayList<PhysicsObject> physicsObjects;

    private ArrayList<PhysicsObject> ignoreAlways;
    private ArrayList<PhysicsObject> ignoreOnFirstReflection;

    public Laser(float x, float y, Vector2 direction, Viewport viewport, ArrayList<PhysicsObject> physicsObjects)
    {
        this.isFiring = true;
        this.initialDirection = direction;
        this.initialDirection.nor();
        this.viewport = viewport;
        Gdx.gl.glLineWidth(10);
//        vertices = new ArrayList<>();
//        vertices = new CopyOnWriteArrayList<>();
        vertices = new Array<>();
        x1 = x;
        y1 = y;
        // vertices.add(new Vector2(x,y));
        this.physicsObjects = physicsObjects;
        this.ignoreAlways = new ArrayList<>();
        this.ignoreOnFirstReflection = new ArrayList<>();
    }


    @Override
    public void process() {
        try {
            // Vector2 firstVertex = vertices.get(0);
            vertices.clear();
            vertices.add(new Vector2(x1, y1)); // size 1
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
            System.out.println("kill me please for fucks sake end this misery");
        }

        Vector2 finalDirection = new Vector2(initialDirection);

        int reflections = 0;

        //I think there are some issues regarding this rectangle
        //On the initial level we created, laser reflections work just fine.
        //However, on other levels reflections don't properly work
        Rectangle viewportBox = new Rectangle(-50_000, -50_000,
                100_000, 100_000);


        boolean finishedTraveling = false;

        Vector2 start = null;
        Vector2 end = null;
        PhysicsObject lastCollidedObject = physicsObjects.get(0);
        while (!finishedTraveling) {

//            if (vertices.size() == 0) {
            if (vertices.size == 0) {
                System.out.println("ARRRRRGHHHH");
                continue;
            }
//            System.out.println(vertices.size());
            //System.out.println(vertices.size);
//            start = vertices.getLast();
//            start = vertices.get(vertices.size() - 1); // BUG IS HERE
            start = vertices.get(vertices.size - 1); // BUG IS HERE
            end = new Vector2(start).mulAdd(finalDirection, 100_000);
            vertices.add(end); // iter 1: size: 2

            Vector2 closestIntersectionInDirection = new Vector2();
            float distanceOfClosestIntersectionInDirection = Float.POSITIVE_INFINITY;
            Vector2 normalOfClosestIntersectionInDirection = new Vector2();

            for (PhysicsObject object : physicsObjects) {
                if (reflections == 0 && ignoreOnFirstReflection.contains(object)) continue;
                if (ignoreAlways.contains(object) || object instanceof PressurePlate || object instanceof Glass) continue;


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
                        lastCollidedObject = object;
                        distanceOfClosestIntersectionInDirection = distanceToIntersection;
                        closestIntersectionInDirection.set(intersection);
                        normalOfClosestIntersectionInDirection.set(vertex2.y - vertex1.y, vertex2.x - vertex1.x);
                    }

                }
            }

            end.set(closestIntersectionInDirection);
            finalDirection = reflect(finalDirection, normalOfClosestIntersectionInDirection);
            reflections++;
            if (!((lastCollidedObject instanceof Mirror) || (lastCollidedObject instanceof Player && ((Player)lastCollidedObject).getIsReflective()))) finishedTraveling = true;
            if (!viewportBox.contains(end) || reflections > MAX_REFLECTIONS) finishedTraveling = true;
            if (lastCollidedObject instanceof LaserTarget) {
                LaserTarget target = (LaserTarget) lastCollidedObject;
                target.trigger();
            }
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
        JSONObject json = new JSONObject();
        ArrayList<int[]> arr = new ArrayList<>();
        for (int i = 0; i < vertices.size; i++) {
            int[] coords = {(int)vertices.get(i).x, (int)vertices.get(i).y};
            arr.add(coords);
        }
        double[] dir = {initialDirection.x, initialDirection.y};
        try {
            json.put("type", this.getClass().getName());
            json.put("id", getID());
            json.put("x", x1);
            json.put("y", y1);
            // json.put("vertices", arr);
            json.put("initialDirection",dir);
            json.put("isFiring", isFiring);
        }
        catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    @Override
    public void setIdentifiers(JSONObject json) {
        try {
            // JSONArray a = json.getJSONArray("vertices");
//            vertices.clear();
//            for(int i = 0; i < a.length(); i++){
//                JSONArray xy = a.getJSONArray(i);
//                vertices.add(new Vector2((int)(xy.get(0)), (int)(xy.get(1))));
//            }
            JSONArray dir = json.getJSONArray("initialDirection");
            Double xDir = dir.getDouble(0);
            Double yDir = dir.getDouble(1);
            this.initialDirection = new Vector2(xDir.floatValue(), yDir.floatValue());
            initialDirection.nor();
            this.ID = json.getString("id");
            Double x = json.getDouble("x");
            Double y = json.getDouble("y");
            x1 = x.floatValue();
            y1 = y.floatValue();
            isFiring = json.getBoolean("isFiring");

        }
        catch (JSONException e) {
            System.out.println("AAAAAAARRRRRRGGHHHHHHHHHHH");
            System.out.println(e);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isFiring) return;
        batch.end();
        debugRenderer.flush();
        debugRenderer.setProjectionMatrix(viewport.getCamera().combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
//        for (int i = 0; i < vertices.size() - 1; i ++)

        try {
            for (int i = 0; i < vertices.size - 1; i ++)
            {
                debugRenderer.line(vertices.get(i), vertices.get(i + 1));
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
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

    public Rectangle setX(float x) {return new Rectangle();}
    public Rectangle setY(float y) {return new Rectangle();}
    public String getID() { return this.ID; }
}
