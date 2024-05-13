package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.CatLaser;
import com.lasercats.GameObjects.Player;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.LobbyScreen;

public class Level3 extends Level {

    public Level3(Game game, Client client, LobbyScreen lobbyScreen) {
        super(game, client, lobbyScreen);
        LevelEditor.loadFromFile("levels/3.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
            Player p1 = (Player) gameObjects.get(0);
            Player p2 = (Player) gameObjects.get(1);
            CatLaser l = (CatLaser) gameObjects.get(2);
            gameObjects.clear();
            physicsObjects.clear();
            gameObjects.add(p1);
            gameObjects.add(p2);
            gameObjects.add(l);
            physicsObjects.add(p1);
            physicsObjects.add(p2);
            client.gameObjects = gameObjects;
            client.physicsObjects = physicsObjects;
			game.setScreen(lobby);
		}
    }
}
