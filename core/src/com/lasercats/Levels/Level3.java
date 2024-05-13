package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;

public class Level3 extends Level {
    public Level3(Game game, Client client, MainMenuScreen menuScreen) {
        super(game, client, menuScreen);
        LevelEditor.loadFromFile("levels/level3.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    } 
}
