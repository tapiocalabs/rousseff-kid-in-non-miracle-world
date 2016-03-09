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

    private static final float WORLD_WIDTH = 20.0f;
    private static final float WORLD_HEIGHT = 15.0f;
    private final RousseffKiddInNonMiracleWorldGame game;
    private final ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Viewport viewport;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private float mapWidth;
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
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_WIDTH);
//        camera.zoom = 1.3f;

        camera.update();

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        tiledMap = assetManager.get("tiles.tmx");

        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / 32f, batch);
        orthogonalTiledMapRenderer.setView(camera);

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        mapWidth = mapLayer.getWidth();

        dilma = new Dilma(tiledMap, shapeRenderer);
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

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        if (dilma.x >= WORLD_WIDTH / 2 && dilma.x <= mapWidth - (WORLD_WIDTH / 2)) {

            camera.position.set(dilma.x, camera.position.y, camera.position.z);
            camera.update();

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
