package com.mygdx.game;

import java.io.IOException;
import java.util.HashSet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

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
		
		server.addListener(new ServerListener());
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
