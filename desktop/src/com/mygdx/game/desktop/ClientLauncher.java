package com.mygdx.game.desktop;



import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.NGame.NGame;
import com.mygdx.game.CoreGame;
import com.mygdx.game.GameClient;
import com.mygdx.screen.*;


public class ClientLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		new LwjglApplication(new ClientWrapper(), config);
		//new LwjglApplication(new TestGame(), config);
		//new LwjglApplication(new NGame(), config);
		//new LwjglApplication(new GameAlgo(), config);

		//new LwjglApplication(new NGame(), config);
		//new LwjglApplication(new StartGame(), config);
		 

		
	//config.fullscreen = true;
	//config.vSyncEnabled = true;
	}
}
