package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;


public class Player {
public static final float MOVE_SPEED = 4f;
public static final float DAMPING = 0.8f;

public enum State{
	Idle, Up, Down, Left, Right, Dead
}

private State state;

private boolean _isFacingRight,_isFacingUp;
private float stateTime;
private Vector3 position, velocity;
private float width, height;

//Player sprites and animation
private TextureAtlas spriteSheet;
private Animation upAnim;
private Animation downAnim;
private Animation rightAnim;
private Animation leftAnim;

//Player game attributes
private int bombCount;
private int fireLength;
private int speedCount;


/*
 * Creates a new player.
 * @param Vector3 position: denotes starting position
 * @param boolean player1: if true = player 1, false = player 2
 */

public Player(boolean player1){
	
	bombCount = 1;
	fireLength = 1;
	speedCount = 1;
	
	velocity = new Vector3();
	_isFacingRight = true;
	stateTime = 0;
	_isFacingUp = true;
	
	//TODO: Default idle states for player 1 and player 2
	state = State.Down;
	
	//TODO: If player1 == false, construct using player 2 sprite sheet
	//spriteSheet = player1 == true ? [player1sheet] : [player2sheet];
	
	
	
	
	//Construct animations for player--***
	float framerate = 1/24f;
	spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/characters/ninja1.txt"));
	
	upAnim = new Animation(framerate, spriteSheet.findRegions("up"));
	upAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

	
	downAnim = new Animation(framerate, spriteSheet.findRegions("down"));
	downAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	
	rightAnim = new Animation(framerate * 1.25f, spriteSheet.findRegions("side"));
	rightAnim.setPlayMode(Animation.PlayMode.LOOP);
	
	Array<TextureAtlas.AtlasRegion> left = spriteSheet.findRegions(("side"));
	for(TextureAtlas.AtlasRegion a : left)
		a.flip(true, false);
	
	leftAnim = new Animation(framerate * 1.25f, left);
	leftAnim.setPlayMode(Animation.PlayMode.LOOP);
	//---------------------------------***
	
	
	
	
	}

	public void SetState(State state){
		this.state = state;
	}
	
	public void ClearState(){
		state = null;
	}
	
	public Animation Animation(){
		switch(state){
		case Up:
			return upAnim;
		case Down:
			return downAnim;
		case Left:
			return leftAnim;
		case Right:
			return rightAnim;
		default:
			return downAnim;
		}
		
	}


}
