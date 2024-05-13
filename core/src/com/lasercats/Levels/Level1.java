package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Gate;
import com.lasercats.GameObjects.Player;
import com.lasercats.Screens.LevelEditor;

public class Level1 extends Level {
    public Level1(Game game, Client client) {
        super(game, client);
        LevelEditor.loadFromFile("levels/1.json", gameObjects, physicsObjects);
        super.setPlayerStarts();
    } 
}
