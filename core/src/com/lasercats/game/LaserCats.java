package com.lasercats.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.*;
import com.lasercats.Screens.MainMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaserCats extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private ArrayList<GameObject> gameObjects;
	private ArrayList<GameObject> renderQueue;
	private Client client;
	private MainMenu menu;
	private Player cat; // TODO move this down to create() later probably ~brtcrt
	private PlayerNonMain otherCat;
	private long roomUpdateTime;
	private JSONObject dataToServer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 720);
		this.cat = new Player(32, 32, 128, 128);
		this.otherCat = new PlayerNonMain(-300, -300, 128, 128);
		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(cat);
		gameObjects.add(otherCat);
		createBoxes();
		// gameObjects.add(new Box(400, 400, cat, otherCat));
		renderQueue = new ArrayList<GameObject>(gameObjects);
		client = new Client(gameObjects);
		menu = new MainMenu(client);
		dataToServer = new JSONObject();
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
			ArrayList<JSONObject> identifiers = new ArrayList<JSONObject>();
			for (GameObject object : gameObjects)
			{
				object.process();
				identifiers.add(object.getIdentifiers());
			}

			createDataJSON(identifiers);
			client.sendUpdate(dataToServer);

			ScreenUtils.clear(1, 1, 1, 1);
			camera.update();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			renderQueue = new ArrayList<GameObject>(gameObjects);
			ySort();

			for (GameObject object : renderQueue)
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
		renderQueue.sort((o1, o2) -> {
			return -1 * Float.compare(o1.getY(), o2.getY());
		});
	}

	private void createDataJSON(ArrayList<JSONObject> identifiers) {
		try {
			dataToServer.put("gameObjects", identifiers);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}

	/**
	 * For testing purposes. Remove later ~brtcrt
	 */
	private void createBoxes() {
		final int BOX_COUNT = 3;
		for (int i = 0; i < BOX_COUNT; i++) {
			Box b;
			boolean goodBox;
			do {
				goodBox = true;
				b = new Box(40 + (int)Math.round(Math.random() * 600), 40 + (int)Math.round(Math.random() * 600), gameObjects);
				for (GameObject g : gameObjects) {
					if (g.getCollider().overlaps(b)) {
						goodBox = false;
					}
				}
			} while (!goodBox);
			gameObjects.add(b);
		}
	}
}
