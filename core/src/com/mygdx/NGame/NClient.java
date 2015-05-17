package com.mygdx.NGame;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.NGame.NNetwork.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class NClient {
	
	Client client;
	String name;
	NPlayer player;
	
	public NClient(String ip, NPlayer player) {
		this.player = player;
		client = new Client();
		client.start();
		
		NNetwork.register(client);
		
		client.addListener(new ThreadedListener(new Listener() {
			
			public void connected (Connection c) {
			}

			public void received (Connection c, Object o) {
				if (o instanceof UpdatedPlayer) {
					
					System.out.println("SOULD UPDATE SCREEN SOMEHOW");
					
				}
			}

			
		}));
		
		// LÄGG IN ERA EGNA LOKALA IPN HÄR
		try {
			client.connect(5000, ip, NNetwork.PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public void sendInput(int direction) {
		
		MovePlayer mp = new MovePlayer();
		
		switch(direction) {
		case Input.Keys.LEFT:   mp.direction = Input.Keys.LEFT;
							    break;
		case Input.Keys.RIGHT:  mp.direction = Input.Keys.RIGHT;
	    						break;
		}
		
		client.sendTCP(mp);
	}
	
	public NPlayer updatePlayer(NPlayer player) {
		NPlayer update = new NPlayer(player.sprite, player.xPos, player.yPos);
		
		
		
		return update;
	}
	
}
