package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.LobbyScreen;

public class Level2 extends Level {
    public Level2(Game game, Client client, LobbyScreen lobbyScreen) {
        super(game, client, lobbyScreen);
        LevelEditor.loadFromFile("levels/2.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
            for (int i = 3; i < gameObjects.size(); i++) {
                gameObjects.remove(i);
            }
            for (int i = 2; i < physicsObjects.size(); i++) {
                physicsObjects.remove(i);
            }
			game.setScreen(new Level3(game, client, lobby));
		}
    }
}
