package com.mygdx.game;

import java.io.IOException;
import java.net.UnknownHostException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.mygdx.NGame.NConfig;
import com.mygdx.NGame.NNetwork;
import com.mygdx.NGame.NNetwork.BombSound;
import com.mygdx.NGame.NNetwork.BombUpdate;
import com.mygdx.NGame.NNetwork.BoxUpdate;
import com.mygdx.NGame.NNetwork.FireUpdate;
import com.mygdx.NGame.NNetwork.GameOver;
import com.mygdx.NGame.NNetwork.ItemUpdate;
import com.mygdx.NGame.NNetwork.LobbyUpdate;
import com.mygdx.NGame.NNetwork.PlayerConnectedFromServer;
import com.mygdx.NGame.NNetwork.PlayerEmptyFromServer;
import com.mygdx.NGame.NNetwork.PlayerReadyFromServer;
import com.mygdx.NGame.NNetwork.PlayerUpdate;
import com.mygdx.NGame.NNetwork.PowerUpSound;
import com.mygdx.NGame.NNetwork.ShakeUpdate;
import com.mygdx.NGame.NNetwork.WinScreenUpdate;
import com.mygdx.gameMenu.ErrorScreen;
import com.mygdx.gameMenu.GameLobby;
import com.mygdx.gameMenu.MainMenu;
import com.mygdx.screen.MainGame;
import com.mygdx.NGame.NNetwork.StartGame;

public class GameClient {

	public MainGame game;
	public GameServer server;
	public Client client;
	public ClientEngine gameEngine;
	private String hostIP;
	private boolean ready;
	private GameLobby lobby;


	
	public GameClient(MainGame game, String hostIP) throws UnknownHostException, IOException{
		this.game = game;
		this.hostIP = hostIP;
		SetupNetworkListener();
		this.gameEngine = new ClientEngine(game, client, hostIP);
		ready = false;
	}

	public void SetGameLobby(GameLobby lobby){
		this.lobby = lobby;
	}
	
	public String ConnectedTo(){
		return hostIP;
	}
	
	public void SetReady(){
		ready = true;
	}
	
	public boolean IsReady(){
		return ready;
	}
	
	public void DisconnectFromServer(){
		client.stop();
	}
	
	public void SetServerReference(GameServer server){
		this.server = server;
		gameEngine.SetServerReference(server);
	}
	
	private void SetupNetworkListener() throws UnknownHostException, IOException{
		
		//========= CLIENT ==========
		client = new Client(B2DVars.NETWORK_CLIENT_BUFFERSIZE, 4096);
		client.start();
		
		//Register packets
		NNetwork.register(client);
		
		// THREADED LISTENER
		client.addListener(new ThreadedListener(new Listener() {
			
			
			public void connected (Connection c) {
			}
			
			public void disconnect (Connection c) {	
			}

			public void received (Connection c, Object o) {
				//System.out.println("Packaged received!");
				
				if(o instanceof BoxUpdate)
					gameEngine.UpdateBoxes((BoxUpdate)o);
					
				
				
				if(o instanceof PlayerUpdate){
					//System.out.println("Players received!");
					if(gameEngine != null)
					gameEngine.UpdatePlayers((PlayerUpdate)o);
				}
				
				if(o instanceof BombUpdate){
					//System.out.println("Bombs received!");
					if(gameEngine != null)
					gameEngine.UpdateBombs((BombUpdate)o);
				}
				
				if(o instanceof FireUpdate){
					//System.out.println("Fires received!");
					if(gameEngine != null)
					gameEngine.UpdateFires((FireUpdate)o);
				}
				
				if(o instanceof ItemUpdate){
					//System.out.println("Items received!");
					if(gameEngine != null)
					gameEngine.UpdateItems((ItemUpdate)o);
				}
				
				if(o instanceof ShakeUpdate){
					//System.out.println("Screen shake received!");
					if(gameEngine != null){
					ShakeUpdate s = (ShakeUpdate)o;
					gameEngine.SetShakeFactor(s.shakeFactor);
					if(B2DVars.SOUND){
						SoundManager.explosion1.setPosition(0);
						SoundManager.explosion1.play();
					}
					}
				}
				
				if(o instanceof WinScreenUpdate){
					//System.out.println("Win screen received!");
					if(gameEngine != null){
					WinScreenUpdate w = (WinScreenUpdate)o;
					gameEngine.SetWinScreen(w.playerNr);
					}
				}
				
				if(o instanceof StartGame){
					if(B2DVars.SOUND)
					game.PlayBGMusic(SoundManager.bg1);
					
					game.setScreen(gameEngine);
					}
				if(o instanceof GameOver){
					GameOver go = (GameOver)o;					
					gameEngine.GameOver(go.errorcode);
				}
				
				if(o instanceof PowerUpSound){
					PowerUpSound p = (PowerUpSound)o;
					if(p.bomb){
					if(B2DVars.SOUND){
						SoundManager.powerup1.setPosition(0f);
						SoundManager.powerup1.play();
					}
					}
					else{
					if(B2DVars.SOUND){
						SoundManager.powerup2.setPosition(0f);
						SoundManager.powerup2.play();
						}
					}
				}
				
				if(o instanceof BombSound)
					if(B2DVars.SOUND){
						SoundManager.walk4.play(1f);
					}
					
				if(o instanceof LobbyUpdate){
					LobbyUpdate l = (LobbyUpdate)o;
					if(lobby != null){
					lobby.UpdateLobby(l.playerStatus);
					lobby.hasReceivedUpdate = true;
					}
				}

			}
		}));
		
		
		// === Try to connect, throws exception if unsuccessful === 
		client.connect(5000, hostIP, NConfig.PORT);		
	}
		
}
