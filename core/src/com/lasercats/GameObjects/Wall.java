package com.lasercats.GameObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Wall extends Empty implements PhysicsObject {
    private TextureRegion[][] dungeonTextures;
    private Texture map;
    private TextureRegion[] textures;
    private Sprite sprite;

    public Wall(float x, float y, float width, float height, int type){
        super(x, y, width, height);
        map = new Texture(Gdx.files.internal("Dungeon_Tileset.png"));
        dungeonTextures = TextureRegion.split(map, 16,16);
        velocity = new Vector2();
        textures = new TextureRegion[8];
        textures[0] = dungeonTextures[0][0];
        textures[1] = dungeonTextures[0][1];
        textures[2] = dungeonTextures[0][5];
        textures[3] = dungeonTextures[1][0];
        textures[4] = dungeonTextures[1][5];
        textures[5] = dungeonTextures[4][0];
        textures[6] = dungeonTextures[4][3];
        textures[7] = dungeonTextures[4][5];
//        textures[0] =  new Texture(Gdx.files.internal("w1.png"));
//        textures[1] =  new Texture(Gdx.files.internal("w2.png"));
//        textures[2] =  new Texture(Gdx.files.internal("w3.png"));
//        textures[3] =  new Texture(Gdx.files.internal("w4.png"));
//        textures[4] =  new Texture(Gdx.files.internal("w5.png"));
        sprite = new Sprite(textures[type - 1]);
    }

    public void process(){

    }

    @Override
    public void calculatePhysics(ArrayList<PhysicsObject> objects) {

    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y , width, height);
    }

    public void destroy(){
        map.dispose();
    }


    public JSONObject getIdentifiers(){
        JSONObject json = new JSONObject();
        try {
            json.put("x", x);
            json.put("y", y);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return json;
    }

    public void setIdentifiers(JSONObject json){
        try {
            x = (float)json.getDouble("x");
            y = (float)json.getDouble("y");
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean canCollide() {
        return true;
    }
}

