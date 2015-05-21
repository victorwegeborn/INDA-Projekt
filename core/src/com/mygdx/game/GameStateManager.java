package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gameData.BoxData;
import com.mygdx.gameData.ItemData;

public class GameStateManager {
	
	
	public static boolean inputBlocked;
	public static final Texture winPlayer1 =  new Texture(Gdx.files.internal("sprites/texts/win1.png"));
	public static final Texture winPlayer2 = new Texture(Gdx.files.internal("sprites/texts/win2.png"));
	public static final Texture winPlayer3 = new Texture(Gdx.files.internal("sprites/texts/win3.png"));
	public static final Texture winPlayer4 = new Texture(Gdx.files.internal("sprites/texts/win4.png"));
	public static int playerWinner = -1;


	private static final float sleepTime = B2DVars.LEVEL_RESET_SLEEPTIME;
	public static float sleepTimer = 0f;
	public static ArrayList<Player> allPlayers;
	
	public static Array<Box> ResetGame(TiledMap tileMap, World world, Array<Box> boxes){
		playerWinner = -1;
		sleepTimer = 0f;
		inputBlocked = false;
		
		//**RESET PLAYER STATS**//
		for(Player p : allPlayers)
			p.Reset();
		
		
		//**REMOVE BOXES FROM WORLD & RESET ITEMS**//
		Array<Body> bodies = new Array<Body>(); 
		world.getBodies(bodies);	
		for(Body b : bodies){
			if(b.getUserData() instanceof BoxData){
				world.destroyBody(b);
			}
			if(b.getUserData() instanceof ItemData){
				ItemData i = (ItemData)b.getUserData();
				i.FlagReset();
			}
		}
		
		
		boxes.clear();
		System.out.println("Boxes after clearing: " + boxes.size);
		tileMap.getLayers().remove(B2DVars.BOXLAYER_INDEX);
		
		
		//**CONSTRUCT NEW BOXES**//
		MapRandomizer mr = new MapRandomizer();
		TiledMapTileLayer boxLayer = mr.fillMap(world, tileMap, B2DVars.BOX_DENSITY);
		boxes = mr.boxes; // Update body references in boxes
		tileMap.getLayers().add(boxLayer);
		tileMap.getLayers().get(B2DVars.BOXLAYER_INDEX).setVisible(false);
		
		System.out.println("Boxes after spawning: " + boxes.size);
		
		return boxes;
		}
	
	//Check if someone has won the game
	public static boolean PlayerWon(){
		int alivePlayers = 0;
		int winner = 0;
		for(Player p : allPlayers){
			if(!p.Dead()){
				alivePlayers++;
				winner = p.GetPlayerNumber();
			}
		}
		
		boolean hasWon = alivePlayers == 1;
		
		if(hasWon)
			playerWinner = winner;
		
		//System.out.println("Alive players: " + alivePlayers + " - someone has won: " + (alivePlayers == 1));
		return hasWon;
	}
		
}

