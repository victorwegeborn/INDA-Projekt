package com.mygdx.NGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



public class NMainMenu implements Screen {

	private NGame game;
	private Stage stage;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;

	private Texture buttonTexture;

	private int[] selected;
	
	
	public NMainMenu(final NGame game) {
		this.game = game;
		
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
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
		
		// Keep track of which is selected.
		selected = new int[4];
		
		//Initialize
		for(int i = 0; i<selected.length; i++) {
			selected[i] = 0;
		}
		selected[0] = 1;
		
		
		// Set up input for menu
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Input.Keys.UP:     moveSelectionUp();
									    break;
				case Input.Keys.DOWN:   moveSelectionDown();
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
		batch.draw(regions[0 + selected[0]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 + 64);
		batch.draw(regions[2 + selected[1]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
		batch.draw(regions[4 + selected[2]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 64);
		batch.draw(regions[6 + selected[3]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 128);
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
				case 0: game.setScreen(new NHostGame(game));
						break;
			//	case 1: game.setScreen(new GameRefactor(game));
			//			break;
				case 2: game.setScreen(new NOptions(game));
						break;
				case 3: Gdx.app.exit();
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
