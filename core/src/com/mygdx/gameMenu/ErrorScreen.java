package com.mygdx.gameMenu;

import java.io.IOException;
import java.net.UnknownHostException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.mygdx.game.B2DVars;
import com.mygdx.game.GameClient;
import com.mygdx.game.SoundManager;
import com.mygdx.screen.MainGame;

public class ErrorScreen implements Screen {
	
	private MainGame game;
	private Stage stage;
	private Skin skin;
	
	
	private SpriteBatch batch;
	

	
	private Sound input, select, start;
	private float stateTime;
	private int error;
	

	public ErrorScreen(final MainGame game, int error) {
		this.game =  game;
		this.error = error;
		stateTime = 0f;
		select = SoundManager.walk1;	
		batch = new SpriteBatch();
	

		
		//If a key is pressed -> return to Join Game-screen
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				game.setScreen(new MainMenu(game));
				return true;
			}
			});	
	}
	

	@Override
	public void render(float delta) {
		stateTime += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		//batch.draw(MenuManager.makeConnect, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 + 64);
		//Buttons
		
		switch(error){
		case B2DVars.ERROR_GAME_FULL:
			batch.draw(MenuManager.gameFull, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
			break;
		case B2DVars.ERROR_UNKNOWN:
			batch.draw(MenuManager.connectError, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
			break;
		case B2DVars.ERROR_ALL_PLAYERS_LEFT:
			batch.draw(MenuManager.allPlayersLeft, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
			break;
		case B2DVars.ERROR_HOST_LEFT:
			batch.draw(MenuManager.hostLeft, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
			break;
		default:
			batch.draw(MenuManager.connectError, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
	}	
		
		batch.draw(MenuManager.pressToReturn, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 64);

		batch.end();

		
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
