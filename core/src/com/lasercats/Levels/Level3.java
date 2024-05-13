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
        LevelEditor.loadFromFile("levels/3.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
            for (int i = 3; i < client.gameObjects.size(); i++) {
                client.gameObjects.remove(i);
            }
            for (int i = 2; i < client.physicsObjects.size(); i++) {
                client.physicsObjects.remove(i);
            }
			game.setScreen(menu);
		}
    }
}
