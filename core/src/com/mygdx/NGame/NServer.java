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
import com.mygdx.gameRefactor.GameAlgo;
import com.mygdx.gameRefactor.GameRefactor;

/**
 * NServer
 * 
 * @author victorwegeborn
 *
 */

public class NServer {
	
	private Server server;
	
	private boolean gameIsRunning = true;
	
	public int currentPlayers = 0;
	
	
	// logged clients
	private HashSet<NPlayer> players = new HashSet<NPlayer>();
	

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
					
					switch(((MovePlayer) o).direction) {
					case Input.Keys.A: System.out.println("[SERVER] CALCULATE TO MOVE LEFT");
						break;
					case Input.Keys.D: System.out.println("[SERVER] CALCULATE TO MOVE RIGHT");
						break;
					case Input.Keys.W: System.out.println("[SERVER] CALCULATE TO MOVE UP");
						break;
					case Input.Keys.S: System.out.println("[SERVER] CALCULATE TO MOVE DOWN");
						break;
					case Input.Keys.SPACE: System.out.println("[SERVER] DROP BOMB");
					break;
					}
					
					if(((MovePlayer) o).bomb == Input.Keys.SPACE)
						System.out.println("PLACE BOMB");
					
					
					return;
				}
			}
		});
		
		server.bind(54555);
		server.start();
		
	
		
		
	}
	
	// This holds per connection state.
	static class NPlayerConnection extends Connection {
		public NPlayer player;
	}
	
}