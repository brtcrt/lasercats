package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.Player;
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
            Player p1 = (Player) gameObjects.get(0);
            Player p2 = (Player) gameObjects.get(1);
            gameObjects.clear();
            physicsObjects.clear();
            gameObjects.add(p1);
            gameObjects.add(p2);
            physicsObjects.add(p1);
            physicsObjects.add(p2);
            client.gameObjects = gameObjects;
            client.physicsObjects = physicsObjects;
			game.setScreen(new Level3(game, client, lobby));
		}
    }
}
