package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Player.State;



public class INDAGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	private Player player;
	private Player enemy;
	
	//Dummy game variables
	private static final float MOVE_SPEED = 4.0f;
	
	//Screen size and resolution
	private static final int VIRTUAL_HEIGHT = 288;
	private static final int VIRTUAL_WIDTH = 512; 
	private Viewport viewport;
	private OrthographicCamera camera;
	//
	
	//The level
	private TiledMap level;
	private TiledMapRenderer tiledMapRenderer;
	//
	
	
	private Sprite sprite;
	
	float stateTime;
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		//Setup screen resolution and camera position***
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
		//-------------------------------------------***
		
		player = new Player(true);
		
		sprite = new TextureAtlas(Gdx.files.internal("sprites/characters/ninja1.txt")).createSprite("down");
		
        level = new TmxMapLoader().load(Gdx.files.internal("maps/level.tmx").path());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(level);
        TiledMapTileLayer layer0 = (TiledMapTileLayer) level.getLayers().get(0);
        Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth() / 2, layer0.getHeight() * layer0.getTileHeight() / 2, 0);

        camera.position.set(center);
        camera.update();

        
		stateTime = 0.0f;
		
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    stateTime += Gdx.graphics.getDeltaTime();
		
		batch.setProjectionMatrix(camera.combined); //Define where to project image
		tiledMapRenderer.setView(camera);
	    tiledMapRenderer.render();
		
		HandleInputs();

	    batch.begin();
        batch.draw(player.Animation().getKeyFrame(stateTime, true), 100, 100);
	    //sprite.draw(batch);         
	    batch.end();
	    
	    if(Gdx.input.isKeyPressed(Input.Keys.Q))
        	Gdx.app.exit();
	}
	
	
	private void HandleInputs(){
		   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	        	player.SetState(State.Left);
	            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	                sprite.translateX(-1f);
	            else
	                sprite.translateX(-MOVE_SPEED);
	        }
	        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	        	player.SetState(State.Right);

	            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	                sprite.translateX(1f);
	            else
	                sprite.translateX(MOVE_SPEED);
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
	        	player.SetState(State.Up);
	        	
	        	if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	        		sprite.translateY(1f);
	        	else
	        		sprite.translateY(MOVE_SPEED);
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
	        	player.SetState(State.Down);

	        	if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	        		sprite.translateY(-1f);
	        	else
	        		sprite.translateY(-MOVE_SPEED);
	        }
		
	}
	
	
	
}
