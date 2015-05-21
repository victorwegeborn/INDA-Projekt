package com.mygdx.gameMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.B2DVars;
import com.mygdx.screen.MainGame;
import com.mygdx.game.SoundManager;



public class MainMenu implements Screen {

	private MainGame game;
	private Stage stage;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private TextureRegion[] regions;
	private Texture topLogo;
	
	private Sound select, start;
	
	private Vector2 logoPos, hostPos, joinPos, quitPos, logoScaled, buttonScaled;
	private static float logoScale = 1.5f;
	private static float buttonScale = 1.5f;
	
	
	
	
	private float centerX;
	private float centerY;
	
	private float stateTime;

	private Texture buttonTexture;

	private int[] selected;
	
	
	public MainMenu(final MainGame game) {
		this.game = game;
		stateTime = 0f;
		
		select = SoundManager.walk1;
		
		camera = new OrthographicCamera(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT);
		Viewport viewport = new StretchViewport(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT, camera);
		camera.zoom = 50f;
		camera.update();
		
		logoPos = new Vector2(Gdx.graphics.getWidth()/2 - 280, Gdx.graphics.getHeight()/2 - 48);
		hostPos = new Vector2(Gdx.graphics.getWidth()/2 - 64, Gdx.graphics.getHeight()/2 - 48);
		joinPos = new Vector2(Gdx.graphics.getWidth()/2 - 64, Gdx.graphics.getHeight()/2 - 76);
		quitPos = new Vector2(Gdx.graphics.getWidth()/2 - 64, Gdx.graphics.getHeight()/2 - 108);
		logoScaled = new Vector2(MenuManager.logoLarge.getRegionWidth() * logoScale, MenuManager.logoLarge.getRegionHeight() * logoScale);

		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		regions = new TextureRegion[8];
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons.png"));
		topLogo = new Texture(Gdx.files.internal("sprites/texts/mainlogobw.png"));
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		
		
		//Make regions out of texture ( texture,      X, Y, width, height)
		//Order is from left to right and down.
//		regions[0] = new TextureRegion(buttonTexture, 0,   0,   256, 64); 
//		regions[1] = new TextureRegion(buttonTexture, 256, 0,   256, 64);
//		regions[2] = new TextureRegion(buttonTexture, 0,   64,  256, 64);
//		regions[3] = new TextureRegion(buttonTexture, 256, 64,  256, 64);
//		regions[4] = new TextureRegion(buttonTexture, 0,   128, 256, 64);
//		regions[5] = new TextureRegion(buttonTexture, 256, 128, 256, 64);
//		regions[6] = new TextureRegion(buttonTexture, 0,   192, 256, 64);
//		regions[7] = new TextureRegion(buttonTexture, 256, 192, 256, 64);
		
		// Keep track of which is selected.
		selected = new int[3];    // 4 with options
		
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
										select.play(1f);
									    break;
				case Input.Keys.DOWN:   moveSelectionDown();
										select.play(1f);
									    break;
				case Input.Keys.ESCAPE: Gdx.app.exit();
				case Input.Keys.ENTER:  handleAction();
				}
				return true;
			}
		});	
		
		game.PlayBGMusic(SoundManager.menubg);
	}
	

	@Override
	public void render(float delta) {
		stateTime += Gdx.graphics.getDeltaTime();
	
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
		
		
		
		batch.begin();
		
		batch.draw(MenuManager.logoLarge, logoPos.x, logoPos.y, logoScaled.x, logoScaled.y);
		
		if(selected[0] == 1)
			batch.draw(MenuManager.hostActive.getKeyFrame(stateTime, true), hostPos.x, hostPos.y);
		else
			batch.draw(MenuManager.hostIdle.getKeyFrame(stateTime, true), hostPos.x, hostPos.y);
		
		if(selected[1] == 1)
			batch.draw(MenuManager.joinActive.getKeyFrame(stateTime, true), joinPos.x, joinPos.y);
		else
			batch.draw(MenuManager.joinIdle.getKeyFrame(stateTime, true), joinPos.x, joinPos.y);
		
		if(selected[2] == 1)
			batch.draw(MenuManager.quitActive.getKeyFrame(stateTime, true), quitPos.x, quitPos.y);
		else
			batch.draw(MenuManager.quitIdle.getKeyFrame(stateTime, true), quitPos.x, quitPos.y);
		
		
//		batch.draw(topLogo, Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 + 128);
//		batch.draw(regions[0 + selected[0]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 + 64);
//		batch.draw(regions[2 + selected[1]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2);
//		batch.draw(regions[4 + selected[2]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 64);
//		batch.draw(regions[6 + selected[3]], Gdx.graphics.getWidth()/2 - 128, Gdx.graphics.getHeight()/2 - 128);
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
			if(selected[i] == 1 && i == 2)
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
				case 0: game.setScreen(new GameLobby(game));
						break;
				case 1: game.setScreen(new JoinGame(game));
						break;
				//case 2: game.setScreen(new Options(game));
				//		break;
				case 2: Gdx.app.exit();
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
