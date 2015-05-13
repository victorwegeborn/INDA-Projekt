package com.mygdx.game;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.Network.*;

public class MPClient {
	
	Client client;
	String name;
	
	
	public MPClient(String name) {
		client = new Client();
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new ClientListener()));
		
		// LÄGG IN ERA EGNA LOKALA IPN HÄR
		try {
		
			client.connect(5000, "10.0.1.201.", Network.PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		Login e = new Login();
		e.name = name;
		this.name = name;
		
		client.sendTCP(e); 
		
	}
	
	public static void main (String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		new MPClient("Victor");
	}
	
}
