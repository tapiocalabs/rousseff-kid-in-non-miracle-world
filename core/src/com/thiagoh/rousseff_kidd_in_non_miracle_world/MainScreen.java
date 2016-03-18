package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 03/03/16.
 */
public class MainScreen extends ScreenAdapter {

	public static final float WORLD_WIDTH = 20.0f;
	public static final float WORLD_HEIGHT = 15.0f;

	private static final float CAMERA_HEIGHT_FROM_DILMA_Y = 4.5f;

	private final RousseffKiddInNonMiracleWorldGame game;
	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	TiledMap map;
	MapLayer positionLayer;
	MapLayer ceilLayer;
	MapLayer collisionLayer;
	MapLayer laddersLayer;
	MapLayer collectablesLayer;
	TiledMapTileLayer backgroundLayer;
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private Viewport viewport;
	private int fps;
	private Dilma dilma;
	private float scaleUnit = 1f / 32f;

	public MainScreen(RousseffKiddInNonMiracleWorldGame rousseffKiddInNonMiracleWorldGame) {
		game = rousseffKiddInNonMiracleWorldGame;
	}

	@Override
	public void show() {

		AssetManager assetManager = game.getAssetManager();

		map = assetManager.get("tiles.tmx");

		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT * 2);
		camera.update();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.apply(true);

		positionLayer = map.getLayers().get("position");
		ceilLayer = map.getLayers().get("ceils");
		collisionLayer = map.getLayers().get("collision");
		collectablesLayer = map.getLayers().get("collectables");
		laddersLayer = map.getLayers().get("ladders");
		backgroundLayer = (TiledMapTileLayer) map.getLayers().get("background");

		ShapeUtil.scale(positionLayer, scaleUnit);
		ShapeUtil.scale(ceilLayer, scaleUnit);
		ShapeUtil.scale(laddersLayer, scaleUnit);
		ShapeUtil.scale(collisionLayer, scaleUnit);
		ShapeUtil.scale(backgroundLayer, scaleUnit);

		dilma = new Dilma(this, 0, 0);
		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(map, scaleUnit, batch);

		restartGame();
	}

	private void restartGame() {
		RectangleMapObject initialPosition = (RectangleMapObject) positionLayer.getObjects().get("initial");
		Rectangle rectangle = initialPosition.getRectangle();

		dilma.bounds.x = rectangle.x;
		dilma.bounds.y = rectangle.y;

		float positionY = calculateCameraY();
		float positionX = calculateCameraX();

		camera.position.set(positionX, positionY, camera.position.z);
		camera.update();

		orthogonalTiledMapRenderer.setView(camera);

		Gdx.app.log("MainScreen", String.format("camera.position (%.2f,%.2f)", camera.position.x, camera.position.y));
	}

	private float calculateCameraX() {

		return MathUtils.clamp(dilma.bounds.x, WORLD_WIDTH / 2, backgroundLayer.getWidth() - (WORLD_WIDTH / 2));
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		fps = Gdx.graphics.getFramesPerSecond();

		if (fps < 50) {
			Gdx.app.log("MainScreen render", String.format("fps %d", fps));
		}

		dilma.update(delta);

		clearScreen();
		updateCamera();
		draw();
	}

	private void updateCamera() {

		float cameraX = camera.position.x;
		float cameraY = camera.position.y;

		updateCameraX();
		updateCameraY();

		if (cameraX != camera.position.x || cameraY != camera.position.y) {
			Gdx.app.log("MainScreen", String.format("camera.position (%.2f,%.2f)", camera.position.x, camera.position.y));
		}
	}

	private void updateCameraX() {

		boolean updateX = dilma.bounds.x >= WORLD_WIDTH / 2 && dilma.bounds.x <= backgroundLayer.getWidth() - (WORLD_WIDTH / 2);

		if (updateX) {

			float positionX = calculateCameraX();

			camera.position.set(positionX, camera.position.y, camera.position.z);
			camera.update();

			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private float calculateCameraY() {

		Rectangle bandRectangle = null;

		for (MapObject object : ceilLayer.getObjects()) {

			Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

			if (dilma.bounds.y >= rectangle.y && dilma.bounds.y <= rectangle.y + rectangle.height ||
					dilma.bounds.y + dilma.bounds.height >= rectangle.y && dilma.bounds.y + dilma.bounds.height <= rectangle.y + rectangle.height) {
				bandRectangle = rectangle;
				break;
			}
		}

		if (bandRectangle != null) {
			return bandRectangle.y + CAMERA_HEIGHT_FROM_DILMA_Y;
		} else {
			return dilma.bounds.y + CAMERA_HEIGHT_FROM_DILMA_Y;
		}
	}

	private void updateCameraY() {

		float positionY = calculateCameraY();

		if (!MathUtils.isEqual(camera.position.y, positionY, 0.5f)) {

			camera.position.set(camera.position.x, positionY, camera.position.z);
			camera.update();
			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private void clearScreen() {
		Gdx.gl.glClearColor(0.4f, 0.4f, 1f, 1f);
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
}
