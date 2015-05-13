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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.Player.State;



public class INDAGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	
	
	//TODO: Migrate to server!
	public static World WORLD;
	private Player player;
	private Player enemy;
	
	private Box2DDebugRenderer b2dr;
	
	//Dummy game variables
	private static final float MOVE_FORCE = 0.02f;
	private static final float MAX_MOVE_SPEED = 0.05f; 
	
	//Screen size and resolution
	private static final int VIRTUAL_HEIGHT = 9;
	private static final int VIRTUAL_WIDTH = 16; 
	private Viewport viewport;
	private OrthographicCamera camera;
	//
	
	//The level
	private TiledMap tileMap;
	private TiledMapRenderer tiledMapRenderer;
	//
	
	
	private Sprite sprite;
	
	float stateTime;
	
	
	@Override
	public void create () {
		//TODO: Migrate to server
		/*
		 * The input vector defines gravitational pull on the x- and
		 * y-axis of the world. 0, 0 = no gravity in either direction.
		 * The boolean value removes inactive bodies from physics calc. 
		 */
		WORLD = new World(new Vector2(0,0), true); 
		player = new Player(true, new Vector2 (2,2));

		
		
		b2dr = new Box2DDebugRenderer();
		

		//Setup screen resolution and camera position***
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		camera.position.set(camera.viewportWidth/2f,camera.viewportHeight/2f,0);
		//-------------------------------------------***
		
		//Setup the sprite batch
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined); //Define where to project image
		
		
		//Map loading and rendering*******************
        tileMap = new TmxMapLoader().load(Gdx.files.internal("maps/level.tmx").path());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap, 1/32f);
        TiledMapTileLayer layer0 = (TiledMapTileLayer) tileMap.getLayers().get(0);
        Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth() / 2, layer0.getHeight() * layer0.getTileHeight() / 2, 0);
                
        MapBodyBuilder mb = new MapBodyBuilder();
        mb.buildShapes(tileMap, B2DVars.PPM, WORLD);
        //--------------------------*******************
                
        
        camera.update();

        
		stateTime = 0.0f;
		
		
	}

	public void update(float dt){
		WORLD.step(dt, 6, 2); // 6 and 2 are accuracy-settings for physics calculations, change later
	}
	
	@Override
	public void render () {
		//Clear screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Update statetime and physics
	    stateTime += Gdx.graphics.getDeltaTime();
	    update(stateTime);
	    
	    tiledMapRenderer.setView(camera);
	    tiledMapRenderer.render();
	    b2dr.render(WORLD, camera.combined);

		handleInputs();
				
	    batch.begin();
        batch.draw(player.Animation().getKeyFrame(stateTime, true), player.body.getPosition().x, player.body.getPosition().y, 1, 1);
	    batch.end();
	    
	    if(Gdx.input.isKeyPressed(Input.Keys.Q))
        	Gdx.app.exit();
	}
	
	
	private void handleInputs(){
		
		   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	        	player.SetState(State.Left);
	        	if(Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
	        		player.body.applyForceToCenter(new Vector2(-MOVE_FORCE, 0), true);
            	
	        	return;
	        }
	        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	        	player.SetState(State.Right);
	        	
	        	if(Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
	        		player.body.applyForceToCenter(new Vector2(MOVE_FORCE, 0), true);
	        	
	        	return;

	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
	        	player.SetState(State.Up);
	        	
	        	if(Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
	        		player.body.applyForceToCenter(new Vector2(0, MOVE_FORCE), true);
            	
	        	return;
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
	        	player.SetState(State.Down);
	        	
	        	if(Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
	        		player.body.applyForceToCenter(new Vector2(0, -MOVE_FORCE), true);
            	
	        	return;
	        }
	        
	        if(Gdx.input.isKeyPressed(Input.Keys.I)){
	        	camera.zoom = camera.zoom++;
	        	batch.setProjectionMatrix(camera.combined);
	        	
	        }
	        

	        if(Gdx.input.isKeyPressed(Input.Keys.O)){
	        	camera.zoom = camera.zoom--;
	        	batch.setProjectionMatrix(camera.combined);

	        }
	        
		
	}
	
	
	
}
