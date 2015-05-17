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
	

}
