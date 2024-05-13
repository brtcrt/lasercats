package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.LobbyScreen;

public class Level1 extends Level {
    public Level1(Game game, Client client, LobbyScreen lobbyScreen) {
        super(game, client, lobbyScreen);
        LevelEditor.loadFromFile("levels/1.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
            for (int i = 3; i < client.gameObjects.size(); i++) {
                client.gameObjects.remove(i);
            }
            for (int i = 2; i < physicsObjects.size(); i++) {
                client.physicsObjects.remove(i);
            }
			game.setScreen(new Level2(game, client, lobby));
		}
    }
}
