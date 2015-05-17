package com.mygdx.NGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class NJoinGame implements Screen {
	
	private NGame game;
	private Stage stage;
	private Skin skin;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;
	private TextField ipAdress;
	
	
	private Texture buttonTexture;

	private int[] selected;
	
	
	public NJoinGame(final NGame game) {
		this.game = game;


		
		regions = new TextureRegion[1];
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons_lobby.png"));
		batch = new SpriteBatch();
		
		//Make regions out of texture ( texture,      X, Y, width, height)
		//Order is from left to right and down.
		regions[0] = new TextureRegion(buttonTexture, 0,   320,  512, 128); 
		
		
		// Set up input for menu
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Input.Keys.UP:     //moveSelectionUp();
									    break;
				case Input.Keys.DOWN:   //moveSelectionDown();
									    break;
				case Input.Keys.ESCAPE: Gdx.app.exit();
				case Input.Keys.ENTER:  //handleAction();
				}
				return true;
			}
		});	
		
		
	}
	

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(regions[0], Gdx.graphics.getWidth()/2 - 256, Gdx.graphics.getHeight()/2 - 64);
		batch.end();
		
	}
	/*
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
	*/
	/*
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
	*/
	/*
	private void handleAction() {
		for(int i = 0; i < selected.length; i++) {
			if(selected[i] == 0)
				continue;
			if(selected[i] == 1) {
				switch(i) {
				case 0: game.setScreen(new NHostGame(game));
						break;
				case 1: game.setScreen(new NJoinGame(game));
						break;
				case 2: game.setScreen(new NOptions(game));
						break;
				case 3: Gdx.app.exit();
						break;	
				}
			}
		}
	}
	*/
	
	
	public class NTextInputListener implements TextInputListener {

		@Override
		public void input(String text) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void canceled() {
			// TODO Auto-generated method stub
			
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
