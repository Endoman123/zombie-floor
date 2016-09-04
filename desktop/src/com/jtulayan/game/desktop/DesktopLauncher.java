package com.jtulayan.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jtulayan.main.ZombieFloor;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1280;
		config.height = 720;
		config.foregroundFPS = 60;
		config.backgroundFPS = 15;

		config.preferencesFileType = Files.FileType.Local;
		config.vSyncEnabled = true;
		config.title = "Zombie Floor";

		new LwjglApplication(new ZombieFloor(), config);
	}
}
