package com.mygdx.NGame;

import com.badlogic.gdx.Input;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
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
import com.badlogic.gdx.utils.Array;
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
		kryo.register(WorldUpdate.class);
		kryo.register(com.badlogic.gdx.physics.box2d.World.class);
		kryo.register(com.badlogic.gdx.physics.box2d.Transform.class);
		kryo.register(com.badlogic.gdx.physics.box2d.MassData.class);
		kryo.register(com.badlogic.gdx.physics.box2d.BodyDef.class);
		kryo.register(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.class);
		kryo.register(com.badlogic.gdx.physics.box2d.Filter.class);
		kryo.register(com.badlogic.gdx.physics.box2d.Shape.Type.class);
		kryo.register(com.badlogic.gdx.physics.box2d.JointDef.class);
		kryo.register(com.badlogic.gdx.utils.LongMap.class);
		kryo.register(long[].class);
		kryo.register(Object[].class);
		kryo.register(com.badlogic.gdx.physics.box2d.Body.class);
		kryo.register(com.badlogic.gdx.utils.Array.class);
		kryo.register(com.badlogic.gdx.physics.box2d.Fixture.class);
	}
	
	public static class RequestConnection {}
	public static class AcceptConnection {}
	public static class MovePlayer { public int direction = -1;
									 public int bomb = -1;}
	public static class UpdatedPlayer { public NPlayer updatedPlayer; }
	public static class WorldUpdate { public Array<Body> bodies; }
}
