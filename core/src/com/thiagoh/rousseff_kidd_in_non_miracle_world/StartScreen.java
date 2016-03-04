package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 03/03/16.
 */
public class StartScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640.0f;
    private static final float WORLD_HEIGHT = 480.0f;
    private final RousseffKiddInNonMiracleWorldGame game;
    private final ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Viewport viewport;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private float dilmaX;
    private float dilmaY;
    private float mapWidth;
    private Vector2 velocity;
    private Vector2 gravity;

    public StartScreen(RousseffKiddInNonMiracleWorldGame game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {

        AssetManager assetManager = game.getAssetManager();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        tiledMap = assetManager.get("tiles.tmx");

        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView(camera);

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        mapWidth = mapLayer.getWidth() * mapLayer.getTileWidth();

        velocity = new Vector2(0.0f, 0.0f);
        gravity = new Vector2(0.0f, -20.0f);
        dilmaX = 30.0f;
        dilmaY = 64.0f;

        restartGame();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        move(delta);

        clearScreen();
        updateCamera();
        draw();
    }

    private void updateCamera() {

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        if (dilmaX >= WORLD_WIDTH / 2 && dilmaX <= mapWidth - (WORLD_WIDTH / 2)) {

            camera.position.set(dilmaX, camera.position.y, camera.position.z);
            camera.update();

            orthogonalTiledMapRenderer.setView(camera);
        }
    }

    private void move(float delta) {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean space = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (left) {
            dilmaX = dilmaX - (5.0f * 32 * delta);
        } else if (right) {
            dilmaX = dilmaX + (5.0f * 32 * delta);
        }

        if (space && velocity.y == 0) {
            velocity.y = 10.0f;
        }

        dilmaY = dilmaY + (velocity.y * 32 * delta);

        if (dilmaY > 64) {
            velocity = velocity.mulAdd(gravity, delta);
        } else if (dilmaY <= 64) {
            velocity.y = 0;
        }

//        Gdx.app.log("StartScreen", String.format("dilmaX %.2f ", dilmaX));
//        Gdx.app.log("StartScreen", String.format("dilmaY %.2f velocity %.2f ", dilmaY, velocity.y));

        if (dilmaX < 0) {
            dilmaX = 0;
        } else if (dilmaX > mapWidth) {
            dilmaX = mapWidth;
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

        final float height = 32;

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(dilmaX, dilmaY + height, 20, height);
        shapeRenderer.end();

        orthogonalTiledMapRenderer.render();
    }

    private void restartGame() {

    }
}
