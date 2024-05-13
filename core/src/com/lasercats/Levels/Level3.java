package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;

public class Level3 extends Level {

    public Level3(Game game, Client client, MainMenuScreen menuScreen) {
        super(game, client, menuScreen);
        for (int i = 3; i < gameObjects.size(); i++) {
            gameObjects.remove(i);
        }
        for (int i = 3; i < physicsObjects.size(); i++) {
            physicsObjects.remove(i);
        }
        LevelEditor.loadFromFile("levels/3.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
			game.setScreen(menu);
		}
    }
}
