package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.Array;
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
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.mygdx.game.Player.State;

public class INDAGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	// TODO: Migrate to server!
	public static World WORLD; // Physics world
	private static ContactHandler contactHandler;
	private static WorldQuery worldQuery;
	
	private Player player;
	private Player[] allplayers;
	
	public static Body FRICTION; // Body used to maintain friction between players and floor
	private static float timeStep = 1f / 60f; // Interval of physics simulation

	
	private Box2DDebugRenderer b2dr;

	// Dummy game variables, move to Player-class?
	private static final float MOVE_FORCE = 10f;
	private static final float MAX_MOVE_SPEED = 3f;

	// Screen size and resolution
	private static final int VIRTUAL_HEIGHT = 9; //Height in world units, 1 unit = 1 tile
	private static final int VIRTUAL_WIDTH = 16; // Width in world units, 1 unit = 1 tile
	private Viewport viewport;
	private OrthographicCamera camera;
	//

	// The level
	private TiledMap tileMap;
	private BatchTiledMapRenderer batch_tiledMapRenderer;
	private static int[] bottomLayers = {0 , 1, 5} ; //0: Floor, 1: Pillars, 5: Boxes
	private static int[] topLayers = {2};			 //2: All sprites to render above everything else
	
	private TiledMapTileLayer boxLayer;
	private TiledMapTileLayer wallLayer;
	//

	
	// Items
	private static int pooledBombs = 40;
	private static Vector2 bombPoolPosition = new Vector2(-100, -100);
	private static Bomb[] bombs = new Bomb[pooledBombs];
	
	private static int pooledFire = 40;
	private static Vector2 firePoolPosition = new Vector2(-200, -200);
	private static Fire[] fires = new Fire[pooledFire];
	//
	
	
	
	// Bomb / Fire animations

	
	// Debug-variables
	private static FPSLogger fps = new FPSLogger();
	
	float stateTime;

	@Override
	public void create() {
		// TODO: Migrate to server
		/*
		 * The input vector defines gravitational pull on the x- and y-axis of
		 * the world. 0, 0 = no gravity in either direction. The boolean value
		 * removes inactive bodies from physics calculation, be sure to leave as true
		 */
		WORLD = new World(new Vector2(0, 0), true);
		contactHandler = new ContactHandler();
		WORLD.setContactListener(contactHandler);
		worldQuery = new WorldQuery();

		// Create players---------------------***
				player = new Player(true, new Vector2(3, 2));

		
		// Create the world friction floor----***
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f);
		FRICTION = WORLD.createBody(bdef);
		FRICTION.setUserData("friction floor");

		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = B2DVars.BIT_FRICTION;
		fdef.filter.maskBits = 0;
		fdef.density = 0.0f;
		fdef.restitution = 0.5f;
		fdef.friction = 0f;
		fdef.shape = shape;

		FRICTION.createFixture(fdef).setUserData("friction floor");
		FRICTION.setTransform(new Vector2(VIRTUAL_WIDTH / 2f,
				VIRTUAL_HEIGHT / 2f), 0);
		shape.dispose();
		// ----------------------------------***

		

		// Join players to friction floor----***
		FrictionJointDef def = new FrictionJointDef();
		def.bodyA = FRICTION;
		def.bodyB = player.body;
		def.maxForce = 2f;// set something sensible;
		def.maxTorque = 2f;// set something sensible;
		FrictionJoint joint = (FrictionJoint) WORLD.createJoint(def);
		// ----------------------------------***
		
		
		// Establish item pools--------------***
		for(int b = 0; b < bombs.length; b++)
			bombs[b] = new Bomb(3, 1, WORLD, bombPoolPosition);
		
		for (int f = 0; f < fires.length; f++)
			fires[f] = new Fire(WORLD, firePoolPosition);
		//-----------------------------------***
		
		

		b2dr = new Box2DDebugRenderer();

		// Setup screen resolution and camera position***
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		// -------------------------------------------***

		// Setup the sprite batch
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined); // Define where to project image
													

		// Map loading and rendering*******************
		tileMap = new TmxMapLoader().load(Gdx.files.internal("maps/level.tmx")
				.path());
		batch_tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap, 1 / 32f);
		TiledMapTileLayer layer0 = (TiledMapTileLayer) tileMap.getLayers().get(
				0);
		Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth()
				/ (2 * 32f), layer0.getHeight() * layer0.getTileHeight()
				/ (2 * 32f), 0);

		
		MapBodyBuilder.buildShapes(tileMap, B2DVars.PPM, WORLD, B2DVars.BIT_WALL, "wall");  //Build walls
		
		boxLayer = MapRandomizer.fillMap(WORLD, tileMap, 50); //Construct random boxes
		tileMap.getLayers().add(boxLayer);
		
		wallLayer = (TiledMapTileLayer)tileMap.getLayers().get("Pillars");
		// --------------------------*******************

		camera.position.set(center);
		camera.update();

		stateTime = 0.0f;
		

	}

	public void update(float dt) {
		WORLD.step(dt, 1, 1); 
	}
	

	@Override
	public void render() {
		handleInputs();
		
		// Update statetime and physics
		stateTime += Gdx.graphics.getDeltaTime();
		update(timeStep);


		
		// Clear screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		

		
		batch_tiledMapRenderer.setView(camera);
		batch_tiledMapRenderer.render(bottomLayers);
		

		//Batch BEGIN-----------------------------------***
		batch.begin();
	
		RenderBombs();
		
		RenderPlayers();
		
		RenderFire();

		
		batch.end();
		//Batch END----------------------------------------***
		

		
		batch_tiledMapRenderer.render(topLayers);
		
		//Debug-tools:
	//	b2dr.render(WORLD, camera.combined);
		//PrintAllContacts();
		//	fps.log();


	}
	
	
	
	

	private void RenderPlayers(){
		batch.draw(player.Animation().getKeyFrame(stateTime, true),
			player.body.getPosition().x - 0.5f,
			player.body.getPosition().y - 0.3f, 1, 1);
	}
	
	
	/**
	 * Check for active bombs and render at their position
	 */
	private void RenderBombs(){
		for(Bomb b : bombs){
			if(b.active && !b.detonate){
				batch.draw(b.Animation().getKeyFrame(stateTime, true), b.body.getPosition().x,
				b.body.getPosition().y, 1, 1);
				b.update(Gdx.graphics.getDeltaTime());
			}
			
			else if(b.detonate){
				DetonateBomb(b);
			}
		}
		
	}
	
	
	/**
	 * Draw all active fires according to their current state
	 */
	
	private void RenderFire(){
		
		for (Fire f : fires){	
			if(f.active){
				f.animTimer += Gdx.graphics.getDeltaTime();
				f.update();
				float x = f.body.getPosition().x - 0.5f; //Align animation with body
				float y = f.body.getPosition().y - 0.5f; //Align animation with body
				batch.draw(f.Animation().getKeyFrame(f.animTimer), x, y, 1, 1);
			}
		}
	}
	
	
	/**
	 * If a bomb is flagged to detonate,
	 * render fire at bombs position in proportion
	 * to the bombs specified firepower
	 */
	private void DetonateBomb(Bomb b){
	
				int firePower = b.getFirePower();
				
				float x = b.detonatePosition.x;
				float y = b.detonatePosition.y;
								
				SetFire(x, y);

				if(Gdx.input.isKeyPressed(Input.Keys.F)){
					System.out.println("position x: " + x + " y:" + y);
				}
				
				Fire fire = null;
				
				boolean obstacleHitLeft = false;
				boolean obstacleHitRight = false;
				boolean obstacleHitUp = false;
				boolean obstacleHitDown = false;
				
				//Place fire in adjacent tiles and set the correct
				//animation state for each fire.
				for(int f = 1; f <= firePower; f++)
					{	
						//TODO: Implement obstacle check
					
						if(!obstacleHitUp){
							fire = SetFire(x, y + f);
							fire.state = f == firePower ? Fire.State.Up : Fire.State.Vertical;
						}
						
						if(!obstacleHitDown){
							fire = SetFire(x, y - f);
							fire.state = f == firePower ? Fire.State.Down : Fire.State.Vertical;
						}
						
						if(!obstacleHitRight){
							fire = SetFire(x + f, y);
							fire.state = f == firePower ? Fire.State.Right : Fire.State.Horizontal;
						}
						
						if(!obstacleHitLeft){
							fire = SetFire(x - f, y);
							fire.state = f == firePower ? Fire.State.Left : Fire.State.Horizontal;
						}
					}
				
				b.detonate = false;
			
		}
	
	

	
	
	
	/**
	 * Grabs a Fire-object from the pool. If all
	 * fire-objects are active, creates a new fire.
	 * @return reference to a Fire-body in world
	 */
	private Fire SetFire(float x, float y){
		Fire fire = null;
		
		for(Fire f : fires){
			if(!f.active){
				fire = f;
				break;
			}		
		}	
		
		//If all fires are active, create new fire
		if(fire == null){
			fire = new Fire(WORLD, firePoolPosition);
			System.out.println("Fire pool buffer underrun");
		}

		fire.body.setTransform(x + 0.5f, y + 0.5f, 0);
		fire.active = true;
		
		return fire;
	}

	
	/**
	 * Grabs the first inactive bomb in bomb pool,
	 * places it at players position (quantized to
	 * nearest tile center)
	 */
	
	private void DropBomb(){
		
		for(Bomb b : bombs){
			if(!b.active){
					
				//Flag bomb as active, set state to Ticking
				b.active = true;
				b.state = Bomb.State.Ticking;
		
				//Quantize player position to nearest tile center and place bomb there
				Vector2 bombPosition = CoordinateConverter.quantizePositionToGrid(player.body.getPosition());
				b.body.setTransform(bombPosition, 0);
		
				break;
			}
			
		}
	}
	
	
	private void handleInputs() {
		

		if (Gdx.input.isKeyPressed(Input.Keys.Q))
			Gdx.app.exit();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			DropBomb();

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.SetState(State.Left);
			if (Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
				player.body.applyForceToCenter(new Vector2(-MOVE_FORCE, 0),
						true);

			return;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			player.SetState(State.Right);

			if (Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
				player.body
						.applyForceToCenter(new Vector2(MOVE_FORCE, 0), true);

			return;

		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			player.SetState(State.Up);

			if (Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
				player.body
						.applyForceToCenter(new Vector2(0, MOVE_FORCE), true);

			return;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			player.SetState(State.Down);

			if (Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
				player.body.applyForceToCenter(new Vector2(0, -MOVE_FORCE),
						true);

			return;
		}
		

		/**
		 * Below are controls for dev-purposes
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.I)) {
			camera.zoom += 0.2f;
			camera.update();
			batch.setProjectionMatrix(camera.combined);

		}

		if (Gdx.input.isKeyPressed(Input.Keys.O)) {
			camera.zoom -= 0.2f;
			camera.update();
			batch.setProjectionMatrix(camera.combined);

		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.T))
			System.out.println("Bodies in world: " + WORLD.getBodyCount());

		if(Gdx.input.isKeyJustPressed(Input.Keys.U)){
			float x = Gdx.input.getX();
			float y = Gdx.input.getY();
			System.out.println("Current mouse pos. x: " + Gdx.input.getX() + " y: " + Gdx.input.getY());
		}
		

		
		if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
			Vector2 pos = player.body.getPosition();
			float posY = Math.round(pos.y);
			float posX = Math.round(pos.x);
			int iposY = (int)pos.y;
			int iposX = (int)pos.x;
					
			System.out.println("Current character world position x: " + pos.x + " y: " + pos.y);
			System.out.println("Current character world position (round) x: " + posX + " y: " + posY);
			System.out.println("Current character world position (int) x: " + iposX + " y: " + iposY);
			
			int bombCount = 0;
			for(Bomb b : bombs){
				if(b.active)
					bombCount++;
			}
			
			int fireCount = 0;
			for(Fire f : fires){
				if(f.active)
					fireCount++;
			}
			

			System.out.println("Current active bombs: " + bombCount + " Current active fires: " + fireCount);
			System.out.println("Fire pool object count: " + fires.length + " Bomb pool object count: " + bombs.length);
		

			
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)){
			for(Fire f : fires)
				System.out.println(f.active);
		}
	}
		
		private void PrintAllContacts(){
			Array<Contact> contacts = WORLD.getContactList();
			
			for(Contact c : contacts){
				Fixture fa = c.getFixtureA();
				Fixture fb = c.getFixtureB();
				String a = (String)fa.getUserData();
				String b = (String)fb.getUserData();
				Vector2 apos = fa.getBody().getPosition();
				Vector2 bpos = fb.getBody().getPosition();
			
				if(a == null || b == null)
					continue;
			
			
				System.out.println(a + " at position " + apos + " collides with " + b + " at position " + bpos);
			}
		}
		
			
	}

