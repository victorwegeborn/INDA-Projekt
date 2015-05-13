package com.mygdx.game;

import java.io.IOException;
import java.util.HashSet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.Network.*;

/**
 * MPServer - Multiplayer Server
 * 
 * This server should be instantieted on the hosts machine.
 * 
 * 
 * @author victorwegeborn
 *
 */

public class MPServer {
	
	private Server server;
	
	// logged clients
	private HashSet<MPClient> clients = new HashSet<MPClient>();
	
	//=== GAME SPECS ===
	private static final int MAX_PLAYERS = 4;
	
	
	
	// Holds per connection state
	private static class ClientConnection extends Connection {
		public MPClient client;
	}
	
	public MPServer() throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new ClientConnection();
			}
		};
		
		Network.register(server);
		
		
		
		server.addListener(new Listener() {
			public void connected (Connection c) {
				Log.info("[SERVER] client connecting");
			}

			
			public void disconnected (Connection c) {
				Log.info("[SERVER] client disconnecting");
			}

			
			public void received (Connection c, Object o) {
				// HERE GOES ALL INGAME LOGIC FOR SERVER TO PROCESS
				
				//TESTING RECIVE
				
				if(o instanceof Example) {
					System.out.println("[SERVER] Example package recived.");
					ExampleReturn r = new ExampleReturn();
					server.sendToAllTCP(r);
				}
				
			}
		});
		
		
		
		server.bind(Network.PORT);
		server.start();
	}
	
	public static void main(String []args) {
		try {
			new MPServer();
			Log.set(Log.LEVEL_DEBUG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


}
