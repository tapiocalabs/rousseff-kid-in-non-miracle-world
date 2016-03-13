package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by thiago on 02/03/16.
 */
public class LoadingScreen extends ScreenAdapter {

   private static final float WORLD_WIDTH = 640.0f;
   private static final float WORLD_HEIGHT = 480.0f;
   private static final float PROGRESS_BAR_HEIGHT = 25.0f;
   private static final float PROGRESS_BAR_WIDTH = 200;
   private final RousseffKiddInNonMiracleWorldGame game;
   private final ShapeRenderer shapeRenderer;
   private float progress;
   private OrthographicCamera camera;
   private Viewport viewport;

   public LoadingScreen(RousseffKiddInNonMiracleWorldGame game) {
      this.game = game;
      this.progress = 0.0f;
      shapeRenderer = new ShapeRenderer();
   }

   @Override
   public void show() {
      super.show();

      camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
      camera.update();

      viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

      AssetManager assetManager = game.getAssetManager();

      assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

      assetManager.load("tiles.tmx", TiledMap.class);

      assetManager.finishLoading();
   }

   @Override
   public void dispose() {
      shapeRenderer.dispose();
   }

   private void clearScreen() {
      Gdx.gl.glClearColor(0f, 0f, 0f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
   }

   private void update() {
      AssetManager assetManager = game.getAssetManager();

      if (assetManager.update()) {
         game.setScreen(new MainScreen(game));
      } else {
         progress = assetManager.getProgress();
      }
   }

   @Override
   public void render(float delta) {
      super.render(delta);

      update();
      clearScreen();
      draw();
   }

   private void draw() {

      shapeRenderer.setProjectionMatrix(camera.projection);
      shapeRenderer.setTransformMatrix(camera.view);

      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(Color.WHITE);

      shapeRenderer.rect((WORLD_WIDTH / 2) - (PROGRESS_BAR_WIDTH / 2), WORLD_HEIGHT / 2, PROGRESS_BAR_WIDTH * progress, PROGRESS_BAR_HEIGHT);
      shapeRenderer.end();
   }
}
