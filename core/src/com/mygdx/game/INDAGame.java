package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	private static ContactHandler contactHandler = new ContactHandler();
	private static WorldQuery worldQuery;
	private static FireRayCastHandler fireRayCast = new FireRayCastHandler();
	
	private Player player;
	private static Array<Player> allPlayers = new Array<Player>();
	
	public static Body FRICTION; // Body used to maintain friction between players and floor
	private static float timeStep = 1f / 60f; // Interval of physics simulation

	//Debug-tools
	private Box2DDebugRenderer b2dr;
	private ShapeRenderer sRend;
	static float sWidth = 0.2f, sHeight = 0.2f;
	static ArrayList<Square> squares = new ArrayList<Square>(); 

	// Dummy game variables, move to Player-class?
	private static final float MOVE_FORCE = 10f;
	private static final float MAX_MOVE_SPEED = 3f;

	// Screen size and resolution
	private static final int VIRTUAL_HEIGHT = 9; //Height in world units, 1 unit = 1 tile
	private static final int VIRTUAL_WIDTH = 16; // Width in world units, 1 unit = 1 tile
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector2 cameraCenterPos;
	private static Shake shake = new Shake(); // Shaking camera effect
	//

	// The level
	private TiledMap tileMap;
	private BatchTiledMapRenderer batch_tiledMapRenderer;
	private static int boxLayerIndex = 5;
	private static int[] bottomLayers = {0 , 1, 5} ; //0: Floor, 1: Pillars, 5: Boxes
	private static int[] topLayers = {2};			 //2: All sprites to render above everything else
	
	private TiledMapTileLayer boxLayer;
	private Array<Body> boxBodies;
	private Texture mapSprite;
	private TextureRegion boxSprite;
	
	private TiledMapTileLayer wallLayer;
	//

	
	// Items
	private static int pooledBombs = 60;
	private static Vector2 bombPoolPosition = new Vector2(-100, -100);
	private static Bomb[] bombs = new Bomb[pooledBombs];
	
	private static int pooledFire = 100; 
	private static Vector2 firePoolPosition = new Vector2(-200, -200);
	private static Fire[] fires = new Fire[pooledFire];
	
	private static int pooledPowerUps = 40; 
	private static Vector2 powPoolPosition = new Vector2(200, 200);
	private static BombPowerUp[] bombPows = new BombPowerUp[pooledPowerUps];
	private static FirePowerUp[] firePows = new FirePowerUp[pooledPowerUps];
	//
	
	
	
	// Debug-variables
	private static FPSLogger fps = new FPSLogger();
	
	float stateTime;

	@Override

	/**
	 * Due to internal referencing,
	 * a certain order of creation
	 * must be retained:
	 * Camera -> World -> Players -> Map
	 * All other create-methods 
	 * have non-critical placement
	 */
	public void create() {
		
		SetupCamera();
		
		SetupSpriteBatch();
	
		CreateWorld();
		
		CreatePlayers(2);
	
		InitializeItemPools();
		
		SetupMap("maps/level.tmx");
			
		SetupDebugRenderers();
		
		stateTime = 0.0f;
		

	}
	
	private void SetupCamera(){

		// Setup screen resolution and camera position***
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		// -------------------------------------------***
		
	}
	
	private void SetupSpriteBatch(){
		// Setup the sprite batch
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined); // Define where to project image
											
	}
	
	private void SetupDebugRenderers(){
		// Initialize debug renderers
		b2dr = new Box2DDebugRenderer();				
		sRend = new ShapeRenderer();
	    sRend.setProjectionMatrix(camera.combined);
	}
	
	/**
	 * Creates all players on current map.
	 * Each player is joined to the friction floor
	 * for accurate physics calculation
	 */
	private void CreatePlayers(int numberOfPlayers){
		
		if(numberOfPlayers < 1)
			numberOfPlayers = 1;
		
		if(numberOfPlayers > 4)
			numberOfPlayers = 4;
		
		// Join players to friction floor----***
		FrictionJointDef def = new FrictionJointDef();
		def.bodyA = FRICTION;
 		def.maxForce = 2f;// set something sensible;
		def.maxTorque = 2f;// set something sensible;
		FrictionJoint joint;
		// ----------------------------------***
		
		Vector2 spawnPos;
		
		for(int i = 0; i < numberOfPlayers; i++){
			
			//Set spawn position for players 1 - 4
			switch(i){
			case 0:
				spawnPos = CoordinateConverter.quantizePositionToGrid(new Vector2(3, 1));
				break;
			case 1:
				spawnPos = CoordinateConverter.quantizePositionToGrid(new Vector2(16, 8));
				break;
			case 2:
				spawnPos = CoordinateConverter.quantizePositionToGrid(new Vector2(16, 1));
				break;
			case 3:
				spawnPos = CoordinateConverter.quantizePositionToGrid(new Vector2(3, 8));
				break;
			default:
				spawnPos = new Vector2(3,2);
			}
			
			
			allPlayers.add(new Player(i + 1, spawnPos));
			def.bodyB = allPlayers.get(i).body;
			joint = (FrictionJoint) WORLD.createJoint(def);
		}
		
		player = allPlayers.get(0);  
		
	}
	
	private void CreateWorld(){
		/*
		 * The input vector defines gravitational pull on the x- and y-axis of
		 * the world. 0, 0 = no gravity in either direction. The boolean value
		 * removes inactive bodies from physics calculation, be sure to leave as true
		 */
		WORLD = new World(new Vector2(0, 0), true);
		WORLD.setContactListener(contactHandler);
		worldQuery = new WorldQuery();
		
		
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
		
		//Center friction floor in camera view space 
		FRICTION.setTransform(new Vector2(VIRTUAL_WIDTH / 2f + 1.5f, VIRTUAL_HEIGHT / 2f), 0);
		shape.dispose();
		// ----------------------------------***
		
		
		
	}
	
	
	/**
	 * Pools needed for fire, bombs and items
	 * All other objects in the game world have a
	 * static upper count
	 */
	private void InitializeItemPools(){
		
		// Establish item pools--------------***
		for(int b = 0; b < bombs.length; b++)
			bombs[b] = new Bomb(1, B2DVars.BOMB_TIME, WORLD, bombPoolPosition);
		
		for (int f = 0; f < fires.length; f++)
			fires[f] = new Fire(WORLD, firePoolPosition);
		
		for(int b = 0; b < bombPows.length; b++)
			bombPows[b] = new BombPowerUp(WORLD, powPoolPosition);
		
		for(int f = 0; f < firePows.length; f++)
			firePows[f] = new FirePowerUp(WORLD, powPoolPosition);
		//-----------------------------------***
		
	}
	
	/**
	 * Creates a new random map using the specified level
	 * in .tmx format.
	 */

	private void SetupMap(String map){
		
		// Map loading and rendering*******************
		tileMap = new TmxMapLoader().load(Gdx.files.internal(map).path());
		
		batch_tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap, 1 / 32f);
		
		TiledMapTileLayer layer0 = (TiledMapTileLayer) tileMap.getLayers().get(0);
		
		
		
		Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth()
				/ (2 * 32f), layer0.getHeight() * layer0.getTileHeight()
				/ (2 * 32f), 0);
		
		cameraCenterPos = new Vector2(center.x, center.y);

		

		MapBodyBuilder.buildShapes(tileMap, B2DVars.PPM, WORLD, B2DVars.BIT_WALL, "wall");  //Build walls
		MapRandomizer mapRand = new MapRandomizer();
		
		boxLayer = mapRand.fillMap(WORLD, tileMap, 50); //Construct random boxes
		boxLayer.setVisible(false);
		tileMap.getLayers().add(boxLayer);
		boxBodies = mapRand.boxBodies;
		mapSprite = mapRand.mapSprite;
		boxSprite = mapRand.boxSprite;
		
		
		wallLayer = (TiledMapTileLayer)tileMap.getLayers().get("Pillars");
		// --------------------------*******************

		camera.position.set(center);
		camera.update();
		
	}
	
	public void Update(float dt) {
		WORLD.step(timeStep, 1, 1); // Note that step is called with a fixed timestep
		
		UpdateGameObjects(dt);
		
		if(B2DVars.SCREEN_SHAKE)
			shake.update(dt, camera, cameraCenterPos);

	}
	

	@Override
	public void render() {
		HandleInputs();
		HandleInputsP2(); // Temp local multiplayer 
		
		// Update statetime and physics
		stateTime += Gdx.graphics.getDeltaTime();
		Update(Gdx.graphics.getDeltaTime());


		
		// Clear screen
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		

		
		batch_tiledMapRenderer.setView(camera);
		batch_tiledMapRenderer.render(bottomLayers);
		

		//Batch BEGIN, order of rendering decides displayed layering----***
		batch.begin();
	
		RenderBombs();
		
		RenderBoxes();
		
		RenderItems();
		
		RenderPlayers();
		
		RenderFire();
		
		batch.end();
		//Batch END-----------------------------------------------------***
		

		
		batch_tiledMapRenderer.render(topLayers);
		

		
		//Debug-tools, uncomment for visual aid / bug-hunting:
		//b2dr.render(WORLD, camera.combined);
		//RenderSquares();
		//PrintAllContacts();
		//fps.log();


	}
	
	/**
	 * Calls the Update-method for all active game objects
	 * @param dt deltatime for updates as float
	 */
	private void UpdateGameObjects(float dt){
		for(Player p : allPlayers){
			if(!p.Dead())
				p.Update();	
		}
		
		for(Bomb b : bombs){
			if(b.active)
				b.Update(dt);
		}
		
		for(Fire f : fires){
			if(f.active)
				f.Update(dt);
		}
		
		for(FirePowerUp f : firePows)
				f.Update(dt);
	
		
		for(BombPowerUp b : bombPows)
				b.Update(dt);
		
		
	}
	
	private void RenderSquares(){
		sRend.setProjectionMatrix(camera.combined);
		sRend.updateMatrices();
		camera.update();
		sRend.begin(ShapeType.Line);
		for(Square s : squares){
			sRend.rect(s.x, s.y, sWidth, sHeight, s.color, s.color, s.color, s.color);
			s.update();
		}
		sRend.end();
	}
	
	private void RenderBoxes(){
		Vector2 origin = new Vector2(0,0);
		for(Body b : boxBodies){
			if(b.isActive())
				batch.draw(boxSprite, b.getPosition().x, b.getPosition().y, 1, 1);
			
		}
	}

	/**
	 * Renders all players that are not dead
	 */
	private void RenderPlayers(){
		for(Player p : allPlayers){
		if(!p.Dead()){
			batch.draw(p.Animation().getKeyFrame(stateTime, true),
					p.body.getPosition().x - 0.5f,
					p.body.getPosition().y - 0.3f, 1, 1);
			}
		}
	}
	
	
	/**
	 * Check for active bombs and render at their position
	 */
	
	private void RenderBombs(){
		for(Bomb b : bombs){
			if(b.active && !b.detonate){
				batch.draw(b.Animation(), b.body.getPosition().x,
				b.body.getPosition().y, 1, 1);
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
		float x;
		float y;
		for (Fire f : fires){	
			if(f.active){
				x = f.body.getPosition().x - 0.5f; //Align animation with body
				y = f.body.getPosition().y - 0.5f; //Align animation with body
				batch.draw(f.Animation().getKeyFrame(f.animTimer), x, y, 1, 1);
			}
		}
	}
	
	private void RenderItems(){
		float x;
		float y;
		
		for(FirePowerUp f : firePows){
			if(f.active){
				x = f.body.getPosition().x;
				y = f.body.getPosition().y;
				batch.draw(f.Animation(), x, y, 1, 1);
			}
		}
			
		for(BombPowerUp b : bombPows){
			if(b.active){
				x = b.body.getPosition().x;
				y = b.body.getPosition().y;
				batch.draw(b.Animation(), x, y, 1, 1);
			}
		}		
		
	}
	
	/**
	 * Place the specified power up at the specified position
	 * @param type 
	 * @param position
	 */
	
	public static void PlacePowerUp(int type, Vector2 position){
		
		switch(type){
		
		case B2DVars.BOMB_POWERUP:
			
			BombPowerUp bombPow;
			
			for(BombPowerUp b : bombPows){
				if(!b.active){
					bombPow = b;
					bombPow.body.setTransform(position, 0);
					bombPow.active = true;
					break;
				}		
			}
			
				break;
				
		case B2DVars.FIRE_POWERUP:
			
			FirePowerUp firePow;
			
			for(FirePowerUp f : firePows){
				if(!f.active){
					firePow = f;
					firePow.body.setTransform(position, 0);
					firePow.active = true;
					break;
				}		
			}
				break;
			
		default:
			break;
			
		}	
	}
	
	
	/**
	 * If a bomb is flagged to detonate,
	 * render fire at bombs position in proportion
	 * to the bombs specified firepower
	 */
	
	private void DetonateBomb(Bomb b){
	
				int firePower = b.GetFirePower();
				
				float x = b.detonatePosition.x;
				float y = b.detonatePosition.y;
				float offset = 0.5f;
				float rayx = x + offset;
				float rayy = y + offset;
				DrawSquare(rayx, rayy, Color.WHITE);
								
				SetFire(x, y);

				if(Gdx.input.isKeyPressed(Input.Keys.F)){
					System.out.println("position x: " + x + " y:" + y);
				}
				
				Fire fireL = null;
				Fire fireR = null;
				Fire fireU = null;
				Fire fireD = null;
				
				boolean obstacleHitLeft = false;
				boolean obstacleHitRight = false;
				boolean obstacleHitUp = false;
				boolean obstacleHitDown = false;
				FireRayCastHandler fRay = new FireRayCastHandler();

				/**
				 * For each tile in each direction, a short raycast is
				 * done to check if the next tile holds a box or a wall.
				 * Note that player  and item collision is handled
				 * dynamically in the ContactHandler.
				 */
				for(int f = 1; f <= firePower; f++)
					{	
					
							if(!obstacleHitUp){
								WORLD.rayCast(fRay, new Vector2(rayx, rayy + f - 1), new Vector2(rayx, rayy + f));
								obstacleHitUp = fRay.hasCollided && Math.abs(fRay.hitPoint.y - (rayy + f - 1)) < 1;
								DrawSquare(rayx, rayy + f, Color.WHITE);

								
								if(!obstacleHitUp){
									fireU = SetFire(x, y + f);
									fireU.state = f == firePower ? Fire.State.Up : Fire.State.Vertical;
								}
							}
						
							if(!obstacleHitDown){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx, rayy - f + 1), new Vector2(rayx, rayy - f));
								
								if(fRay.hitPoint != null)
									obstacleHitDown = fRay.hasCollided && Math.abs(fRay.hitPoint.y - (rayy - f + 1)) < 1;
								
								DrawSquare(rayx, rayy - f, Color.WHITE);
								
							
								if(!obstacleHitDown){
									fireD = SetFire(x, y - f);
									fireD.state = f == firePower ? Fire.State.Down : Fire.State.Vertical;
								}
							}
							
							if(!obstacleHitRight){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx + f - 1, rayy), new Vector2(rayx + f, rayy));
								
								if(fRay.hitPoint != null)
									obstacleHitRight = fRay.hasCollided && Math.abs(fRay.hitPoint.x - (rayx + f - 1)) < 1;
								
								DrawSquare(rayx + f, rayy, Color.WHITE);

							
								if(!obstacleHitRight){
									fireR = SetFire(x + f, y);
									fireR.state = f == firePower ? Fire.State.Right : Fire.State.Horizontal;
								}
							}	
							
							if(!obstacleHitLeft){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx - f + 1, rayy), new Vector2(rayx - f, rayy));
								
								if(fRay.hitPoint != null)
									obstacleHitLeft = fRay.hasCollided && Math.abs(fRay.hitPoint.x - (rayx - f + 1)) < 1;
								
								DrawSquare(rayx - f, rayy, Color.WHITE);

							
								if(!obstacleHitLeft){	
									fireL = SetFire(x - f, y);
									fireL.state = f == firePower ? Fire.State.Left : Fire.State.Horizontal;
								}
							}
							
					}
				
				shake.shake(0.1f * firePower); //Screen shakes proportionately to fire power
				b.detonate = false;
			
		}
	
	

	
	private boolean isOccupied(float x, float y){
		//TODO: Implement occupation check
		return false;
	}
	
	
	//Useful for debugging world checks
	public static void DrawSquare(float x, float y, Color color){
		squares.add(new Square(x, y, color));	
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
	
	private void DropBomb(Player player){
		
		if(!player.CanDropBomb())
			return;
		
		BombQuery bombQuery = new BombQuery();
		int firePower = player.GetFirePower();
		Vector2 bombPosition = CoordinateConverter.quantizePositionToGrid(player.body.getPosition());
		WORLD.QueryAABB(bombQuery, bombPosition.x - 0.2f, bombPosition.y - 0.2f, bombPosition.x + 0.2f, bombPosition.y + 0.2f);
		
		if(bombQuery.tileHasBomb){
			System.out.println("Tile has bomb already.");
			return;
		}
		
		for(Bomb bomb : bombs){
			if(!bomb.active){
				
				
				//Flag bomb as active, set state to Ticking, and set firepower to players current firepower
				bomb.active = true;
				bomb.state = Bomb.State.Ticking;
				bomb.SetFirePower(firePower); 
				player.body.getPosition();
				//Quantize player position to nearest tile center and place bomb there
				bomb.body.setTransform(bombPosition, 0);
				
				player.RegisterDroppedBomb(bomb);
				return;
			}
			
		}
		
	}
	
	
	private void HandleInputsP2(){
		Player p = allPlayers.get(1);
		if (Gdx.input.isKeyJustPressed(Input.Keys.T))
			if(!p.Dead()){
				DropBomb(p);
			}

		if (Gdx.input.isKeyPressed(Input.Keys.A)){
			if(!p.Dead()){
				p.SetState(State.Left);
				if (Math.abs(p.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
					p.body.applyForceToCenter(new Vector2(-MOVE_FORCE, 0),	true);
		}
			return;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			if(!p.Dead()){
				p.SetState(State.Right);

				if (Math.abs(p.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
					p.body.applyForceToCenter(new Vector2(MOVE_FORCE, 0), true);
			}

			return;

		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			if(!p.Dead()){
				p.SetState(State.Up);
				
				if (Math.abs(p.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
					p.body.applyForceToCenter(new Vector2(0, MOVE_FORCE), true);
				}
			return;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			if(!p.Dead()){
				p.SetState(State.Down);

				if (Math.abs(p.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
					p.body.applyForceToCenter(new Vector2(0, -MOVE_FORCE),	true);
			}
			return;
		}
		
		
	}
	
	
	
	private void HandleInputs() {
		

		if (Gdx.input.isKeyPressed(Input.Keys.Q))
			Gdx.app.exit();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.P))
			if(!player.Dead()){
				DropBomb(player);
			}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if(!player.Dead()){
				player.SetState(State.Left);
				if (Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
					player.body.applyForceToCenter(new Vector2(-MOVE_FORCE, 0),	true);
		}
			return;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if(!player.Dead()){
				player.SetState(State.Right);

				if (Math.abs(player.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
					player.body.applyForceToCenter(new Vector2(MOVE_FORCE, 0), true);
			}

			return;

		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if(!player.Dead()){
				player.SetState(State.Up);
				
				if (Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
					player.body.applyForceToCenter(new Vector2(0, MOVE_FORCE), true);
				}
			return;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			if(!player.Dead()){
				player.SetState(State.Down);

				if (Math.abs(player.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
					player.body.applyForceToCenter(new Vector2(0, -MOVE_FORCE),	true);
			}
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
		
		if(Gdx.input.isKeyPressed(Input.Keys.B))
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
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.L))
			DrawSquare(player.body.getPosition().x, player.body.getPosition().y, Color.BLUE);
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

