package com.mygdx.NGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
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
import com.mygdx.gameRefactor.GameRefactor;

public class NJoinGame implements Screen {
	
	private NGame game;
	private Stage stage;
	private Skin skin;
	
	private SpriteBatch batch;
	private TextureRegion[] regions;
	
	
	private Texture buttonTexture;
	private TextField textField;

	private int[] selected;
	
	private String ipString;
	private final int maxStringSize = 15;
	private BitmapFont font;
	private int cursorXPos;
	private final int cursorStep = 8;
	private final int cursorDotStep = 4;
	

	public NJoinGame(final NGame game) {
		this.game = game;
		
		
		
		
		regions = new TextureRegion[6];
		//Set up texture
		buttonTexture = new Texture(Gdx.files.internal("sprites/buttons/ph_buttons_Join.png"));
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		//Make regions out of texture ( texture,      X, Y, width, height)
		//Order is from left to right and down.
		regions[0] = new TextureRegion(buttonTexture, 0,   320,  512, 128); 
		regions[1] = new TextureRegion(buttonTexture, 0,   256,   128, 64); 
		regions[2] = new TextureRegion(buttonTexture, 128,   256,   128, 64); 
		regions[3] = new TextureRegion(buttonTexture, 256,   256,   128, 64); 
		regions[4] = new TextureRegion(buttonTexture, 384,   256,   128, 64); 
		regions[5] = new TextureRegion(buttonTexture, 0, 320, 4, 25); // Pointer
		
		// Keep track of which is selected.
		selected = new int[2];

		//Initialize
		for(int i = 0; i<selected.length; i++) {
			selected[i] = 0;
		}
		selected[0] = 1;
			
		ipString = "";
		
		cursorXPos = Gdx.graphics.getWidth()/2 - 68;
		
		
		
		// Set up input for menu
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Input.Keys.LEFT:   moveSelectionUp();
											break;
					case Input.Keys.RIGHT:  moveSelectionDown();
											break;
					case Input.Keys.ENTER:  handleAction();
											break;
					case Input.Keys.BACKSPACE: generateNewString("backspace");
											break;
					case Input.Keys.NUMPAD_0:
					case Input.Keys.NUM_0:  generateNewString("0");
											break;
					case Input.Keys.NUMPAD_1:
					case Input.Keys.NUM_1:  generateNewString("1");
											break;
					case Input.Keys.NUMPAD_2:
					case Input.Keys.NUM_2:  generateNewString("2");
											break;
					case Input.Keys.NUMPAD_3:
					case Input.Keys.NUM_3:  generateNewString("3");
											break;
					case Input.Keys.NUMPAD_4:
					case Input.Keys.NUM_4:  generateNewString("4");
											break;
					case Input.Keys.NUMPAD_5:
					case Input.Keys.NUM_5:  generateNewString("5");
											break;
					case Input.Keys.NUMPAD_6:
					case Input.Keys.NUM_6:  generateNewString("6");
											break;
					case Input.Keys.NUMPAD_7:
					case Input.Keys.NUM_7:  generateNewString("7");
											break;
					case Input.Keys.NUMPAD_8:
					case Input.Keys.NUM_8:  generateNewString("8");
											break;
					case Input.Keys.NUMPAD_9:
					case Input.Keys.NUM_9:  generateNewString("9");
											break;
					case Input.Keys.PERIOD: generateNewString(".");
											break;
					
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
		//Buttons
		batch.draw(regions[5], cursorXPos, Gdx.graphics.getHeight()/2 - 12);
		font.draw(batch, ipString, Gdx.graphics.getWidth()/2 - 68, Gdx.graphics.getHeight()/2 + 6);
		font.draw(batch, "ENTER IP:", Gdx.graphics.getWidth()/2 - 152, Gdx.graphics.getHeight()/2 + 6);
		batch.draw(regions[1 + selected[0]], Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2 - 128);
		batch.draw(regions[3 + selected[1]], Gdx.graphics.getWidth()/2 + 128, Gdx.graphics.getHeight()/2 - 128);
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
				// START NEW GAME HERE; MAKE SURE U CAN SEND STRING AS IP
				case 0: //game.setScreen(new GameRefactor(game, ipString));
						break;
				case 1: game.setScreen(new NMainMenu(game));
						break;
				}
			}
		}
	}

	
	private void generateNewString(String s) {
		StringBuilder sb = new StringBuilder(ipString);
		sb.trimToSize();
		if(s == "backspace" && sb.length() > 0) {
			char check = sb.charAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			ipString = sb.toString();
			if(check == '.')
				moveCursor(true, false);
			else
				moveCursor(false, false);
		} else if (s == "backspace" && sb.length() == 0) {
			return;		
		} else if (sb.length() < maxStringSize) {
			sb.append(s);
			ipString = sb.toString();
			if(s == ".")
				moveCursor(true, true);
			else
				moveCursor(false, true);
		}
	}
	
	private void moveCursor(boolean isDot, boolean goForward) {
		if(goForward) {
			if(isDot) cursorXPos += cursorDotStep;
			else cursorXPos += cursorStep;
		} else {
			if(isDot) cursorXPos -= cursorDotStep;
			else cursorXPos -= cursorStep;
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
