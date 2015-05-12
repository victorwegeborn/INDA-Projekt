package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.Input;


public class INDAGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	//Dummy game variables
	private static final float MOVE_SPEED = 4f;
	
	//Screen size and resolution
	private static final int VIRTUAL_HEIGHT = 256;
	private static final int VIRTUAL_WIDTH = 256; 
	private Viewport viewport;
	private OrthographicCamera camera;
	//
	
	//Player sprites and animations
	private TextureAtlas p1SpriteSheet;
	private TextureRegion p1CurrentFrame;
	private Animation p1UpAnim;
	private Animation p1DownAnim;
	private Animation p1SideAnim;
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
		
		//Construct animations for player 1***
		float framerate = 1/30f;
		p1SpriteSheet = new TextureAtlas(Gdx.files.internal("sprites/characters/ninja1.txt"));
		p1UpAnim = new Animation(framerate, p1SpriteSheet.findRegions("up"));
		p1DownAnim = new Animation(framerate, p1SpriteSheet.findRegions("down"));
		p1SideAnim = new Animation(framerate, p1SpriteSheet.findRegions("side"));
		//---------------------------------***
		
		sprite = p1SpriteSheet.createSprite("up");
		
		stateTime = 0.0f;
		
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		batch.setProjectionMatrix(camera.combined); //Define where to project image 
		
		
		HandleInputs();
		
	    batch.begin();
	    sprite.draw(batch);         
	    batch.end();
		
	
	}
	
	
	private void HandleInputs(){
		   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	                sprite.translateX(-1f);
	            else
	                sprite.translateX(-MOVE_SPEED);
	        }
	        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	                sprite.translateX(1f);
	            else
	                sprite.translateX(MOVE_SPEED);
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
	        	if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	        		sprite.translateY(1f);
	        	else
	        		sprite.translateY(MOVE_SPEED);
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
	        	if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	        		sprite.translateY(-1f);
	        	else
	        		sprite.translateY(-MOVE_SPEED);
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.Q))
	        	Gdx.app.exit();
			
			
		
	}
	
	
	
}
