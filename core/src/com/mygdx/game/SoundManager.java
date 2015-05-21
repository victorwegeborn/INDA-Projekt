package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
	
	
	//==== GAME SOUNDS ====
	public static Sound walk1 = Gdx.audio.newSound(Gdx.files.internal("sounds/walk1.wav"));
	public static Sound walk2 = Gdx.audio.newSound(Gdx.files.internal("sounds/walk2.wav"));
	public static Sound walk3 = Gdx.audio.newSound(Gdx.files.internal("sounds/walk3.wav"));
	public static Sound walk4 = Gdx.audio.newSound(Gdx.files.internal("sounds/walk4.wav"));
	public static Sound walk5 = Gdx.audio.newSound(Gdx.files.internal("sounds/walk5.wav"));
	public static Sound powerup1 = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup1.wav"));
	public static Sound powerup2 = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup2.wav"));
	public static Sound dropbomb = Gdx.audio.newSound(Gdx.files.internal("sounds/dropbomb.wav"));
	public static Sound explosion1 = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion1.wav"));

	
	// ==== MUSIC ====
	public static Sound menubg = Gdx.audio.newSound(Gdx.files.internal("sounds/songs/menu.mp3"));
	public static Sound bg1 = Gdx.audio.newSound(Gdx.files.internal("sounds/songs/1.mp3"));

	
	// ==== MENU SOUNDS ====
	public static Sound select1 = Gdx.audio.newSound(Gdx.files.internal("sounds/select1.wav"));


	public static void StopAllBG(){
		menubg.stop();
		bg1.stop();
	}
	
}
