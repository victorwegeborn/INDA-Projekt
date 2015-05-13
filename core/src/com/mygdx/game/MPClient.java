package com.mygdx.game;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.Network.*;

public class MPClient {
	
	Client client;
	
	
	public MPClient() {
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new ClientListener()));
		
		// LÄGG IN ERA EGNA LOKALA IPN HÄR
		try {
		
			client.connect(5000, "37.123.160.23", Network.PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		Example e = new Example();
		
		client.sendTCP(e); //TESTAR SKICKA PACKET
		
	}
	
	public static void main (String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		new MPClient();
	}
	
}
