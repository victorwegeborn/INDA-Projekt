package com.mygdx.game.desktop;



import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.NGame.NGame;
import com.mygdx.game.INDAGame;
import com.mygdx.gameRefactor.*;
import com.mygdx.screen.*;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		//new LwjglApplication(new INDAGame(), config);
		//new LwjglApplication(new TestGame(), config);
		//new LwjglApplication(new NGame(), config);
		new LwjglApplication(new StartGame(), config);

		
	//config.fullscreen = true;
	//config.vSyncEnabled = true;
	}
}
