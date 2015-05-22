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
	

	
	//Integer representations
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
	
	public static final int BOXLAYER_INDEX = 5;
	
	public static final int PLAYER_EMPTY = 0;
	public static final int PLAYER_CONNECTED = 1;
	public static final int PLAYER_READY = 2;

	public static final int ERROR_GAME_FULL = 1;
	public static final int ERROR_UNKNOWN = 2;
	public static final int ERROR_ALL_PLAYERS_LEFT = 3;
	public static final int ERROR_HOST_LEFT = 4;

	
	
	
	//Game config values:
	public static final boolean SCREEN_SHAKE = true;
	public static final float SHAKE_TIME = 0.1f;
	public static final float SHAKE_AMPLITUDE = 0.1f;  //Default 0.1f
	public static final int BOX_DENSITY = 50;
	public static final int LEVEL_RESET_SLEEPTIME = 4;
	public static final int MAX_PLAYERS = 4;
	
	/**
	 * Lower this value if animations are choppy during network play.
	 * This will increase server side cpu usage. Default value 16.66667
	 */
	public static final long NETWORK_UPDATE_CYCLE_TIME = (long)16.66667;
	public static final int NETWORK_CLIENT_BUFFERSIZE = 262112;
	
	public static boolean SOUND = false;
	
	public static final String NETWORK_DEFAULT_PORT_STRING = "54555";
	public static final int NETWORK_DEFAULT_PORT_INT = 54555;
	
	public static final int VIRTUAL_WIDTH = 16;
	public static final int VIRTUAL_HEIGHT = 9;

	

	

	
	public static final float BOMB_TIME = 3f;
	public static final int DROP_RATE = 50; //The drop rate for powerups in percent
	//

	

}
