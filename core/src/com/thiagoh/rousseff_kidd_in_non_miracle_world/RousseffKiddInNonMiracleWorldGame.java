package com.thiagoh.rousseff_kidd_in_non_miracle_world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

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
