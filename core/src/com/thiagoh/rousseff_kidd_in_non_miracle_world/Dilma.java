package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
    final Rectangle bounds = new Rectangle(0, 0, DILMA_WIDTH, DILMA_HEIGHT);
    private final ShapeRenderer shapeRenderer;
    private final TiledMap tiledMap;
    private final Polygon dilmaPolygon = new Polygon(new float[]{0, 0, DILMA_WIDTH, 0, DILMA_WIDTH, DILMA_HEIGHT, 0, DILMA_HEIGHT});
    Vector2 velocity;
    Vector2 gravity;
    private int jump = 0;
    private boolean landed = true;
    private boolean canIncJump = false;
    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    public Dilma(float x, float y, TiledMap tiledMap, ShapeRenderer renderer) {

        this.tiledMap = tiledMap;
        bounds.x = x;
        bounds.y = y;

        shapeRenderer = renderer;
        velocity = new Vector2(0.0f, 0.0f);
        gravity = new Vector2(0.0f, -20.0f);

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

    private void handleCollision(float delta) {

        MapLayer mapLayer = tiledMap.getLayers().get("collision");
        MapObjects objects = mapLayer.getObjects();

        dilmaPolygon.setPosition(bounds.x, bounds.y);

        Polygon tmp = new Polygon();

        for (int i = 0; i < objects.getCount(); i++) {
            MapObject object = objects.get(i);

            Polygon objectPolygon = null;

            if (object instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) object;
                objectPolygon = polygonMapObject.getPolygon();

//                Gdx.app.log("Dilma", String.format("PolygonMapObject x,y (%.2f,%.2f) width,height (%.2f,%.2f)", objectPolygon.getX(), objectPolygon.getY(), objectPolygon.getBoundingRectangle().width, objectPolygon.getBoundingRectangle().height));
            } else if (object instanceof RectangleMapObject) {

                RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
                Rectangle rectangle = rectangleMapObject.getRectangle();

                objectPolygon = tmp;
                objectPolygon.setVertices(new float[]{0, 0, rectangle.width, 0, rectangle.width, rectangle.height, 0, rectangle.height});
                objectPolygon.setPosition(rectangle.x, rectangle.y);

//                Gdx.app.log("Dilma", String.format("RectangleMapObject x,y (%.2f,%.2f) width,height (%.2f,%.2f)", bounds.x, bounds.y, bounds.width, bounds.height));
            }

            if (objectPolygon == null) {
                continue;
            }

            boolean overlaps = Intersector.overlapConvexPolygons(dilmaPolygon, objectPolygon, mtv);

            if (overlaps) {
                Gdx.app.log("Dilma", String.format("Object %d is overlapping by (x,y) (%.2f,%.2f,%.2f) ", i, mtv.normal.x, mtv.normal.y, mtv.depth));

                bounds.x += mtv.normal.x * mtv.depth;
                bounds.y += mtv.normal.y * mtv.depth;

                if (Math.abs(mtv.normal.x) > 0 && mtv.normal.y == 0) {
                    velocity.x = 0;
                }

                if (Math.abs(mtv.normal.y) > 0) {
                    jump = 0;
                    landed = true;
                    velocity.y = 0;
                    canIncJump = false;
                }
            }

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

//        Gdx.app.log("Dilma", String.format("\n\n"));
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
        boolean actionJump = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP);

        if (actionJump) {
            if (jump == 0 && velocity.y >= 0) {
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
        } else if (!actionJump && jump > 0) {
            canIncJump = false;
        }

        velocity.mulAdd(gravity, delta);

        if (left || right) {
            if (velocity.x <= 1f && velocity.x >= -1f) {
                velocity.x = (left ? -1f : 1f) * INITIAL_X_VELOCITY;
            } else if (left && velocity.x > 0 || right && velocity.x < 0) {
                velocity.x += (velocity.x > 0 ? -1f : 1f) * (MOVEMENT_RESISTENCY + INITIAL_X_VELOCITY) * delta;
            } else {
                velocity.x += (left ? -1f : 1f) * 5f * delta;
            }
        } else {
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

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        bounds.x = MathUtils.clamp(bounds.x, 0, mapLayer.getWidth() - DILMA_WIDTH);
        bounds.y = MathUtils.clamp(bounds.y, 0, StartScreen.WORLD_HEIGHT * 2f);

        Gdx.app.log("Dilma", String.format("X,Y (%.2f,%.2f) velocity (x,y) (%.2f,%.2f) ", bounds.x, bounds.y, velocity.x, velocity.y));
    }
}
