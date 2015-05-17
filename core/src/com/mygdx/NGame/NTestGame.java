package com.mygdx.NGame;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.kryonet.Client;

public class NTestGame implements Screen {

	private NGame game;
	private Stage stage;
	private SpriteBatch batch;
	private TextureRegion sprite;
	

	//=== SERVER CLIENT RELATED ===
	private NClient client;
	private NPlayer player;
	
	public NTestGame (final NGame game) {
		this.game = game;
		
		Texture tex = new Texture(Gdx.files.internal("sprites/characters/ninja1.png"));
		sprite = new TextureRegion(tex, 0,0,32,32);
		player = new NPlayer(sprite, 300, 300);
		
		
		try {
			client = new NClient(InetAddress.getLocalHost().getHostAddress(), player);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyDown(int keycode) {
				client.sendInput(keycode);
				return true;
			}
		});	
	}
	
	@Override
	public void show() {
		batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		

		batch.begin();
		batch.draw(player.sprite, player.xPos, player.yPos);
		batch.end();

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
