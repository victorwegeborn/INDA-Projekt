package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.NGame.NNetwork;
import com.mygdx.NGame.NNetwork.LobbyUpdate;
import com.mygdx.NGame.NNetwork.MovePlayer;
import com.mygdx.NGame.NNetwork.NPlayerConnection;
import com.mygdx.NGame.NNetwork.StartGame;
import com.mygdx.NGame.NNetwork.PlayerReady;
import com.mygdx.NGame.NNetwork.PlayerReadyFromServer;
import com.mygdx.NGame.NNetwork.PlayerEmptyFromServer;
import com.mygdx.NGame.NNetwork.PlayerConnectedFromServer;
import com.mygdx.NGame.NNetwork.RequestLobbyUpdate;
import com.mygdx.NGame.NNetwork.DropBomb;
import com.mygdx.NGame.NNetwork.GameOver;
import com.mygdx.NGame.NNetwork.BombSound;
import com.mygdx.gameMenu.GameLobby;
import com.mygdx.gameMenu.MainMenu;
import com.mygdx.screen.MainGame;

public class GameServer {
	
	private MainGame game;
	private NetworkEngine gameEngine;
	private Server server;
	private int playerCount;
	private boolean hostServer;
	private boolean gameStarted;
	private boolean[] playerReady;
	private GameLobby lobby;
	private Thread updatePhysics;
	
	private float bombTimer;
	private static float bombTime = 0.2f;
	
	public GameServer(MainGame game, GameLobby lobby) throws IOException {
		this.game = game;
		gameEngine = new NetworkEngine();
		this.lobby = lobby;
		SetupServer();
		gameEngine.server = server;
		hostServer = true;
		playerReady = new boolean[4];
		gameStarted = false;
		
		bombTimer = bombTime;
		
		playerCount = 0; 	//Host is included by default and flagged ready
		playerReady[0] = true;
		lobby.PlayerReady(1);
	}
	
private void SetupServer(){
		
		server = new Server() {
			
			
			protected Connection newConnection () {	
				
				NPlayerConnection c = new NPlayerConnection();
				int assignedPlayerNumber = 0;
				
				if(playerCount < B2DVars.MAX_PLAYERS){
					playerCount++;
					assignedPlayerNumber = playerCount;
				}else{
					assignedPlayerNumber = -1;  //-1 = Spectator
				}
				
				//Log.info("Current player count: " + playerCount);
				
				c.player = assignedPlayerNumber;
				//Log.info("Assigned player number: " + assignedPlayerNumber);
			
				return c;
			}
		};

		Log.set(Log.LEVEL_WARN);

		
		//Register all packages that will be sent between server and client
		NNetwork.register(server);
				
		//Process all packages in the listener
		server.addListener(new Listener() {
					
			public void connected (Connection c) {
				if(playerCount > 3 || gameStarted)
					c.close();
				
				if(c instanceof NPlayerConnection){
			//	Log.info("[SERVER] player connecting");
					NPlayerConnection playercon = (NPlayerConnection)c;
				
				
					lobby.PlayerConnected(playercon.player);
					lobby.PlayerReady(playercon.player + 1);
					LobbyUpdate lobbyUpdate = new LobbyUpdate();
					lobbyUpdate.playerStatus = lobby.GetLobbyPlayerStatus();
					server.sendToAllTCP(lobbyUpdate);
				}
			}

					
			public void disconnected (Connection c) {
			//	Log.info("[SERVER] player disconnecting");
				
				if(c instanceof NPlayerConnection){
					NPlayerConnection player = (NPlayerConnection)c;
					lobby.PlayerEmpty(player.player);
					LobbyUpdate lobbyUpdate = new LobbyUpdate();
					lobbyUpdate.playerStatus = lobby.GetLobbyPlayerStatus();
					server.sendToAllTCP(lobbyUpdate);
					gameEngine.RemovePlayer(player.player);
				}
			}

			public void received (Connection c, Object o) {
				NPlayerConnection con = (NPlayerConnection)c;
				int player = con.player;		
				//Log.info("Package received from player: " + player);

				if(o instanceof DropBomb){
					//Log.info("Drop Bomb-request received!");
					if(bombTimer < 0){
						gameEngine.DropBomb(player);
						server.sendToAllTCP(new BombSound());
						bombTimer = bombTime;
					}
				}
							
				if(o instanceof MovePlayer){
					//Log.info("Move data received");
					if(gameStarted)
					gameEngine.currentMovePlayer[player - 1] = (com.mygdx.NGame.NNetwork.MovePlayer)o;
					
					return;
					}
			
				if(o instanceof PlayerReady){
					PlayerReadyFromServer pready = new PlayerReadyFromServer();
					pready.player = player;
					playerReady[player] = true;
					lobby.PlayerReady(player);
					LobbyUpdate lobbyUpdate = new LobbyUpdate();
					lobbyUpdate.playerStatus = lobby.GetLobbyPlayerStatus();
					server.sendToAllTCP(lobbyUpdate);
				}
				
				if(o instanceof RequestLobbyUpdate){
					LobbyUpdate lobbyUpdate = new LobbyUpdate();
					lobbyUpdate.playerStatus = lobby.GetLobbyPlayerStatus();
					server.sendToAllTCP(lobbyUpdate);
				}
			}
		
		});
				
			try {
				server.bind(B2DVars.NETWORK_DEFAULT_PORT_INT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Gdx.app.exit();
			}
			server.start();
	}

	//Returns true if all players are ready
	public boolean AllPlayersReady(){
		for(int i = 0; i < playerCount; i++){
			if(!playerReady[i])
				return false;
		}
		return true;
	}

	public void StartGame(){
		gameStarted = true;
		server.sendToAllTCP(new StartGame());
		hostServer = true;
		RunGame();
	}
	
	public void StopServer(){
		hostServer = false;
		gameStarted = false;
		playerCount = 0;
		server.close();
	}
	
	public int ConnectedPlayers(){
		return playerCount + 1; // Include host
	}

	/**
	 * Runs the game simulation. Physics is updated
	 * concurrently at a pace of ~ 60 fps
	 */
	public void RunGame(){
		gameEngine.CreatePlayers(playerCount); 

		gameStarted = true;
		
		updatePhysics = new Thread(new Runnable() {
		     public void run() {
		    	 while(hostServer){	
		    		bombTimer -= 0.01f;
		 			if(gameEngine.GameOver()){
		 				hostServer = false;
		 				server.sendToAllTCP(new GameOver());
		 			}else{	
			 			gameEngine.render();
			 			
		 			//Slow thread to keep steady physics calculations
		 			try {
						Thread.sleep(B2DVars.NETWORK_UPDATE_CYCLE_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 		}
		 		}
		    	 
		     }
		});  
		updatePhysics.start();
		
	}
}
