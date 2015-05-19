package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ItemPlacer {
	
	public static void SpawnRandomPowerUp(Vector2 position){
		Random r = new Random();
		int i = r.nextInt(100);
		if(i < B2DVars.DROP_RATE){
				if(i < B2DVars.DROP_RATE / 2){
				PlacePowerUp(B2DVars.BOMB_POWERUP, position);
				return;
				}
				
				if(i >= B2DVars.DROP_RATE / 2){
				PlacePowerUp(B2DVars.FIRE_POWERUP, position);
				return;
				}
			}
	}
	
	
	/**
	 * Place the specified power up at the specified position
	 * @param type 
	 * @param position
	 */
	
	public static void PlacePowerUp(int type, Vector2 position){
		
		switch(type){
		
		case B2DVars.BOMB_POWERUP:
			
			BombPowerUp bombPow;
			
			for(BombPowerUp b : ItemPool.bombPows){
				if(!b.active){
					bombPow = b;
					bombPow.body.setTransform(position, 0);
					bombPow.active = true;
					return;
				}		
			}
			
				break;
				
		case B2DVars.FIRE_POWERUP:
			
			FirePowerUp firePow;
			
			for(FirePowerUp f : ItemPool.firePows){
				if(!f.active){
					firePow = f;
					firePow.body.setTransform(position, 0);
					firePow.active = true;
					return;
				}		
			}
				break;
			
		default:
			break;
			
		}	
	}
	
	public static void DropBomb(Player player){
		
		if(!player.CanDropBomb())
			return;
		
		BombQuery bombQuery = new BombQuery();
		int firePower = player.GetData().GetFirePower();
		Vector2 bombPosition = CoordinateConverter.quantizePositionToGrid(player.body.getPosition());
//		WORLD.QueryAABB(bombQuery, bombPosition.x - 0.2f, bombPosition.y - 0.2f, bombPosition.x + 0.2f, bombPosition.y + 0.2f);
//		
//		if(bombQuery.tileHasBomb){
//			System.out.println("Tile has bomb already.");
//			return;
//		}
		
		for(Bomb bomb : ItemPool.bombs){
			if(!bomb.active){
				
				
				//Flag bomb as active, set state to Ticking, and set firepower to players current firepower
				bomb.active = true;
				bomb.state = Bomb.State.Ticking;
				bomb.SetFirePower(firePower); 
				player.body.getPosition();
				//Quantize player position to nearest tile center and place bomb there
				bomb.body.setTransform(bombPosition, 0);
				
				player.RegisterDroppedBomb(bomb);
				return;
			}
			
		}
		
	}
	
	public static Fire SetFire(float x, float y, World world){
		Fire fire = null;
		
		for(Fire f : ItemPool.fires){
			if(!f.active){
				fire = f;
				break;
			}		
		}	
		
		//If all fires are active, create new fire
		if(fire == null){
			fire = new Fire(world, ItemPool.firePoolPosition);
			System.out.println("Fire pool buffer underrun");
		}

		fire.body.setTransform(x + 0.5f, y + 0.5f, 0);
		fire.active = true;
		
		return fire;
	}

}
