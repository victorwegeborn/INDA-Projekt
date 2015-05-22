package com.mygdx.gameMenu;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.B2DVars;
import com.mygdx.game.ClientEngine;
import com.mygdx.game.GameClient;
import com.mygdx.game.GameServer;
import com.mygdx.screen.ClientWrapper;
import com.mygdx.screen.MainGame;
import com.mygdx.NGame.NNetwork.PlayerReady;
import com.mygdx.NGame.NNetwork.RequestLobbyUpdate;
import com.mygdx.game.SoundManager;
import com.badlogic.gdx.audio.Sound;



public class GameLobby implements Screen {
	
	private MainGame game;
	private Stage stage;
	private GameServer server; //Server if host
	private GameClient client; //Local client if client
	
	private int[] playerStatus;
	private int[] bufferedPlayerStatus;
	public boolean hasReceivedUpdate;

	
	private OrthographicCamera camera;
	private static float cameraZoom = 50f; 
	
	private boolean isHost;
	
	private float stateTime;
	
	private Sound select, start;
	
	private BitmapFont font;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;
	
	
	private float screenWidth;
	private float screenHeight;

	private Texture buttonTexture;

	private int[] selected;
	
	
	//===== SERVER RELATED =======
	private CharSequence port;
	private CharSequence ipString;
	private static CharSequence portString = "PORT: " + B2DVars.NETWORK_DEFAULT_PORT_STRING;
	
	//===== IF NO CLIENT IS PASSED -> HOST LOBBY =====
	public GameLobby(final MainGame game) {
		this.game = game;
		isHost = true;
		stateTime = 0f;
		stage = new Stage();
		playerStatus = new int[4];
		
		for(int i = 0; i < playerStatus.length; i++){
			playerStatus[i] = B2DVars.PLAYER_EMPTY;
		}
		
		SetupCamera();
		CreateGraphics();
		SetupAudio();
		SetupServer();
	
		StartInputListening();
	}
	
	
	//===== IF CLIENT IS PASSED -> JOIN LOBBY =====
	public GameLobby(final MainGame game, final GameClient client){
		ipString = client.ConnectedTo(); 
		this.game = game;
		isHost = false;
		stateTime = 0f;
		this.client = client;
		playerStatus = new int[4];
		
		for(int i = 0; i < playerStatus.length; i++){
			playerStatus[i] = B2DVars.PLAYER_EMPTY;
		}
		
		
		stage = new Stage();
		
		SetupCamera();
		CreateGraphics();
		SetupAudio();
		StartInputListening();
		
		
	}
	
	public void UpdateLobby(int[] updatedPlayerStatus){
		playerStatus = updatedPlayerStatus;
	}
	
	public int[] GetLobbyPlayerStatus(){
		return playerStatus;
	}
	
	public void PlayerConnected(int player){
		if(player < 1 || player > 4)
			return;
		playerStatus[player - 1] = B2DVars.PLAYER_CONNECTED;
	}
	
	public void PlayerReady(int player){
		if(player < 1 || player > 4)
			return;
		playerStatus[player - 1] = B2DVars.PLAYER_READY;
	}
	
	public void PlayerEmpty(int player){
		if(player < 1 || player > 4)
			return;
		playerStatus[player - 1] = B2DVars.PLAYER_EMPTY;
	}
	
	private void SetupCamera(){
		camera = new OrthographicCamera(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT);
		Viewport viewport = new StretchViewport(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT, camera);
		camera.zoom += cameraZoom;
		camera.update();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
	}
	
	
	private void SetupAudio(){
		select = SoundManager.walk1;
		
		//TODO: Change music?
	}
	
	private void SetupServer(){		
		//==============SERVER RELATED======================	
			try {
				ipString = "IP: " + InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}

			try {
				server = new GameServer(game, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//==============SERVER RELATED END===================		
	}
	
	private void CreateGraphics(){
		regions = new TextureRegion[14];
		
		font = new BitmapFont();
		
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons_lobby.png"));
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		
		// Keep track of which is selected.
		selected = new int[2];
		
		//Initialize
		for(int i = 0; i<selected.length; i++) {
			selected[i] = 0;
		}
		selected[0] = 1;
		
	}
	
	private void StartInputListening(){
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Input.Keys.LEFT:   moveSelectionUp();
										select.play(1f);
									    break;
				case Input.Keys.RIGHT:  moveSelectionDown();
										select.play(1f);
									    break;
				case Input.Keys.ESCAPE: Gdx.app.exit();
				case Input.Keys.ENTER:  handleAction();
				
				}
				return true;
			}
		});		
	}
	
	@Override
	public void render(float delta) {
		if(!hasReceivedUpdate && !isHost){
			client.client.sendTCP(new RequestLobbyUpdate());
		}
		stateTime += Gdx.graphics.getDeltaTime();
		bufferedPlayerStatus = playerStatus;
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		stage.draw();
		batch.begin();
		
		//Lobby banner
		batch.draw(MenuManager.lobbyBanner, Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2 + 128);
		
		//=== Draw player slots ===
		batch.draw(MenuManager.GetPlayerStatusTexture(1, bufferedPlayerStatus[0]), screenWidth / 2 - 128, screenHeight / 2 + 64);
		batch.draw(MenuManager.GetPlayerStatusTexture(2, bufferedPlayerStatus[1]), screenWidth / 2, screenHeight / 2 + 64);
		batch.draw(MenuManager.GetPlayerStatusTexture(3, bufferedPlayerStatus[2]), screenWidth / 2 - 128, screenHeight / 2);
		batch.draw(MenuManager.GetPlayerStatusTexture(4, bufferedPlayerStatus[3]), screenWidth / 2, screenHeight / 2);

		
		// === Draw buttons ===
		if(selected[0] == 1)
			batch.draw(MenuManager.zuluActive.getKeyFrame(stateTime,true), screenWidth / 2 - 128, screenHeight / 2 - 192);
		else
			batch.draw(MenuManager.zuluIdle.getKeyFrame(stateTime,true), screenWidth / 2 - 128, screenHeight / 2 - 192);
		
		if(selected[1] == 1)
			batch.draw(MenuManager.backActive.getKeyFrame(stateTime,true), screenWidth / 2, screenHeight / 2 - 192);
		else
			batch.draw(MenuManager.backIdle.getKeyFrame(stateTime,true), screenWidth / 2, screenHeight / 2 - 192);


		
		//Printout Server related stuff.
		font.draw(batch, ipString, Gdx.graphics.getWidth()/2 - 230, Gdx.graphics.getHeight()/2 - 60);
		font.draw(batch, portString, Gdx.graphics.getWidth()/2 - 230, Gdx.graphics.getHeight()/2 - 80);
		
		batch.end();
	}
	
	
	private void moveSelectionUp() {
		for(int i = 0; i < selected.length; i++) {
			if(selected[i] == 1 && i == 0)
				break;
			else if(selected[i] == 1) {
				selected[i-1] = 1;
				selected[i] = 0;
				break;
			}
		}
	}
	
	private void moveSelectionDown() {
		for(int i = 0; i < selected.length; i++) {
			if(selected[i] == 1 && i == 1)
				break;
			else if(selected[i] == 1) {
				selected[i+1] = 1;
				selected[i] = 0;
				break;
			}
		}
	}
	
	
	private void handleAction() {
		for(int i = 0; i < selected.length; i++) {
			if(selected[i] == 0)
				continue;
			if(selected[i] == 1) {
				switch(i) {
				case 0:		
						if(isHost){
							StartGame();							
						}else{
							client.client.sendTCP(new PlayerReady());
						}
													
						break;
				
				case 1: if(isHost)
							server.StopServer();
						else
							client.DisconnectFromServer();
				
						game.setScreen(new MainMenu(game));

						break;
				}
			}
		}
	}
	
	private void StartGame(){
		
		if(server.ConnectedPlayers() > 1 && server.AllPlayersReady()){
			
			//Set up local client
			try {
				client = new GameClient(game, "localhost");
				client.SetGameLobby(this);
				client.SetServerReference(server);
		
				server.StartGame(); // Start game
				game.setScreen(client.gameEngine); //Change to client
			
				
			} catch (IOException e) {
				game.setScreen(new ErrorScreen(game, B2DVars.ERROR_UNKNOWN));
				} 
			}
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void hide() { }

	@Override
	public void dispose() { }

}
