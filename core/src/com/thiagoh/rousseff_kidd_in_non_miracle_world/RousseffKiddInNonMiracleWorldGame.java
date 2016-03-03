package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RousseffKiddInNonMiracleWorldGame extends Game {

    private final AssetManager assetManager;

    public RousseffKiddInNonMiracleWorldGame() {
        assetManager = new AssetManager();
    }

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
