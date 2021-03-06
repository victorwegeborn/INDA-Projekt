package com.mygdx.game;
import static com.mygdx.game.B2DVars.PPM;
import static com.mygdx.game.CoreGame.WORLD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gameData.PlayerData;


public class Player {
public static final float MOVE_SPEED = 0.05f;
public static final float DAMPING = 0.8f;

public enum State{
	Idle, Up, Down, Left, Right, Dead
}

private State state;
private PlayerData data;
public Body body;

private boolean remove;

private int playerNumber;

//Player sprites and animation
private TextureAtlas spriteSheet;
private Animation upAnim, upIdleAnim, downAnim, downIdleAnim,
leftAnim, leftIdleAnim, rightAnim, rightIdleAnim;

//Player game attributes
private int bombCapacity;
private int droppedBombs;
private int activeBombs;
private Iterator bombIterator;
private int firePower;
private int speedCount;
private Vector2 spawnPosition;

public boolean immortal;

private boolean killed;

/*
 * Creates a new player.
 * @param Vector2 position: denotes starting position
 * @param boolean player1: if true = player 1, false = player 2
 */

public Player(int player, Vector2 position, World world){
	
	if(player < 1)
		player = 1;
	
	if(player > 4)
		player = 4;
	
	playerNumber = player;
	
	immortal = false;
	
	spawnPosition = CoordinateConverter.quantizePositionToGrid(position);
	droppedBombs = 0;
	bombCapacity = 1;
	firePower = 1;
	speedCount = 1;
	killed = false;
	
	activeBombs = 0;
	
	//Create player body
	BodyDef bdef = new BodyDef();
	bdef.position.set(spawnPosition);
	bdef.type = BodyType.DynamicBody;
	bdef.allowSleep = false;
	
	FixtureDef fdef = new FixtureDef();
	
	PolygonShape shape = new PolygonShape();	//The box collider
	shape.setAsBox(0.3f, 0.25f);				//The box collider
	fdef.shape = shape;
	//Collision mask---* 
	fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
	fdef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_WALL | 
			B2DVars.BIT_ITEM | B2DVars.BIT_FIRE | B2DVars.BIT_BOMB;
	//-----------------*
	body = world.createBody(bdef);
	body.createFixture(fdef).setUserData("player");
	shape.dispose();

	//Initialize state to down
	state = State.Down;
	
	//Create player data. This will be sent as packets via kryonet
	data = new PlayerData((byte)B2DVars.BIT_PLAYER, (byte)player, body.getPosition().x, body.getPosition().y, true);
	body.setUserData(data); // Store reference to data object in user data for external referencing

	CreateAnimations(playerNumber);
	}

	private void CreateAnimations(int playernr){

		//TODO: If player1 == false, construct using player 2 sprite sheet
		//i.e. using spriteSheet = player1 == true ? [player1sheet] : [player2sheet];
		//If more players are to be integrated, consider a switch statement
		
		String sheetFile = "sprites/characters/ninja" + playernr + ".txt";
		
		float framerate = 1/24f;
		spriteSheet = new TextureAtlas(Gdx.files.internal(sheetFile));
		
		upAnim = new Animation(framerate, spriteSheet.findRegions("up"));
		upAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		upIdleAnim = new Animation(framerate, spriteSheet.findRegion("up", 4));

		
		downAnim = new Animation(framerate, spriteSheet.findRegions("down"));
		downAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		downIdleAnim = new Animation(framerate, spriteSheet.findRegion("down", 4));

		rightAnim = new Animation(framerate, spriteSheet.findRegions("side"));
		rightAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		rightIdleAnim = new Animation(framerate * 1.25f, spriteSheet.findRegion("side", 4));
		
		
		/**
		 * Left running animation needs to be mirrored
		 */
		Array<TextureAtlas.AtlasRegion> left = spriteSheet.findRegions(("side"));
		
		for(TextureAtlas.AtlasRegion a : left)
			a.flip(true, false);
		
		leftAnim = new Animation(framerate, left);
		leftAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		leftIdleAnim = new Animation(framerate * 1.25f, left.get(3));
		
	}
	
	public void SetBombCapacity(int c){
		if(c < 1)
			c = 1;
		
		if(c > B2DVars.MAX_BOMBCAPACITY)
			c = B2DVars.MAX_BOMBCAPACITY;
		
		bombCapacity = c;
	}
	
	public void IncrementBombCapacity(){
		if(bombCapacity < B2DVars.MAX_BOMBCAPACITY){
			bombCapacity++;
			
		data.IncrementFirePower();
		}
	}
	
	public void SetFirePower(int f){
		if(f < 1)
			f = 1;
		
		if(f >= B2DVars.MAX_FIREPOWER)
			f = B2DVars.MAX_FIREPOWER;
		
		firePower = f;
	}
	
	public void IncrementFirePower(){
		if(firePower < B2DVars.MAX_FIREPOWER){
			firePower++;
		
		data.IncrementFirePower();
		}
	}
	
	public int GetPlayerNumber(){
		return playerNumber;
	}
	
	public int GetFirePower(){
		return firePower;
	}
	
	public void IncrementActiveBombs(){
		activeBombs++;
	}
	
	public void DecrementActiveBombs(){
		activeBombs--;
	}
	
	public boolean CanDropBomb(){
		return activeBombs < data.GetBombCapacity();
	}

	public void SetState(State state){
		this.state = state;
	}
	
	public void Remove(){
		remove = true;
	}
	
	public boolean RemoveThis(){
		return remove;
	}
	
	public void ClearState(){
		state = null;
	}
	
	public void Update(){
		killed = data.Dead();
		UpdatePlayerData();
	}
	
	public void Reset(){
		data.SetActive(true);
		killed = false;
		firePower = 1;
		bombCapacity = 1;
		data.Reset();
		body.setActive(true);
		body.setTransform(spawnPosition, 0);
		activeBombs = 0;
	}
	
	
	private void UpdatePlayerData(){
		
		data.SetActive(!killed);
		
		data.SetPosition(body.getPosition().x - 0.5f, body.getPosition().y - 0.3f);
		
		data.SetVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y);
		
		switch(state){
		case Up:
			data.SetState((byte)B2DVars.PLAYER_UP);
			break;
		case Down:
			data.SetState((byte)B2DVars.PLAYER_DOWN);
			break;
		case Left:
			data.SetState((byte)B2DVars.PLAYER_LEFT);
			break;
		case Right:
			data.SetState((byte)B2DVars.PLAYER_RIGHT);
			break;
		}
		
		
	}

	
	/**
	 * Check all bombs for activity
	 */
	
	
	public Animation Animation(){
		switch(state){
		case Up:
			return body.getLinearVelocity().y > 0 ? upAnim : upIdleAnim;
		case Down:
			return body.getLinearVelocity().y < 0 ? downAnim : downIdleAnim;
		case Left:
			return body.getLinearVelocity().x < 0 ? leftAnim : leftIdleAnim;
		case Right:
			return body.getLinearVelocity().x > 0 ? rightAnim : rightIdleAnim;
		default:
			return downAnim;
		}	
	}
	
	public void Kill(){
		killed = true;
	}
	
	public boolean Dead(){
		return killed;
	}
	
	public PlayerData GetData(){
		return data;
	}
}
