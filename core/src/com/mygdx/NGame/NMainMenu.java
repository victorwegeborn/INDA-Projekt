package com.mygdx.NGame;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.screen.MGameVars;


public class NMainMenu implements Screen {

	private NGame game;
	private Stage stage;
	private BitmapFont font;
	
	// Visuals
	private SpriteBatch batch;
	private TextureRegion[] regions;

	private Texture buttonTexture;
	
	private ImageButton bHostGame;
	private ImageButton bJoinGame;
	private ImageButton bOptions;
	private ImageButton bQuit;
	
	private ImageButtonStyle bHostGameStyle;
	private ImageButtonStyle bJoinGameStyle;
	private ImageButtonStyle bOptionsStyle;
	private ImageButtonStyle bQuitStyle;
	
	private Skin skin;
	private TextureAtlas atlas;

	private int[] selected;
	
	public NMainMenu(final NGame game) {
		this.game = game;
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		font = MGameVars.DEFAULT_FONT;
		
		regions = new TextureRegion[8];
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons.png"));
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
		
		// Keep track of wich is selected.
		selected = new int[4];
		
		//Initialize
		for(int i = 0; i<selected.length; i++) {
			selected[i] = 0;
		}
		selected[0] = 1;
		
	
	}
	
	
	

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		handleInputs();
		
		stage.draw();
		
		batch.begin();
		
		
		
		batch.draw(regions[0 + selected[0]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 + 64);
		batch.draw(regions[2 + selected[1]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
		batch.draw(regions[4 + selected[2]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 64);
		batch.draw(regions[6 + selected[3]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 128);
		
		batch.end();
		
		
		
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();
	}
	
	private void handleInputs() {
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
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
		
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
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
	}
	
	
	
	
	@Override
	public void show() {
		batch = new SpriteBatch();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	

}
