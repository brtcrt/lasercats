package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaserTarget extends Empty implements Detector, PhysicsObject {

    private Texture image;
    private Sprite sprite;
    private ArrayList<Activatable> activatables;
    private ArrayList<String> activatableIDs = new ArrayList<>();;
    private TextureRegion offFrame;
    private Animation<TextureRegion> onAnimation;
    private float state;
    private boolean isTriggered;

    public LaserTarget(int x, int y, int width, int height) {
        this((float)x, (float)y, (float)width, (float)height);
    }
    public LaserTarget(float x, float y, float width, float height) {

        super(x, y, width, height);
        velocity = new Vector2();
        image = new Texture(Gdx.files.internal("LaserTargetSheet2.png"));
        TextureRegion[][] tmp = TextureRegion.split(image, 32, 32);
        TextureRegion[] offFrames = tmp[0];
        TextureRegion[] onFrames = tmp[1];
        offFrame = offFrames[0];
        onAnimation = new Animation<TextureRegion>(0.4f, onFrames);
        onAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        sprite = new Sprite(offFrame);
        isTriggered = false;
        state = 0;
        activatables = new ArrayList<Activatable>();
    }
    public LaserTarget(float x, float y, float width, float height, ArrayList<Activatable> activatables){
        this(x, y, width, height);
        setActivatables(activatables);
    }
    public LaserTarget(float x, float y, float width, float height, Activatable a){
        this(x,y,width,height, new ArrayList<Activatable>());
        ArrayList<Activatable> arr = new ArrayList<Activatable>();
        arr.add(a);
        setActivatables(arr);
    }
    public void process(){
        state += Gdx.graphics.getDeltaTime();
        for (Activatable a : activatables) {
            if (isTriggered) {
                sprite = new Sprite(onAnimation.getKeyFrame(state, true));
                a.activate();
            } else {
                sprite = new Sprite(offFrame);
                a.deactivate();
            }
        }
    }
    @Override
    public void setActivatables(ArrayList<Activatable> activatables) {
        this.activatables = activatables;
    }
    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {
        reset();
        if (this.activatables.isEmpty()) {
            for (PhysicsObject o : objects) {
                if (activatableIDs.contains(o.getID())) {
                    activatables.add((Activatable) o);
                }
            }
        }
    }
    @Override
    public boolean isStatic() {
        return true;
    }
    public void render(SpriteBatch batch) {
        batch.draw(sprite, x, y , width, height);
    }
    public void destroy() {
        image.dispose();
    }
    @Override
    public void addActivatable(Activatable a) {
        for (Activatable activatable : activatables) {
            if (activatable.equals(a)) {
                return;
            }
        }
        activatables.add(a);
        activatableIDs.add(((GameObject)a).getID());
    }
    public JSONObject getIdentifiers(){
        JSONObject json = new JSONObject();
        try {
            json.put("type", this.getClass().getName());
            json.put("x", x);
            json.put("y", y);
            json.put("width", width);
            json.put("height", height);
            json.put("id", getID());
            json.put("activatables", this.activatableIDs);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }
    public void setIdentifiers(JSONObject json){
        try {
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
            width = (float)json.getDouble("width");
            height = (float)json.getDouble("height");
            this.ID = json.getString("id");
            JSONArray activatableIDsJSON = json.getJSONArray("activatables");
            for (int i = 0; i < activatableIDsJSON.length(); i++) {
                activatableIDs.add(activatableIDsJSON.getString(i));
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    @Override
    public boolean isTriggered() {
        return isTriggered;
    }
    public ArrayList<Activatable> getActivatables() {
        return activatables;
    }
    @Override
    public boolean canCollide() {
        return false;
    }
    public void trigger() {
        this.isTriggered = true;
    }
    public void reset() {
        this.isTriggered = false;
    }
}