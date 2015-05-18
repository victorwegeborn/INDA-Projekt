package com.mygdx.NGame;

import com.badlogic.gdx.Input;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


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

	}
	
	public static class RequestConnection {}
	public static class AcceptConnection {}
	public static class MovePlayer { public int direction = -1;
									 public int bomb = -1;}
	public static class UpdatedPlayer { public NPlayer updatedPlayer; }
}
