package com.mygdx.NGame;
import java.util.ArrayList;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Box;
import com.mygdx.gameData.BombData;
import com.mygdx.gameData.BoxData;
import com.mygdx.gameData.FireData;
import com.mygdx.gameData.ItemData;
import com.mygdx.gameData.PlayerData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
/** Network
 * 
 *  Class is used to keep things common between server and client.
 *  Classes that are registred can be used as packages between
 *  server and client. Due to Kryonet we need to be consistant
 *  of how and in wich order we register packages.
 *  
 */
public class NNetwork {
	
	//STANDARD PORT
	public static final int PORT = 54555;
	
	
	//Register packages in the same order for all
	//EndPoints (server and clients).
	public static void register(EndPoint ep) {
		Kryo kryo = ep.getKryo();
		
		//Register packages here
		kryo.register(RequestConnection.class);
		kryo.register(AcceptConnection.class);
		kryo.register(MovePlayer.class);
		kryo.register(UpdatedPlayer.class);
		kryo.register(com.mygdx.gameData.BoxData.class);
		kryo.register(BoxUpdate.class);
		kryo.register(com.mygdx.gameData.BombData.class);
		kryo.register(BombUpdate.class);
		kryo.register(com.mygdx.gameData.PlayerData.class);
		kryo.register(PlayerUpdate.class);
		kryo.register(com.mygdx.gameData.FireData.class);
		kryo.register(FireUpdate.class);
		kryo.register(com.mygdx.gameData.ItemData.class);
		kryo.register(ItemUpdate.class);
		kryo.register(com.mygdx.gameData.ObjectData.class);
		kryo.register(com.badlogic.gdx.utils.Array.class);
		kryo.register(java.util.ArrayList.class);
		kryo.register(ShakeUpdate.class);
		kryo.register(WinScreenUpdate.class);
	}
	
	public static class RequestConnection {}
	public static class AcceptConnection {}
	public static class MovePlayer { public int direction = -1;
									 public int bomb = -1;}
	public static class UpdatedPlayer { public NPlayer updatedPlayer; }
	public static class BoxUpdate { public ArrayList<BoxData> boxes; }
	public static class FireUpdate { public ArrayList<FireData> fires; }
	public static class PlayerUpdate { public ArrayList<PlayerData> players; }
	public static class BombUpdate { public ArrayList<BombData> bombs; }
	public static class ItemUpdate { public ArrayList<ItemData> items; }
	public static class ShakeUpdate { public float shakeFactor; }
	public static class WinScreenUpdate { public int playerNr; }
	
	
}