package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 03/03/16.
 */
public class StartScreen extends ScreenAdapter {

	public static final float WORLD_WIDTH = 20.0f;
	public static final float WORLD_HEIGHT = 15.0f;

	public static final float MAX_WORLD_HEIGHT = WORLD_HEIGHT * 2;

	public static final float GROUND_HEIGHT = 3.0f;
	private final RousseffKiddInNonMiracleWorldGame game;
	private final ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private Viewport viewport;
	private SpriteBatch batch;
	private TiledMap tiledMap;
	private int fps;
	private Dilma dilma;

	public StartScreen(RousseffKiddInNonMiracleWorldGame game) {
		this.game = game;
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void show() {

		AssetManager assetManager = game.getAssetManager();

		batch = new SpriteBatch();

		tiledMap = assetManager.get("tiles.tmx");


		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.apply(true);

		dilma = new Dilma(3.0f, GROUND_HEIGHT + WORLD_HEIGHT, tiledMap, shapeRenderer);

		float positionY = calculateCameraY();

		camera.position.set(camera.position.x, positionY, camera.position.z);
		camera.update();

		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / 32f, batch);
		orthogonalTiledMapRenderer.setView(camera);

		Gdx.app.log("StartScreen: updateY", String.format("camera.position.y %.2f", camera.position.y));
		restartGame();
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		fps = Gdx.graphics.getFramesPerSecond();

		if (fps < 50) {
			Gdx.app.log("StartScreen render", String.format("fps %d", fps));
		}

		dilma.update(delta);

		clearScreen();
		updateCamera();
		draw();
	}


	private void updateCamera() {

		updateCameraX();
		updateCameraY();
	}

	private void updateCameraX() {

		TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		boolean updateX = dilma.x >= WORLD_WIDTH / 2 && dilma.x <= mapLayer.getWidth() - (WORLD_WIDTH / 2);

		if (updateX) {

			camera.position.set(dilma.x, camera.position.y, camera.position.z);
			camera.update();

			Gdx.app.log("StartScreen: updateX", String.format("camera.position.y %.2f", camera.position.y));

			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private float calculateCameraY() {

		return dilma.y - StartScreen.GROUND_HEIGHT + (WORLD_HEIGHT / 2);
	}

	private void updateCameraY() {

		float positionY = calculateCameraY();
		boolean updateY = positionY >= WORLD_HEIGHT / 2 && positionY <= MAX_WORLD_HEIGHT / 2;

		if (updateY) {

			camera.position.set(camera.position.x, positionY, camera.position.z);
			camera.update();

			Gdx.app.log("StartScreen: updateY", String.format("camera.position.y %.2f", camera.position.y));

			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private void clearScreen() {
		Gdx.gl.glClearColor(0.4f, 0.4f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}

	private void draw() {

		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);

		orthogonalTiledMapRenderer.render();
		dilma.draw();
	}

	private void restartGame() {

	}
}
