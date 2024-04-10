package com.lasercats.Screens;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.Box;
import com.lasercats.GameObjects.GameObject;
import com.lasercats.GameObjects.Wall;

public class LevelScreen extends LaserCatsScreen {

    private ArrayList<GameObject> gameObjects;
	private ArrayList<GameObject> renderQueue;
	private Client client;
	private JSONObject dataToServer;

    public LevelScreen(Game game, Client client) {
        super(game);
        this.client = client;
        this.genericViewport = new ExtendViewport(1024, 720, camera);
        this.genericViewport.apply();
        this.stage = new Stage(genericViewport, batch);
        this.camera.setToOrtho(false, this.genericViewport.getScreenWidth(), this.genericViewport.getScreenHeight());
        //TODO Placeholder JSON. Change later.
        this.skin = new Skin(Gdx.files.internal("clean-crispy/skin/clean-crispy-ui.json"));
        this.root.setFillParent(true);
        gameObjects = client.getGameObjects();
		//By the way we really shouldn't position objects with manually entered coordinates, this works terribly with different aspect ratios.
        createBoxes();
		gameObjects.add(new Wall(600, 600, 128, 32));
		gameObjects.add(new Wall(472, 600, 128, 32));
		gameObjects.add(new Wall(728, 472, 32, 128));
		gameObjects.add(new Wall(728, 344, 32, 128));

        renderQueue = new ArrayList<GameObject>(gameObjects);
		dataToServer = new JSONObject();
    }
    @Override
    public void render(float delta) {
		
        Gdx.input.setInputProcessor(stage);
        this.stage.act(delta);
        this.batch.setProjectionMatrix(this.genericViewport.getCamera().combined);
        this.stage.draw();

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

			batch.begin();

			renderQueue = new ArrayList<GameObject>(gameObjects);
			ySort();

			for (GameObject object : renderQueue)
			{
				object.render(batch);
			}
			batch.end();	
    }
    @Override
    public void dispose() {
        for (GameObject object : gameObjects)
		{
			object.destroy();
		}
        batch.dispose();
		stage.dispose();
		client.close();
    }
    @Override
    public void pause() {}
    @Override
    public void createActors() {}
    @Override
    public void setListeners() {}
    @Override
    public void positionActors() {}
    @Override
    public void resume() {}
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
}
