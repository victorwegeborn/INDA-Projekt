package com.mygdx.game;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.Network.*;

public class MPClient {
	
	Client client;
	String name;
	
	
	public MPClient() {
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new ClientListener()));
		
		// LÄGG IN ERA EGNA LOKALA IPN HÄR
		try {
			client.connect(5000, "192.168.0.106", Network.PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public static void main (String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		
		new MPClient();
	}
	
}
