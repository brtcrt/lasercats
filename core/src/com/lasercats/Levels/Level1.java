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
            super.displayLevelEnding();
			game.setScreen(new Level2(game, client, lobby));
		}
    }
}
