package com.mygdx.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


/** Network
 * 
 *  Class is used to keep things common between server and client
 */

public class Network {
	
	//STANDARD PORT
	public static final int PORT = 54555;
	
	
	//Register packages in the same order for all
	//EndPoints (server and clients).
	public static void register(EndPoint ep) {
		Kryo kryo = ep.getKryo();
		//Register packages here
		kryo.register(Example.class);
	}
	
	public static class Example {
	}

	
}
