package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;

public class Level3 extends Level {
    public Level3(Game game, Client client) {
        super(game, client);
        LevelEditor.loadFromFile("levels/level3.json", gameObjects, physicsObjects);
    } 
}
