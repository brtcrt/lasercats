package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;

public class Level2 extends Level {
    public Level2(Game game, Client client, MainMenuScreen menuScreen) {
        super(game, client, menuScreen);
        LevelEditor.loadFromFile("levels/level2.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    } 
}
