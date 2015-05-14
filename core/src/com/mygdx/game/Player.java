package com.mygdx.game;
import static com.mygdx.game.B2DVars.PPM;
import static com.mygdx.game.INDAGame.WORLD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;


public class Player {
public static final float MOVE_SPEED = 0.05f;
public static final float DAMPING = 0.8f;

public enum State{
	Idle, Up, Down, Left, Right, Dead
}

private State state;

public Body body;

//Player sprites and animation
private TextureAtlas spriteSheet;
private Animation upAnim, upIdleAnim, downAnim, downIdleAnim,
leftAnim, leftIdleAnim, rightAnim, rightIdleAnim;

//Player game attributes
private int bombCount;
private int fireLength;
private int speedCount;


/*
 * Creates a new player.
 * @param Vector2 position: denotes starting position
 * @param boolean player1: if true = player 1, false = player 2
 */

public Player(boolean player1, Vector2 position){
	
	bombCount = 1;
	fireLength = 1;
	speedCount = 1;
	
	//Create player body
	BodyDef bdef = new BodyDef();
	bdef.position.set(position);
	bdef.type = BodyType.DynamicBody;
	
	FixtureDef fdef = new FixtureDef();
	
	PolygonShape shape = new PolygonShape();	//The box collider
	shape.setAsBox(0.25f, 0.3f);				//The box collider
	fdef.shape = shape;
	//Collision mask---* 
	fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
	fdef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_WALL | B2DVars.BIT_ITEM | B2DVars.BIT_EVERYTHING;;
	//-----------------*
	body = WORLD.createBody(bdef);
	body.createFixture(fdef).setUserData("player");
	shape.dispose();

	//Initialize state to down
	state = State.Down;
	
	//TODO: If player1 == false, construct using player 2 sprite sheet
	//i.e. using spriteSheet = player1 == true ? [player1sheet] : [player2sheet];
	//If more players are to be integrated, consider a switch statement
	
	
	
	
	//Construct animations for player--***
	float framerate = 1/24f;
	spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/characters/ninja1.txt"));
	
	upAnim = new Animation(framerate, spriteSheet.findRegions("up"));
	upAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	upIdleAnim = new Animation(framerate, spriteSheet.findRegion("up", 4));

	
	downAnim = new Animation(framerate, spriteSheet.findRegions("down"));
	downAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	downIdleAnim = new Animation(framerate, spriteSheet.findRegion("down", 4));

	rightAnim = new Animation(framerate, spriteSheet.findRegions("side"));
	rightAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	rightIdleAnim = new Animation(framerate * 1.25f, spriteSheet.findRegion("side", 4));

	
	Array<TextureAtlas.AtlasRegion> left = spriteSheet.findRegions(("side"));
	for(TextureAtlas.AtlasRegion a : left)
		a.flip(true, false);
	
	leftAnim = new Animation(framerate, left);
	leftAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
	leftIdleAnim = new Animation(framerate * 1.25f, left.get(3));

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


}
