package com.mygdx.NGame;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.NGame.NNetwork.*;

/**
 * NServer
 * 
 * @author victorwegeborn
 *
 */

public class NServer {
	
	private Server server;
	
	public int currentPlayers = 0;
	
	// logged clients
	private HashSet<NPlayer> players = new HashSet<NPlayer>();
	
	//=== GAME SPECS ===
	private static final int MAX_CLIENTS = 4;
	
	

	public NServer() throws IOException {
		
		server = new Server() {
			protected Connection newConnection () {
				// By providing our own connection implementation, we can store per
				// connection state without a connection ID to state look up.
				return new NPlayerConnection();
			}
		};
		
		//Register all packages that will be sent between server and client
		NNetwork.register(server);
		
		//Process all packages in the listener
		server.addListener(new Listener() {
			
			public void connected (Connection c) {
				Log.info("[SERVER] player connecting");
			}

			
			public void disconnected (Connection c) {
				Log.info("[SERVER] player disconnecting");

			}

			public void received (Connection c, Object o) {
				
				if(o instanceof MovePlayer) {
					
					int dir = ((MovePlayer) o).direction;
					

					
					switch(((MovePlayer) o).direction) {
					case Input.Keys.LEFT: System.out.println("[SERVER] CALCULATE TO MOVE LEFT");
						break;
					case Input.Keys.RIGHT: System.out.println("[SERVER] CALCULATE TO MOVE RIGHT");
						break;
					}
					
					
					return;
				}
			}
		});
		
		server.bind(NNetwork.PORT);
		server.start();
	}
	
	// This holds per connection state.
	static class NPlayerConnection extends Connection {
		public NPlayer player;
	}
	
}