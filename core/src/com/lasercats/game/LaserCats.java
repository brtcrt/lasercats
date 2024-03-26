package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.Box;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Player;
import com.lasercats.GameObjects.PlayerNonMain;
import com.lasercats.Screens.MainMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ArrayList<GameObject> gameObjects;
	private Client client;
	private MainMenu menu;
	private Player cat; // TODO move this down to create() later probably ~brtcrt
	private long roomUpdateTime;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		this.cat = new Player(32, 32, 128, 80);
		PlayerNonMain otherCat = new PlayerNonMain(-300, -300, 128, 80);
		gameObjects = new ArrayList<>();
		gameObjects.add(cat);
		gameObjects.add(otherCat);
		gameObjects.add(new Box(400, 400, cat, otherCat));
		client = new Client(otherCat);
		menu = new MainMenu(client);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0);
		menu.getStage().act(Gdx.graphics.getDeltaTime());
		menu.getStage().draw();
		if (TimeUtils.nanoTime() - this.roomUpdateTime > TimeUtils.millisToNanos(1000)) {
			menu.updateRoomList();
			menu.updateCurrentRoom();
			this.roomUpdateTime = TimeUtils.nanoTime();
		}
		if (!menu.getGameModeButton().isChecked() && menu.getRoomCreateButton().isPressed()) {
			// menu.getRoomCreateButton().setDisabled(true);
			String roomName = menu.getRoomCreateField().getText();
			client.createRoom(roomName);
			Gdx.app.log("New Room Request", roomName);
		}
		if (!menu.getGameModeButton().isChecked() && !menu.getRoomClicked().isEmpty()) {
			client.joinRoom(menu.getRoomClicked());
		}
		if (menu.getGameModeButton().isChecked()) {
			menu.getGameModeButton().setDisabled(true);
			for (GameObject object : gameObjects)
			{
				object.process();
			}

			client.sendUpdate(this.cat.getIdentifiers());

			ScreenUtils.clear(1, 1, 1, 1);
			camera.update();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			ySort();
			for (GameObject object : gameObjects)
			{
				object.render(batch);
			}
			batch.end();
		}		
	}
	@Override
	public void dispose () {
		for (GameObject object : gameObjects)
		{
			object.destroy();
		}
		batch.dispose();
		client.close();
		menu.disposeMenu();
	}

	private void ySort() {
		gameObjects.sort((o1, o2) -> {
			return -1 * Float.compare(o1.getY(), o2.getY());
		});
	}
}
