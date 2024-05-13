package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;

public class Level1 extends Level {
    public Level1(Game game, Client client, MainMenuScreen menuScreen) {
        super(game, client, menuScreen);
        LevelEditor.loadFromFile("levels/1.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    } 
}
