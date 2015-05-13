package com.mygdx.game;

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

public class Network {
	
	//STANDARD PORT
	public static final int PORT = 54555;
	
	
	//Register packages in the same order for all
	//EndPoints (server and clients).
	public static void register(EndPoint ep) {
		Kryo kryo = ep.getKryo();
		//Register packages here
		kryo.register(Login.class);
		kryo.register(AddClient.class);

	}
	
	public static class Login { public String name; }
	public static class AddClient { public MPClient client; }
}
