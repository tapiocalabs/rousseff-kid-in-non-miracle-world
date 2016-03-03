package com.thiagoh.rousseff_kidd_in_non_miracle_world.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thiagoh.rousseff_kidd_in_non_miracle_world.RousseffKiddInNonMiracleWorldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RousseffKiddInNonMiracleWorldGame(), config);
	}
}
