package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.NGame.NConfig;
import com.mygdx.NGame.NNetwork;
import com.mygdx.NGame.NNetwork.NPlayerConnection;
import com.mygdx.NGame.NNetwork.BombUpdate;
import com.mygdx.NGame.NNetwork.PlayerUpdate;
import com.mygdx.NGame.NNetwork.FireUpdate;
import com.mygdx.NGame.NNetwork.ItemUpdate;
import com.mygdx.NGame.NNetwork.ShakeUpdate;
import com.mygdx.NGame.NPlayer;
import com.mygdx.NGame.NNetwork.BoxUpdate;
import com.mygdx.NGame.NNetwork.WinScreenUpdate;
import com.mygdx.NGame.NNetwork.MovePlayer;
import com.mygdx.game.Player.State;
import com.mygdx.gameData.BombData;
import com.mygdx.gameData.BoxData;
import com.mygdx.gameData.FireData;
import com.mygdx.gameData.ItemData;
import com.mygdx.gameData.PlayerData;

public class NetworkEngine {		//implements Screen {
	SpriteBatch batch;
	Texture img;
	
	private static final boolean renderWorld = false;
	
	
	public static World WORLD; // Physics world
	private static ContactHandler contactHandler = new ContactHandler();
	private static WorldQuery worldQuery;
	private static FireRayCastHandler fireRayCast = new FireRayCastHandler();
	
	private Player player;
	private WinScreenUpdate winUpdate = new WinScreenUpdate();
	private boolean sentWinUpdate;
	
	public static Body FRICTION; // Body used to maintain friction between players and floor
	private static float timeStep = 1f / 60f; // Interval of physics simulation

	//Debug-tools
	private Box2DDebugRenderer b2dr;
	private ShapeRenderer sRend;
	static float sWidth = 0.2f, sHeight = 0.2f;
	static ArrayList<Square> squares = new ArrayList<Square>(); 

	// Dummy game variables, move to Player-class / B2DVars?
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
	private Array<Box> boxes;
	private Texture mapSprite;
	private TextureRegion boxSprite;
	
	private TiledMapTileLayer wallLayer;
	
	public MovePlayer[] currentMovePlayer;
	
	private boolean resetGame;
	
	// Debug-variables
	private static FPSLogger fps = new FPSLogger();
	
	float stateTime;
	
	public Server server;
	private int playerCount;
		
	
	public NetworkEngine(){
		create();
	}
	
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
	
		InitializeItemPools();
		
		SetupMap("maps/level.tmx");
			
		SetupDebugRenderers();
		
		stateTime = 0.0f;
		playerCount = 0;
		

	}
	
	public void RemovePlayer(int player){
		
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
	public void CreatePlayers(int numberOfPlayers){
		//Init the update field
		currentMovePlayer = new MovePlayer[numberOfPlayers];
		for(int i = 0; i < numberOfPlayers; i++)
			currentMovePlayer[i] = new MovePlayer();
		
		GameStateManager.allPlayers = PlayerCreator.CreatePlayers(numberOfPlayers, FRICTION, WORLD);
		
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
		for(int b = 0; b < ItemPool.bombs.length; b++)
			ItemPool.bombs[b] = new Bomb(1, B2DVars.BOMB_TIME, WORLD, ItemPool.bombPoolPosition);
		
		for (int f = 0; f < ItemPool.fires.length; f++)
			ItemPool.fires[f] = new Fire(WORLD, ItemPool.firePoolPosition);
		
		for(int b = 0; b < ItemPool.bombPows.length; b++)
			ItemPool.bombPows[b] = new BombPowerUp(WORLD, ItemPool.powPoolPosition);
		
		for(int f = 0; f < ItemPool.firePows.length; f++)
			ItemPool.firePows[f] = new FirePowerUp(WORLD, ItemPool.powPoolPosition);
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

		

		MapBodyBuilder.buildShapes(tileMap, B2DVars.PPM, WORLD, B2DVars.BIT_WALL, "wall", false);  //Build walls
		MapRandomizer mapRand = new MapRandomizer();
		
		boxLayer = mapRand.fillMap(WORLD, tileMap, 50); //Construct random boxes
		boxLayer.setVisible(false);
		tileMap.getLayers().add(boxLayer);
		boxes = mapRand.boxes;
		mapSprite = mapRand.mapSprite;
		boxSprite = mapRand.boxSprite;
		
		
		wallLayer = (TiledMapTileLayer)tileMap.getLayers().get("Pillars");
		// --------------------------*******************

		camera.position.set(center);
		camera.update();
		
	}
	
	public void Update(float dt) {
		CheckAllPlayerMovement();
		
		WORLD.step(timeStep, 1, 1); // Note that step is called with a fixed timestep
		
		UpdateGameObjects(dt);
		
		if(B2DVars.SCREEN_SHAKE)
			shake.update(dt, camera, cameraCenterPos);
		
		SendActiveObjects();

		
	}
	

	
	public void render() {
		
		//System.out.println("Render called! Deltatime: " + Gdx.graphics.getDeltaTime());
		
		// Update statetime and physics
		stateTime += Gdx.graphics.getDeltaTime();
		Update(Gdx.graphics.getDeltaTime());

		if(renderWorld){
		
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
		
		}
		
		//Debug-tools, uncomment for visual aid / bug-hunting:
		//b2dr.render(WORLD, camera.combined);
		//RenderSquares();
		//PrintAllContacts();
		//fps.log();
		

		if(GameStateManager.PlayerWon()){
			GameStateManager.inputBlocked = true;
			GameStateManager.sleepTimer += Gdx.graphics.getDeltaTime();
			winUpdate.playerNr = GameStateManager.playerWinner;
			
			if(!sentWinUpdate){
				server.sendToAllTCP(winUpdate);
				sentWinUpdate = true;
			}
			
			if(renderWorld){

			Texture winScreen;
			
			switch(GameStateManager.playerWinner){
			case 1:
				winScreen = GameStateManager.winPlayer1;
				break;
			case 2:
				winScreen = GameStateManager.winPlayer2;
				break;
			case 3:
				winScreen = GameStateManager.winPlayer3;
				break;
			case 4:
				winScreen = GameStateManager.winPlayer4;
				break;
			default:
				winScreen = GameStateManager.winPlayer1;
			}
			batch.begin();
			batch.draw(winScreen, 6.5f, 3.5f, 192 / B2DVars.PPM, 64 / B2DVars.PPM);
			batch.end();
			
			}
			
			if(GameStateManager.sleepTimer > B2DVars.LEVEL_RESET_SLEEPTIME)
				boxes = GameStateManager.ResetGame(tileMap, WORLD, boxes);
				winUpdate.playerNr = GameStateManager.playerWinner;
				server.sendToAllTCP(winUpdate);
				sentWinUpdate = false;
		}
		
		if(resetGame){
			boxes = GameStateManager.ResetGame(tileMap, WORLD, boxes);
			resetGame = false;
		}

	}
	
	/**
	 * Calls the Update-method for all active game objects.
	 * This step is important in order to keep object data
	 * up to date over a network
	 * @param dt deltatime for updates as float
	 */
	private void UpdateGameObjects(float dt){
		
		for(Player p : GameStateManager.allPlayers){
			if(!p.Dead())
				p.Update();	
		}
		
		for(Bomb b : ItemPool.bombs){
			if(b.active)
				b.Update(dt);
			
			if(b.detonate){
				//System.out.println("detonatePosition: " + b.detonatePosition + " body position" + b.body.getPosition());
				DetonateBomb(b);
			}
		}
		
		for(Fire f : ItemPool.fires){
			if(f.active)
				f.Update(dt);
		}
		
		for(FirePowerUp f : ItemPool.firePows)
				f.Update(dt);
	
		
		for(BombPowerUp b : ItemPool.bombPows)
				b.Update(dt);
		
		
		for(Box b : boxes)
			b.Update(dt);	
	}
	
	private void SendActiveObjects(){
		
		//**BOXES (BoxUpdate)**
		BoxUpdate boxUpdate = new BoxUpdate(); 
		ArrayList<BoxData> activeBoxes = new ArrayList<BoxData>();
		for(Box b : boxes){
			if(b.IsActive())
				activeBoxes.add((BoxData)b.body.getUserData());
		}
		boxUpdate.boxes = activeBoxes;
		
		server.sendToAllTCP(boxUpdate);
		
		//**PLAYERS (PlayerUpdate)**
		PlayerUpdate playerUpdate = new PlayerUpdate();
		ArrayList<PlayerData> activePlayers = new ArrayList<PlayerData>();
		for(Player p : GameStateManager.allPlayers){
			if(!p.Dead())
				activePlayers.add((PlayerData)p.body.getUserData());
		}
		playerUpdate.players = activePlayers;
		server.sendToAllTCP(playerUpdate);
		
		//**BOMBS (BombUpdate)**
		BombUpdate bombUpdate = new BombUpdate();
		ArrayList<BombData> activeBombs = new ArrayList<BombData>();
		for(Bomb b : ItemPool.bombs){
			if(b.active){
				activeBombs.add(b.GetData());
			}	
		bombUpdate.bombs = activeBombs;
		server.sendToAllTCP(bombUpdate);
		
		}
		
		//**Fires (FireUpdate)**
		FireUpdate fireUpdate = new FireUpdate();
		ArrayList<FireData> activeFires = new ArrayList<FireData>();
		for(Fire f : ItemPool.fires){
			if(f.active)
				activeFires.add(f.GetData());
		}
		fireUpdate.fires = activeFires;
		server.sendToAllTCP(fireUpdate);
		
		
		//**Bomb PowerUps && Fire PowerUps (ItemUpdate)**
		ItemUpdate  itemUpdate = new ItemUpdate();
		ArrayList<ItemData> activeItems = new ArrayList<ItemData>();
		
		for(BombPowerUp b : ItemPool.bombPows){
			if(b.active)
				activeItems.add(b.GetData());
		}
		
		for(FirePowerUp f : ItemPool.firePows){
			if(f.active)
				activeItems.add(f.GetData());
		}
		
		itemUpdate.items = activeItems;
		server.sendToAllTCP(itemUpdate);
	}
	
	private void CheckAllPlayerMovement(){
		for(Player p : GameStateManager.allPlayers){
			if(!p.Dead())
				MovePlayer(p, currentMovePlayer[p.GetPlayerNumber() - 1]);
		}
	}
	

	
	private void MovePlayer(Player p, MovePlayer moveData){
	
	if(!GameStateManager.inputBlocked){
	
	switch(moveData.direction){
		case Input.Keys.A: //System.out.println("[SERVER] CALCULATE TO MOVE LEFT");
		if(!p.Dead()){
			p.SetState(State.Left);
			if (Math.abs(p.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
				p.body.applyForceToCenter(new Vector2(-MOVE_FORCE, 0),	true);
		}
	
		break;
		case Input.Keys.D: //System.out.println("[SERVER] CALCULATE TO MOVE RIGHT");
		if(!p.Dead()){
			p.SetState(State.Right);

			if (Math.abs(p.body.getLinearVelocity().x) < MAX_MOVE_SPEED)
				p.body.applyForceToCenter(new Vector2(MOVE_FORCE, 0), true);
		}	
	
		break;
		case Input.Keys.W: //System.out.println("[SERVER] CALCULATE TO MOVE UP");
		if(!p.Dead()){
			p.SetState(State.Up);
			
			if (Math.abs(p.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
				p.body.applyForceToCenter(new Vector2(0, MOVE_FORCE), true);
			}
		break;
		case Input.Keys.S: //System.out.println("[SERVER] CALCULATE TO MOVE DOWN");
		if(!p.Dead()){
			p.SetState(State.Down);

			if (Math.abs(p.body.getLinearVelocity().y) < MAX_MOVE_SPEED)
				p.body.applyForceToCenter(new Vector2(0, -MOVE_FORCE),	true);
		}
		break;
		}
	
		if(((MovePlayer) moveData).bomb == Input.Keys.SPACE)
			ItemPlacer.DropBomb(p);		
	}
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
		for(Box b : boxes){
			if(b.IsActive())
				batch.draw(boxSprite, b.body.getPosition().x, b.body.getPosition().y, 1, 1);
			
		}
	}

	/**
	 * Renders all players that are not dead
	 */
	private void RenderPlayers(){
		for(Player p : GameStateManager.allPlayers){
		if(!p.GetData().Dead()){
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
		for(Bomb b : ItemPool.bombs){
			if(b.active && !b.detonate){
				batch.draw(b.Animation(), b.body.getPosition().x,
				b.body.getPosition().y, 1, 1);
			}
		}
		
	}
	
	
	/**
	 * Draw all active fires according to their current state
	 */
	
	private void RenderFire(){
		float x;
		float y;
		for (Fire f : ItemPool.fires){	
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
		
		for(FirePowerUp f : ItemPool.firePows){
			if(f.active){
				x = f.body.getPosition().x;
				y = f.body.getPosition().y;
				batch.draw(f.Animation(), x, y, 1, 1);
			}
		}
			
		for(BombPowerUp b : ItemPool.bombPows){
			if(b.active){
				x = b.body.getPosition().x;
				y = b.body.getPosition().y;
				batch.draw(b.Animation(), x, y, 1, 1);
			}
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
				System.out.println("detonate: " + x + " " + y);
				float offset = 0.5f;
				float rayx = x + offset;
				float rayy = y + offset;
								
				ItemPlacer.SetFire(x, y, WORLD);

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

								
								if(!obstacleHitUp){
									fireU = ItemPlacer.SetFire(x, y + f, WORLD);
									fireU.state = f == firePower ? Fire.State.Up : Fire.State.Vertical;
								}
							}
						
							if(!obstacleHitDown){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx, rayy - f + 1), new Vector2(rayx, rayy - f));
								
								if(fRay.hitPoint != null)
									obstacleHitDown = fRay.hasCollided && Math.abs(fRay.hitPoint.y - (rayy - f + 1)) < 1;
																
							
								if(!obstacleHitDown){
									fireD = ItemPlacer.SetFire(x, y - f, WORLD);
									fireD.state = f == firePower ? Fire.State.Down : Fire.State.Vertical;
								}
							}
							
							if(!obstacleHitRight){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx + f - 1, rayy), new Vector2(rayx + f, rayy));
								
								if(fRay.hitPoint != null)
									obstacleHitRight = fRay.hasCollided && Math.abs(fRay.hitPoint.x - (rayx + f - 1)) < 1;
								

							
								if(!obstacleHitRight){
									fireR = ItemPlacer.SetFire(x + f, y, WORLD);
									fireR.state = f == firePower ? Fire.State.Right : Fire.State.Horizontal;
								}
							}	
							
							if(!obstacleHitLeft){
								fRay.ResetCollisionCheck();
								WORLD.rayCast(fRay, new Vector2(rayx - f + 1, rayy), new Vector2(rayx - f, rayy));
								
								if(fRay.hitPoint != null)
									obstacleHitLeft = fRay.hasCollided && Math.abs(fRay.hitPoint.x - (rayx - f + 1)) < 1;
								

							
								if(!obstacleHitLeft){	
									fireL = ItemPlacer.SetFire(x - f, y, WORLD);
									fireL.state = f == firePower ? Fire.State.Left : Fire.State.Horizontal;
								}
							}
							
					}
				
				//Tell all clients to initiate shake effect
				ShakeUpdate shakeUpdate = new ShakeUpdate();
				shakeUpdate.shakeFactor = B2DVars.SHAKE_TIME * firePower;	
				server.sendToAllTCP(shakeUpdate);
				
				shake.shake(B2DVars.SHAKE_TIME * firePower); //Screen shakes proportionately to fire power
				b.detonate = false;
				b.GetData().UnflagDetonation();
			
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

