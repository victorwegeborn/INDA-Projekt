package com.mygdx.game;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.mygdx.NGame.NConfig;
import com.mygdx.NGame.NNetwork;
import com.mygdx.NGame.NNetwork.BombUpdate;
import com.mygdx.NGame.NNetwork.BoxUpdate;
import com.mygdx.NGame.NNetwork.FireUpdate;
import com.mygdx.NGame.NNetwork.ItemUpdate;
import com.mygdx.NGame.NNetwork.MovePlayer;
import com.mygdx.NGame.NNetwork.PlayerUpdate;
import com.mygdx.NGame.NNetwork.ShakeUpdate;
import com.mygdx.game.Player.State;
import com.mygdx.gameData.BombData;
import com.mygdx.gameData.BoxData;
import com.mygdx.gameData.FireData;
import com.mygdx.gameData.ItemData;
import com.mygdx.gameData.PlayerData;

public class GameClient implements Screen {
	SpriteBatch batch;
	Texture img;
	
	private static boolean renderWorld = false;
	
	private static float timeStep = 1f / 60f; // Interval of physics simulation
	
	//Debug-tools
	private Box2DDebugRenderer b2dr;
	private ShapeRenderer sRend;
	static float sWidth = 0.2f, sHeight = 0.2f;
	static ArrayList<Square> squares = new ArrayList<Square>(); 

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
	private static int[] bottomLayers = {0 , 1} ; 	 //0: Floor, 1: Pillars
	private static int[] topLayers = {2};			 //2: All sprites to render above everything else
	

	//Animations objects
	private Texture mapSprite;
	private TextureRegion boxSprite;
	private Animation bombAnim, firePowAnim, bombPowAnim;
	//
	
	//Animation for a player
	static class PlayerAnimator{
		TextureAtlas spriteSheet;
		
		Animation upAnim, upIdleAnim, downAnim, downIdleAnim,
		leftAnim, leftIdleAnim, rightAnim, rightIdleAnim;
	}
	
	static class FireAnimator{
		TextureAtlas spriteSheet;
		
		Animation fireUp, fireDown, fireLeft, 
		fireRight, fireMid, fireVert, fireHori;

	}
	
	private PlayerAnimator[] playerAnim;
	
	private FireAnimator fireAnim;
	
	//Updates received from server
	private BoxUpdate activeBoxes;
	private PlayerUpdate activePlayers;
	private BombUpdate activeBombs;
	private FireUpdate activeFires;
	private ItemUpdate activeItems;
	

	float stateTime;
	
	
	private Client client;
	private static final String hostIP = "localhost";
	private static MovePlayer mp = new MovePlayer();
	private static FPSLogger fps = new FPSLogger();

	
	
	
	public GameClient(){
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
		
		SetupNetworkListener();
		
		SetupCamera();
		
		SetupSpriteBatch();
			
		SetupMap("maps/level.tmx");
		
		InitializeObjectAnimations();
		
		InitializePlayerAnimations();
		
		InitializeUpdateFields();
			
		SetupDebugRenderers();
		
		stateTime = 0.0f;
		

	}
	
	private void SetupNetworkListener(){
		
		//========= CLIENT ==========
		client = new Client(32764, 4096);
		client.start();
		
		//Register packets
		NNetwork.register(client);
		
		// THREADED LISTENER
		client.addListener(new ThreadedListener(new Listener() {
			
			public void connected (Connection c) {
			}
			
			public void disconnect (Connection c) {
				
			}

			public void received (Connection c, Object o) {
				if(o instanceof BoxUpdate){
					//System.out.println("Boxes received!");
					activeBoxes = (BoxUpdate)o;
				}
				
				if(o instanceof PlayerUpdate){
					//System.out.println("Players received!");
					activePlayers = (PlayerUpdate)o;
				}
				
				if(o instanceof BombUpdate){
					//System.out.println("Bombs received!");
					activeBombs = (BombUpdate)o;
				}
				
				if(o instanceof FireUpdate){
					//System.out.println("Fires received!");
					activeFires = (FireUpdate)o;
				}
				
				if(o instanceof ItemUpdate){
					//System.out.println("Items received!");
					activeItems = (ItemUpdate)o;
				}
				
				if(o instanceof ShakeUpdate){
					//System.out.println("Screen shake received!");
					ShakeUpdate s = (ShakeUpdate)o;
					shake.shake(s.shakeFactor);
				}

			}
		}));
		
		
		try {
			client.connect(5000, hostIP, NConfig.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//========= CLIENT END ==========
		
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
	
	private void InitializeUpdateFields(){
		activeBoxes = new BoxUpdate();
		activeBoxes.boxes = new ArrayList<BoxData>();
		
		activePlayers = new PlayerUpdate();
		activePlayers.players = new ArrayList<PlayerData>();
		
		activeFires = new FireUpdate();
		activeFires.fires = new ArrayList<FireData>();
	}
	
	

	private void InitializeObjectAnimations(){
		TextureAtlas spriteSheet;
		
		//Grab box sprite from current level
		TiledMapTileLayer t = (TiledMapTileLayer)tileMap.getLayers().get("Boxes");
		boxSprite = t.getCell(0, 0).getTile().getTextureRegion();
		
		spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/items.txt"));
		bombAnim = new Animation(1/12f, spriteSheet.findRegions("bomb"));
		bombAnim.setPlayMode(PlayMode.LOOP);
		
		fireAnim = CreateFireAnimations();
		
		spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/powerups.txt"));
		
		firePowAnim = new Animation(1/12f, spriteSheet.findRegions("firepowerup"));
		firePowAnim.setPlayMode(PlayMode.LOOP);
		
		bombPowAnim = new Animation(1/12f, spriteSheet.findRegions("bombpowerup"));
		bombPowAnim.setPlayMode(PlayMode.LOOP);
	}
		
	private FireAnimator CreateFireAnimations(){
		TextureAtlas sheet = new TextureAtlas(Gdx.files.internal("sprites/items/bombfire.txt"));
		float framerate = 1/24f; 
		FireAnimator f = new FireAnimator();
		
		f.spriteSheet = sheet;
		
		f.fireUp = new Animation(framerate, f.spriteSheet.findRegions("fireup"));
		f.fireUp.setPlayMode(PlayMode.NORMAL);
						
		f.fireDown = new Animation(framerate, f.spriteSheet.findRegions("firedown"));
		f.fireDown.setPlayMode(PlayMode.NORMAL);
			
		f.fireLeft = new Animation(framerate, f.spriteSheet.findRegions("fireleft"));
		f.fireLeft.setPlayMode(PlayMode.NORMAL);
			
		f.fireRight = new Animation(framerate, f.spriteSheet.findRegions("fireright"));
		f.fireRight.setPlayMode(PlayMode.NORMAL);
			
		f.fireMid = new Animation(framerate, f.spriteSheet.findRegions("firemid"));
		f.fireMid.setPlayMode(PlayMode.NORMAL);
			
		f.fireVert = new Animation(framerate, f.spriteSheet.findRegions("firevert"));
		f.fireVert.setPlayMode(PlayMode.NORMAL);
			
		f.fireHori = new Animation(framerate, f.spriteSheet.findRegions("firehori"));
		f.fireHori.setPlayMode(PlayMode.NORMAL);
			
		return f;
			
	}
		
	private void InitializePlayerAnimations() {
		playerAnim = new PlayerAnimator[4];
		
		for(int i = 0; i< 4; i++)
			playerAnim[i] = CreatePlayerAnimation(i + 1);
		
	}
	
	private PlayerAnimator CreatePlayerAnimation(int playernumber){

		String sheetFile = "sprites/characters/ninja" + playernumber + ".txt";
		
		PlayerAnimator p = new PlayerAnimator();
		
		float framerate = 1/24f;
		p.spriteSheet = new TextureAtlas(Gdx.files.internal(sheetFile));
		
		p.upAnim = new Animation(framerate, p.spriteSheet.findRegions("up"));
		p.upAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		p.upIdleAnim = new Animation(framerate, p.spriteSheet.findRegion("up", 4));

		
		p.downAnim = new Animation(framerate, p.spriteSheet.findRegions("down"));
		p.downAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		p.downIdleAnim = new Animation(framerate, p.spriteSheet.findRegion("down", 4));

		p.rightAnim = new Animation(framerate, p.spriteSheet.findRegions("side"));
		p.rightAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		p.rightIdleAnim = new Animation(framerate * 1.25f, p.spriteSheet.findRegion("side", 4));
		
		
		/**
		 * Left running animation needs to be mirrored
		 */
		Array<TextureAtlas.AtlasRegion> left = p.spriteSheet.findRegions(("side"));
		
		for(TextureAtlas.AtlasRegion a : left)
			a.flip(true, false);
		
		p.leftAnim = new Animation(framerate, left);
		p.leftAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		p.leftIdleAnim = new Animation(framerate * 1.25f, left.get(3));
		
		return p;
	}

	

	private void SetupMap(String map){
		
		// Map loading and rendering*******************
		tileMap = new TmxMapLoader().load(Gdx.files.internal(map).path());
		
		batch_tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap, 1 / 32f);
		
		TiledMapTileLayer layer0 = (TiledMapTileLayer) tileMap.getLayers().get(0);
		
		
		
		Vector3 center = new Vector3(layer0.getWidth() * layer0.getTileWidth()
				/ (2 * 32f), layer0.getHeight() * layer0.getTileHeight()
				/ (2 * 32f), 0);
		
		cameraCenterPos = new Vector2(center.x, center.y);

		camera.position.set(center);
		camera.update();
		
	}
	
	public void Update(float dt) {
		
		SendInputs();
		
		
//		try {
//			client.update(5000);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		if(B2DVars.SCREEN_SHAKE)
			shake.update(dt, camera, cameraCenterPos);

	}
	

	@Override
	public void render(float dt) {
		Update(dt);
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)){
			Gdx.app.exit();
		}
		
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
	 * NOTE: When rendering, use local variables to avoid
	 * concurrency issues
	 */
	private void RenderBoxes(){
		ArrayList<BoxData> boxes = activeBoxes.boxes;		
		for(BoxData b : boxes){
			batch.draw(boxSprite, b.PosX(), b.PosY(), 1, 1);
		}
	
	}

	/**
	 * Render players by interpreting PlayerUpdate packets
	 */
	private void RenderPlayers(){
		int player;
		boolean movingX;
		boolean movingY;
		
		PlayerUpdate playerUpdate = activePlayers;
		
		for(PlayerData p : activePlayers.players){
			player = p.PlayerNumber() - 1; //Index of this player in playerAnim
			
			movingX = Math.abs(p.VelX()) > 0.01f;
			movingY = Math.abs(p.VelY()) > 0.01f;
			
			switch(p.State()){
				case B2DVars.PLAYER_UP:
					if(movingY)
						batch.draw(playerAnim[player].upAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
					else
						batch.draw(playerAnim[player].upIdleAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
			
					break;
			
				case B2DVars.PLAYER_DOWN:
					if(movingY)
						batch.draw(playerAnim[player].downAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
					else
						batch.draw(playerAnim[player].downIdleAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
			
					break;
			
				case B2DVars.PLAYER_LEFT:
					if(movingX)
						batch.draw(playerAnim[player].leftAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
					else
						batch.draw(playerAnim[player].leftIdleAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
				
					break;
			
				case B2DVars.PLAYER_RIGHT:
					if(movingX)
						batch.draw(playerAnim[player].rightAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);
					else
						batch.draw(playerAnim[player].rightIdleAnim.getKeyFrame(stateTime, true), p.PosX(), p.PosY(), 1, 1);

					break;
			}
		}
			
	}
	
	
	/**
	 * Render bombs by interpreting BombUpdate packets
	 */
	private void RenderBombs(){
		ArrayList<BombData> bombs = activeBombs.bombs;
		for(BombData b : bombs){
			batch.draw(bombAnim.getKeyFrame(stateTime, true), b.PosX(), b.PosY(), 1, 1);
		}
	}
	
	
	/**
	 * Draw all active fires according to their current state
	 */
	
	private void RenderFire(){
		ArrayList<FireData> fires = activeFires.fires;
		for(FireData f : fires){
			switch(f.GetState()){
			
			case B2DVars.FIRE_UP:
				batch.draw(fireAnim.fireUp.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
			case B2DVars.FIRE_DOWN:
				batch.draw(fireAnim.fireDown.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
				
			case B2DVars.FIRE_LEFT:
				batch.draw(fireAnim.fireLeft.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
				
			case B2DVars.FIRE_RIGHT:
				batch.draw(fireAnim.fireRight.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
				
			case B2DVars.FIRE_MID:
				batch.draw(fireAnim.fireMid.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
				
			case B2DVars.FIRE_VERTICAL:
				batch.draw(fireAnim.fireVert.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;
				
			case B2DVars.FIRE_HORIZONTAL:
				batch.draw(fireAnim.fireHori.getKeyFrame(f.GetAnimTimer()), f.PosX(), f.PosY(), 1, 1);
				break;			
			}
		}	
	}
	
	private void RenderItems(){
		ArrayList<ItemData> items = activeItems.items;
		for(ItemData i : items){
			switch(i.ItemType()){
			case B2DVars.BOMB_POWERUP: 
				batch.draw(bombPowAnim.getKeyFrame(i.AnimTimer(), true), i.PosX(), i.PosY(), 1, 1);
				break;
			case B2DVars.FIRE_POWERUP:
				batch.draw(firePowAnim.getKeyFrame(i.AnimTimer(), true), i.PosX(), i.PosY(), 1, 1);
				break;
				
			}		
		}
	}

	
	
	private void SendInputs(){
		
		boolean changed = false;
		MovePlayer mpUpdate = new MovePlayer();
					
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
				mpUpdate.bomb = Input.Keys.SPACE;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.A) && !changed){		
				mpUpdate.direction = Input.Keys.A;
			}
			
			if (Gdx.input.isKeyPressed(Input.Keys.D) && !changed) {			
				mpUpdate.direction = Input.Keys.D;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.W) && !changed) {				
					mpUpdate.direction = Input.Keys.W;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.S) && !changed) {
					mpUpdate.direction = Input.Keys.S;
			}
			
			if(mp.direction != mpUpdate.direction || mp.bomb != mpUpdate.bomb) {
				client.sendTCP(mpUpdate);
				mp = mpUpdate;
			}
			
	}
	
	
		


	public void show() {
			// TODO Auto-generated method stub
			
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

