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
	private static final int MAX_CLIENTS = 4;
	private static int currentConnectedClients = 0;
	
	
	
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
				
				//All connections are MPClients, cast to MPClient
				ClientConnection connection = (ClientConnection)c;
				MPClient client = connection.client;
				
				
				
				
				if(o instanceof Login) {
					
					System.out.println("GOT LOGIN PACKAGE");
					
					// If player is already in game, reject.
					if(client != null)
						return;
					
					//Reject if name is invalid. ( REGEX?? )
					//Close connection and return.
					String name = ((Login) o).name;
					if(!validName(name)) {
						c.close();
						return;
					}
					
					// If somehow tries to reconnect, reject.
					for(MPClient connected : clients) {
						if(connected.name.equals(name)){
							c.close();
							return;
						}
					}
					
					LogClient(connection, client);
					return;
				}
				
			}
			
			private boolean validName(String name) {
				if(name == null)
					return false;
				if(name.length() == 0)
					return false;
				return true;
			}
		});
		
			
		
		server.bind(Network.PORT);
		server.start();
	}
	
	
	void LogClient(ClientConnection c, MPClient client) {
		c.client = client;
		
		//Add connected clients to new connection
		for(MPClient mpclient : clients) {
			AddClient addClient = new AddClient();
			addClient.client = mpclient;
			c.sendTCP(addClient);
		}
		
		//Add client to HashSet
		clients.add(client);
		
		
		// Add all clients to connections
		AddClient addClient = new AddClient();
		addClient.client = client;
		server.sendToAllTCP(addClient);
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
