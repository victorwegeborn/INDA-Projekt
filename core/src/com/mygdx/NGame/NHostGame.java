package com.mygdx.NGame;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class NHostGame implements Screen {
	
	private NGame game;
	private Stage stage;
	
	private BitmapFont font;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;

	private Texture buttonTexture;

	private int[] selected;
	
	
	//===== SERVER RELATED =======
	private CharSequence port;
	private CharSequence ipString;
	private CharSequence portString;
	
	public NHostGame(final NGame game) {
		this.game = game;
		
		
		stage = new Stage();
		regions = new TextureRegion[14];
		
		font = new BitmapFont();
		
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons_lobby.png"));
		batch = new SpriteBatch();
		
		
		//Make regions out of texture ( texture,      X, Y, width, height)
		//Order is from left to right and down.
		regions[0] = new TextureRegion(buttonTexture, 0,   0,   256, 64); 
		regions[1] = new TextureRegion(buttonTexture, 256, 0,   256, 64);
		regions[2] = new TextureRegion(buttonTexture, 0,   64,  256, 64);
		regions[3] = new TextureRegion(buttonTexture, 256, 64,  256, 64);
		regions[4] = new TextureRegion(buttonTexture, 0,   128, 256, 64);
		regions[5] = new TextureRegion(buttonTexture, 256, 128, 256, 64);
		regions[6] = new TextureRegion(buttonTexture, 0,   192, 256, 64);
		regions[7] = new TextureRegion(buttonTexture, 256, 192, 256, 64);
		
		regions[8] = new TextureRegion(buttonTexture, 0,   256,   128, 64); 
		regions[9] = new TextureRegion(buttonTexture, 128,   256,   128, 64); 
		regions[10] = new TextureRegion(buttonTexture, 256,   256,   128, 64); 
		regions[11] = new TextureRegion(buttonTexture, 384,   256,   128, 64); 
		
		regions[12] = new TextureRegion(buttonTexture, 0,   320,  512, 128); 
		regions[13] = new TextureRegion(buttonTexture, 0,   448,   512, 64); 
		
		// Keep track of which is selected.
		selected = new int[2];
		
		//Initialize
		for(int i = 0; i<selected.length; i++) {
			selected[i] = 0;
		}
		selected[0] = 1;
		
		
		//==============SERVER RELATED======================
		
		port = "54555"; // Call function to get portnumber??
		portString = "PORT: " + port;
		
		try {
			ipString = "IP: " + InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		try {
			new NServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//==============SERVER RELATED END===================
		
		//============== HOST PLAYER SPECS ==================
		/*
			Hosten kan connecta som sista man till servern
			på detta sätt garanterar man de andras connection
			och sedan i slutligen starta spelet och connecta
			sjlv.........
		*/
		//===================================================
		
		// Set up input for menu
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Input.Keys.LEFT:   moveSelectionUp();
									    break;
				case Input.Keys.RIGHT:  moveSelectionDown();
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		stage.draw();
		batch.begin();
		
		//Lobby banner
		batch.draw(regions[13], Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2 + 128);
		
		//Player slots
		batch.draw(regions[0], Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2 + 64);
		batch.draw(regions[2], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2 + 64);
		batch.draw(regions[4], Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2);
		batch.draw(regions[6], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		//infospace ip and ports etc
		batch.draw(regions[12], Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2 - 128);
		
		//Buttons
		batch.draw(regions[8 + selected[0]], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2 - 192);
		batch.draw(regions[10 + selected[1]], Gdx.graphics.getWidth()/2 + 128, Gdx.graphics.getHeight()/2 - 192);
		
		//Printout Server related stuff.
		font.draw(batch, ipString, Gdx.graphics.getWidth()/2 - 230, Gdx.graphics.getHeight()/2 - 20);
		font.draw(batch, portString, Gdx.graphics.getWidth()/2 - 230, Gdx.graphics.getHeight()/2 - 40);
		
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
			if(selected[i] == 1 && i == 3)
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
			  //case 0: game.setScreen(new GameRefactor(game));
			  //		break;
				case 1: game.setScreen(new NMainMenu(game));
						break;
				}
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
