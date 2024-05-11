package com.lasercats.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.lasercats.Screens.OptionsScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player extends Empty implements PhysicsObject {

    protected Texture animationSheet;
    protected Sprite sprite;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> transitionAnimation;
    protected Animation<TextureRegion> sleepAnimation;
    protected Animation<TextureRegion> currentAnimation;
    private boolean inTransition;
    private long transitionBeginTime;
    private float transitionState;
    protected boolean isSleeping;
    protected long lastInputTime;
    protected Sound meow;
    protected Sound meowOrange;
    private boolean isMainPlayer;
    private boolean isMeow = false;
    private boolean isMeowOrange = false;
    private boolean isReflective = false;

    public Laser laser;

    protected final float walkSpeed = 150f;
    protected final float idlePeriod = 0.5f;
    protected final float walkPeriod = 0.14f;
    private final static float WIDTH = 128 , HEIGHT = 128;
    protected float stateTime;
    public Vector2 direction;
    public static int[] controlScheme;

    private float sfxVolume;

    public Player (float x, float y, float width, float height, boolean isMainPlayer) {
        super(x, y, width - 20, height - 52);
        this.isMainPlayer = isMainPlayer;
        this.isSleeping = true;
        this.inTransition = false;
        lastInputTime = TimeUtils.nanoTime();

        animationSheet = new Texture(Gdx.files.internal("CatAnimationSheet.png"));

        TextureRegion[][] tmp = TextureRegion.split(animationSheet, 32, 32);
        TextureRegion[] idleFrames = new TextureRegion[2];
        TextureRegion[] walkFrames = new TextureRegion[2];
        TextureRegion[] sleepFrames = new TextureRegion[2];
        TextureRegion[] transitionFrames = new TextureRegion[4];
        TextureRegion[] reverseTransitionFrames = new TextureRegion[4];

        walkFrames[0] = tmp[1][0];
        walkFrames[1] = tmp[1][1];

        idleFrames[0] = tmp[0][0];
        idleFrames[1] = tmp[0][1];

        sleepFrames[0] = tmp[2][3];
        sleepFrames[1] = tmp[3][3];


        // walking to sleeping
        transitionFrames[0] = tmp[2][0];
        transitionFrames[1] = tmp[2][1];
        transitionFrames[2] = tmp[2][2];
        transitionFrames[3] = tmp[2][3];

        idleAnimation = new Animation<TextureRegion>(idlePeriod, idleFrames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkAnimation = new Animation<TextureRegion>(walkPeriod, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        transitionAnimation = new Animation<TextureRegion>(0.2f, transitionFrames);
        sleepAnimation = new Animation<TextureRegion>(idlePeriod, sleepFrames);
        sleepAnimation.setPlayMode(Animation.PlayMode.LOOP);
        currentAnimation = sleepAnimation;

        direction = new Vector2();
        stateTime = 0;

        transitionState = 0;


        controlScheme = new int[]{Input.Keys.W, Input.Keys.S, Input.Keys.D, Input.Keys.A, Input.Keys.SPACE, Input.Keys.E, Input.Keys.M};


        meow = Gdx.audio.newSound(Gdx.files.internal("sounds/Meow1.mp3"));
        meowOrange = Gdx.audio.newSound(Gdx.files.internal("sounds/Meow(turuncu).mp3"));
        sfxVolume = 0;
    }

    public void addLaser(CatLaser laser)
    {
        this.laser = laser;
    }

    public void process()
    {

        // Animation
        stateTime += Gdx.graphics.getDeltaTime();

        if (isSleeping) {
            currentAnimation = sleepAnimation;
        } else if (is_walking()) {
            currentAnimation = walkAnimation;
        } else {
            currentAnimation = idleAnimation;
        }

        if (TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(lastInputTime)) > 5000 && !isSleeping && !inTransition) {
            isSleeping = true;
            inTransition = true;
            transitionBeginTime = TimeUtils.nanoTime();
            transitionState = 0;
        }

        if (TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(lastInputTime)) < 5000 && isSleeping && !inTransition) {
            isSleeping = false;
            inTransition = true;
            transitionBeginTime = TimeUtils.nanoTime();
            transitionState = 0;
        }

        sprite = new Sprite(currentAnimation.getKeyFrame(stateTime));

        if (inTransition) {
            if (isSleeping) {
                transitionAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                currentAnimation = transitionAnimation;
            } else {
                transitionAnimation.setPlayMode(Animation.PlayMode.REVERSED);
                currentAnimation = transitionAnimation;
            }
            inTransition = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(transitionBeginTime)) < currentAnimation.getAnimationDuration()* 1000;
            sprite = new Sprite(currentAnimation.getKeyFrame(transitionState));
            transitionState += Gdx.graphics.getDeltaTime();
        }


        if(direction.x > 0) {
            sprite.flip(true, false);
        }


        sfxVolume = OptionsScreen.getSFXVolume();

        if (Gdx.input.isKeyJustPressed(controlScheme[6]))
        {
            isMeow = true;
            int rand = (int)Math.round(Math.random() * 3);
            if(rand == 1){
                isMeowOrange = true;
            }
            else{
                isMeowOrange = false;
            }
        }

        if(isMeow){
            if(!isMeowOrange){
                meow.play(sfxVolume / 100);
            }
            else{
                meowOrange.play(sfxVolume / 100);
            }
        }

        if (Gdx.input.isKeyJustPressed(controlScheme[5]))
        {
            if (isMainPlayer) laser.rotateRight();
        }
    }

    public void render(SpriteBatch batch)
    {
        batch.setColor(OptionsScreen.getSelectedColor());
        batch.draw(sprite, x - 10, y , WIDTH, HEIGHT);
        batch.setColor(Color.WHITE);
    }

    public void move()
    {
        if (!velocity.isZero()) {
            lastInputTime = TimeUtils.nanoTime();
        }
        if (inTransition) return;
        x += velocity.x * walkSpeed * Gdx.graphics.getDeltaTime();
        y += velocity.y * walkSpeed * Gdx.graphics.getDeltaTime();
    }

    public JSONObject getIdentifiers()
    {
        // Fuck libgdx's json library I'm changing it to org.json ~brtcrt
        JSONObject json = new JSONObject();
        try {
            json.put("type", this.getClass().getName());
            json.put("direction.x", direction.x);
            json.put("direction.y", direction.y);
            json.put("velocity.x", velocity.x);
            json.put("velocity.y", velocity.y);
            json.put("x", x);
            json.put("y", y);
            json.put("meow", isMeow);
            json.put("meowOrange", isMeowOrange);
            json.put("id", getID());
            isMeow = false;
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json)
    {
        try {
            direction.x = (float)json.getDouble("direction.x");
            direction.y = (float)json.getDouble("direction.y");
            velocity.x = (float)json.getDouble("velocity.x");
            velocity.y = (float)json.getDouble("velocity.y");
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
            isMeow = (boolean)json.getBoolean("meow");
            isMeowOrange = (boolean)json.getBoolean("meowOrange");
            this.ID = json.getString("id");
        } catch (JSONException e) {
            System.out.println(e);
        }

    }

    public boolean is_walking()
    {
        return !velocity.isZero();
    }


    public void destroy()
    {
        animationSheet.dispose();
        meow.dispose();
    }
    public static void setControlScheme(int[] keybinds) {
        controlScheme = keybinds;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {
        // Movement
        if (isMainPlayer)
        {
            velocity.x = 0;
            velocity.y = 0;
            if (Gdx.input.isKeyPressed(controlScheme[2])) {
                direction.x = 1;
                velocity.x = 1;
            } if (Gdx.input.isKeyPressed(controlScheme[3])) {
                direction.x = -1;
                velocity.x = -1;
            } if (Gdx.input.isKeyPressed(controlScheme[0])) {
                velocity.y = 1;
            } if (Gdx.input.isKeyPressed(controlScheme[1])) {
                velocity.y = -1;
            }

        }
        for (PhysicsObject o : objects) {
            if (o.getCollider().overlaps(this) && o.isStatic() && o.canCollide()) {
                Vector2 center = o.getCollider().getCenter(new Vector2(o.getX(), o.getY()));
                Vector2 moveVector = this.getCenter(new Vector2(x, y)).sub(center);
                moveVector.nor();
                velocity.x += moveVector.x;
                velocity.y += moveVector.y;
            }
        }
        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            velocity.y = 0;
        } else if (Math.abs(velocity.x) < Math.abs(velocity.y)) {
            velocity.x = 0;
        }
        velocity.nor();
        move();
    }

    public boolean getIsReflective(){
        return this.isReflective;
    }

    public void setIsReflective(boolean b){
        this.isReflective = b;
    }
}
