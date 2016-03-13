package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by thiago on 08/03/16.
 */
public class Dilma {

   private static final float DILMA_WIDTH = 0.3f;
   private static final float DILMA_HEIGHT = 1.6f;
   private static final float MAX_VELOCITY = 6.0f;
   private static final float MOVEMENT_RESISTENCY = 20.0f;
   private static final float INITIAL_X_VELOCITY = 3.0f;
   private static final float INITIAL_Y_VELOCITY = 3.0f;
   final Rectangle bounds = new Rectangle(0, 0, DILMA_WIDTH, DILMA_HEIGHT);
   private final Polygon polygon = new Polygon(new float[]{0, 0, DILMA_WIDTH, 0, DILMA_WIDTH, DILMA_HEIGHT, 0, DILMA_HEIGHT});
   private SpriteBatch batch;
   private ShapeRenderer shapeRenderer;
   private Vector2 velocity;
   private Vector2 gravity;
   private TiledMap map;
   private MapObjects collisionObjects;
   private MapObjects laddersObjects;
   private int jump = 0;
   private boolean landed = true;
   private boolean canIncJump = false;
   private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
   private Polygon tmpPolygon = new Polygon();
   private boolean holdingLadder;
   private boolean canJumpAgain;

   public Dilma(MainScreen mainScreen, float x, float y) {

      map = mainScreen.map;
      bounds.x = x;
      bounds.y = y;

      collisionObjects = mainScreen.collisionLayer.getObjects();
      laddersObjects = mainScreen.laddersLayer.getObjects();

      shapeRenderer = mainScreen.shapeRenderer;
      batch = mainScreen.batch;
      velocity = new Vector2(0.0f, 0.0f);
      gravity = new Vector2(0.0f, -20.0f);

      holdingLadder = false;
      canJumpAgain = true;

//            MapProperties properties = object.getProperties();
//
//            Iterator<String> keys = properties.getKeys();
//
//            while (keys.hasNext()) {
//
//                String key = keys.next();
//                Gdx.app.log("Dilma", String.format("Object %d Key %s Value: %s", i, key, properties.get(key)));
//            }
   }

   public void update(float delta) {

      move(delta);
      handleCollision(delta);
   }

   private boolean overlapsLadders(float delta) {

      boolean overlaps = false;

      for (int i = 0; !overlaps && i < laddersObjects.getCount(); i++) {
         MapObject object = laddersObjects.get(i);

         ShapeUtil.fillPolygon(object, tmpPolygon);

         overlaps = Intersector.overlapConvexPolygons(polygon, tmpPolygon, null);
      }

      return overlaps;
   }

   public void draw() {

      shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
      shapeRenderer.setColor(Color.RED);
      shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
      shapeRenderer.end();
   }

   private void move(float delta) {

      boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
      boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

      boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
      boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);

      velocity.mulAdd(gravity, delta);

      boolean overlapsLadders = overlapsLadders(delta);

      if (overlapsLadders) {
         if (up || down) {
            holdingLadder = true;
         }
      } else {
         holdingLadder = false;
      }

      if (holdingLadder) {

         land();

         if (up || down) {
            if (velocity.y <= 1f && velocity.y >= -1f) {
               velocity.y = (down ? -1f : 1f) * INITIAL_Y_VELOCITY;
            } else if (down && velocity.y > 0 || up && velocity.y < 0) {
               velocity.y += (velocity.y > 0 ? -1f : 1f) * (MOVEMENT_RESISTENCY + INITIAL_Y_VELOCITY) * delta;
            } else {
               velocity.y += (down ? -1f : 1f) * MOVEMENT_RESISTENCY * delta;
            }
         }
      }

      boolean jumpKeys = Gdx.input.isKeyPressed(Input.Keys.SPACE) || up;

      if (jumpKeys) {
         if (canJumpAgain && jump == 0 && landed) {
            jump = 1;
            landed = false;
            canIncJump = true;
            velocity.y = 6.0f;
         } else if (canIncJump && jump == 1 && velocity.y < 5.0f) {
            jump = 2;
            velocity.y = 7.0f;
//            } else if (canIncJump && jump == 2 && velocity.y < 5.0f) {
//                jump = 3;
//                velocity.y = 4.0f;
         }
         canJumpAgain = false;
      } else {
         canIncJump = false;
         canJumpAgain = true;
      }

      if (left || right) {
         if (velocity.x <= 1f && velocity.x >= -1f) {
            velocity.x = (left ? -1f : 1f) * INITIAL_X_VELOCITY;
         } else if (left && velocity.x > 0 || right && velocity.x < 0) {
            velocity.x += (velocity.x > 0 ? -1f : 1f) * (MOVEMENT_RESISTENCY + INITIAL_X_VELOCITY) * delta;
         } else {
            velocity.x += (left ? -1f : 1f) * 5f * delta;
         }
      } else {
         // inertia
         if (velocity.x > 1f || velocity.x < -1f) {
            velocity.x += (velocity.x > 0 ? -1f : 1f) * MOVEMENT_RESISTENCY * delta;
         } else if (velocity.x <= 1f && velocity.x >= -1f) {
            velocity.x = 0.0f;
         }
      }

      // clamp the velocity to the maximum, x-axis only
      velocity.x = MathUtils.clamp(velocity.x, -MAX_VELOCITY, MAX_VELOCITY);

      bounds.y += velocity.y * delta;
      bounds.x += velocity.x * delta;

      TiledMapTileLayer mapLayer = (TiledMapTileLayer) map.getLayers().get(0);

      bounds.x = MathUtils.clamp(bounds.x, 0, mapLayer.getWidth() - DILMA_WIDTH);
      bounds.y = MathUtils.clamp(bounds.y, 0, MainScreen.WORLD_HEIGHT * 2f);

      polygon.setPosition(bounds.x, bounds.y);

      Gdx.app.log("Dilma", String.format("X,Y (%.2f,%.2f) velocity (x,y) (%.2f,%.2f) ", bounds.x, bounds.y, velocity.x, velocity.y));
   }

   private void handleCollision(float delta) {

      landed = false;

      for (int i = 0; i < collisionObjects.getCount(); i++) {
         MapObject object = collisionObjects.get(i);

         ShapeUtil.fillPolygon(object, tmpPolygon);

         boolean overlaps = Intersector.overlapConvexPolygons(polygon, tmpPolygon, mtv);

         if (overlaps) {
            Gdx.app.log("Dilma", String.format("Object %d is overlapping by (x,y) (%.2f,%.2f,%.2f) ", i, mtv.normal.x, mtv.normal.y, mtv.depth));

            bounds.x += mtv.normal.x * mtv.depth;
            bounds.y += mtv.normal.y * mtv.depth;

            if (Math.abs(mtv.normal.x) > 0 && mtv.normal.y == 0) {
               velocity.x = 0;
            }

            if (Math.abs(mtv.normal.y) > 0) {
               land();
            }
         }
      }
   }

   private void land() {
      jump = 0;
      landed = true;
      velocity.y = 0;
      canIncJump = false;
   }
}
