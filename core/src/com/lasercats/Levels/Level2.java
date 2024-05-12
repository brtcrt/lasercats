package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;

public class Level2 extends Level {
    public Level2(Game game, Client client) {
        super(game, client);
        LevelEditor.loadFromFile("levels/level2.json", gameObjects, physicsObjects);
    } 
}
