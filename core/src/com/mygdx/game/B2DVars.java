package com.mygdx.game;

public final class B2DVars {
	
	//PPM = Pixels per meter. Conversion-variable. 
	public static final float PPM = 32f;
	
	//Collision categories
	public static final short BIT_PLAYER = 2;
	public static final short BIT_WALL = 4;
	public static final short BIT_BOX = 8;
	public static final short BIT_ITEM = 16;	
	public static final short BIT_EVERYTHING = -1;
	public static final short BIT_FRICTION = 32;
	public static final short BIT_BOMB = 64;
	public static final short BIT_FIRE = 128;
	
	
	public static final int MAX_FIREPOWER = 6;
	public static final int MAX_SPEED = 3;
	public static final int MAX_BOMBCAPACITY = 5;
	

	
	//State integer representations
	public static final int PLAYER_UP = 1;
	public static final int PLAYER_DOWN = 2;
	public static final int PLAYER_LEFT = 3;
	public static final int PLAYER_RIGHT = 4;
	public static final int PLAYER_DEAD = 5;
	
	public static final int FIRE_UP = 1;
	public static final int FIRE_DOWN = 2;
	public static final int FIRE_LEFT = 3;
	public static final int FIRE_RIGHT = 4;
	public static final int FIRE_HORIZONTAL = 5;
	public static final int FIRE_VERTICAL = 6;
	public static final int FIRE_MID = 7;
	
	public static final int BOMB_POWERUP = 1;
	public static final int FIRE_POWERUP = 2;
	
	
	//Game config values:
	public static final boolean SCREEN_SHAKE = true;
	public static final float SHAKE_TIME = 0.1f;
	public static final float SHAKE_AMPLITUDE = 0.1f;  //Default 0.1f
	

	
	public static final float BOMB_TIME = 3f;
	public static final int DROP_RATE = 50; //The drop rate for powerups in percent
	//

	

}
