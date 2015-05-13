package com.mygdx.game;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;

public class MPClient {
	
	Client client;
	
	
	public MPClient() {
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new ClientListener()));
		
		try {
			client.connect(5000, "10.0.1.2", Network.PORT);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		new MPClient();
	}
	
}
