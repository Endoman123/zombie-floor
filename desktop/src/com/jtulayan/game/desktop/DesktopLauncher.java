package com.jtulayan.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jtulayan.main.ZombieFloor;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 600;
		config.height = 600;
		config.foregroundFPS = 60;

		new LwjglApplication(new ZombieFloor(), config);
	}
}
