package com.mygdx.gameMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.B2DVars;

public class MenuManager {
	
	public static float framerate = 1/4f;
	public static TextureAtlas mainMenuSheet = new TextureAtlas(Gdx.files.internal("sprites/texts/menuitems.txt"));
	public static TextureAtlas lobbySheet = new TextureAtlas(Gdx.files.internal("sprites/texts/lobbyitems.txt"));

	public static Animation hostIdle = new Animation(framerate, mainMenuSheet.findRegions("hostidle"));
	public static Animation hostActive = new Animation(framerate, mainMenuSheet.findRegions("hostactive"));
	
	public static Animation joinIdle = new Animation(framerate, mainMenuSheet.findRegions("joinidle"));
	public static Animation joinActive = new Animation(framerate, mainMenuSheet.findRegions("joinactive"));

	public static Animation quitIdle = new Animation(framerate, mainMenuSheet.findRegions("quitidle"));
	public static Animation quitActive = new Animation(framerate, mainMenuSheet.findRegions("quitactive"));
	
	public static TextureRegion logo = mainMenuSheet.findRegion("mainlogobw");
	public static TextureRegion logoLarge = mainMenuSheet.findRegion("mainlogobwlarge");
	
	public static TextureRegion lobbyBanner = lobbySheet.findRegion("lobbybanner");
	public static Animation zuluIdle = new Animation(framerate, lobbySheet.findRegions("zuluidle"));
	public static Animation zuluActive = new Animation(framerate, lobbySheet.findRegions("zuluactive"));
	public static Animation backIdle = new Animation(framerate, lobbySheet.findRegions("backidle"));
	public static Animation backActive = new Animation(framerate, lobbySheet.findRegions("backactive"));


	/**
	 * Returns the specified player status texture 
	 * for use in the lobby menu
	 */
	public static TextureRegion GetPlayerStatusTexture(int player, int status){
		if(player < 1 || player > 4)
			player = 1;
		
		String filePath = "p" + player;
		
		switch(status){
			case B2DVars.PLAYER_EMPTY:
				filePath += "empty";
				break;
			case B2DVars.PLAYER_CONNECTED:
				filePath += "connected";
				break;
			case B2DVars.PLAYER_READY:
				filePath += "ready";
				break;
			default:
				filePath += "empty";
		}
		return new TextureRegion(lobbySheet.findRegion(filePath));		
	}	

}
