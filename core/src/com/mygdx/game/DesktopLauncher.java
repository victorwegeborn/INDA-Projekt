package com.mygdx.game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.INDAGame;
import com.mygdx.screens.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//new LwjglApplication(new TestGame(), config);
		new LwjglApplication(new INDAGame(), config);
		
		

	   config.fullscreen = true;
	   config.vSyncEnabled = true;

		//config.fullscreen = true;
		//config.vSyncEnabled = true;

	}
}