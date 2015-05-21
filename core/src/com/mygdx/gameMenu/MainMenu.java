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
import com.badlogic.gdx.math.Vector3;
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
	private static float cameraZoom = 0f;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;
	private Texture topLogo;
	
	private Sound select, start;
	
	private Vector2 logoPos, hostPos, joinPos, quitPos, logoScaled, buttonScaled;
	private static float logoScale = 1.5f;
	private static float buttonScale = 1.5f;
	
	private static final float fxTime = 5f;
	private float fxTimer;
	private boolean distortLogo;
	
	private static final float distortTime = 0.05f;
	private float distortTimer;
	
	
	private float centerX;
	private float centerY;
	
	private float stateTime;

	private Texture buttonTexture;

	private int[] selected;
	
	
	public MainMenu(final MainGame game) {
		this.game = game;
		stateTime = 0f;
		fxTimer = 0f;
		distortTimer = 0f;
		
		select = SoundManager.walk1;
		
		camera = new OrthographicCamera(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT);
		Viewport viewport = new StretchViewport(B2DVars.VIRTUAL_WIDTH, B2DVars.VIRTUAL_HEIGHT, camera);
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom += cameraZoom;
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
		//camera.zoom += cameraZoom;
		//camera.position.set(327, 296, 0);
		camera.update();
		
		
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
		float graphicsDelta = Gdx.graphics.getDeltaTime();

		stateTime += graphicsDelta;
		
		if(!distortLogo)
		fxTimer += graphicsDelta;
		else
		distortTimer += graphicsDelta;

		
		if(fxTimer > fxTime){
			SoundManager.logoDistort.play(1f);
			distortLogo = true;
			fxTimer = 0f;
		}

			
		if(distortTimer > distortTime && distortLogo){
			distortLogo = false;
			distortTimer = 0f;
		}
		
		float cameraMod = 0.0001f * (float)Math.sin(stateTime);
		camera.zoom += cameraMod;
		camera.rotate(cameraMod);
		camera.update();
        batch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
		
		batch.begin();
		
		if(!distortLogo)
		batch.draw(MenuManager.logoLarge, logoPos.x, logoPos.y, logoScaled.x, logoScaled.y);
		else
		batch.draw(MenuManager.distortLogo.getKeyFrame(stateTime, true), logoPos.x, logoPos.y, logoScaled.x, logoScaled.y);

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
