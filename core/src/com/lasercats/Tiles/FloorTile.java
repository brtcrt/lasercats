package com.lasercats.Tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FloorTile extends Rectangle implements Tile {
    private TextureRegion[][] dungeonTextures;
    private Texture map;
    private TextureRegion[] textures;
    private Sprite sprite;

    public FloorTile(float x, float y, float width, float height, int type){
        super(x, y, width, height);
        map = new Texture(Gdx.files.internal("Dungeon_Tileset.png"));
        dungeonTextures = TextureRegion.split(map, 16,16);
        textures = new TextureRegion[12];
        textures[0] = dungeonTextures[0][6];
        textures[1] = dungeonTextures[0][7];
        textures[2] = dungeonTextures[0][8];
        textures[3] = dungeonTextures[0][9];
        textures[4] = dungeonTextures[1][6];
        textures[5] = dungeonTextures[1][7];
        textures[6] = dungeonTextures[1][8];
        textures[7] = dungeonTextures[1][9];
        textures[8] = dungeonTextures[2][6];
        textures[9] = dungeonTextures[2][7];
        textures[10] = dungeonTextures[2][8];
        textures[11] = dungeonTextures[2][9];
//        textures[0] =  new Texture(Gdx.files.internal("w1.png"));
//        textures[1] =  new Texture(Gdx.files.internal("w2.png"));
//        textures[2] =  new Texture(Gdx.files.internal("w3.png"));
//        textures[3] =  new Texture(Gdx.files.internal("w4.png"));
//        textures[4] =  new Texture(Gdx.files.internal("w5.png"));
        sprite = new Sprite(textures[type - 1]);
    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, x, y , width, height);
    }

    public void destroy(){
        map.dispose();
    }

}
