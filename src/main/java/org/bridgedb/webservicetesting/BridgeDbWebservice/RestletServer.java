package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.File;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestletServer {
	
	private Component component;

	public void run(int port, File configFile, boolean transitive, boolean cors)
	{
		component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new RestletService(configFile, transitive, cors));
		try {
			System.out.println ("Starting server on port " + port);
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		try {
			component.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 public static void main(String[] args) throws Exception {
			Class.forName ("org.bridgedb.rdb.IDMapperRdb");
			int port = 8080; // default port
			boolean cors = true;
			if(args.length > 0) port = Integer. parseInt(args[0]);
			if(args.length > 1) cors = !("false".equals(args[1]));
			boolean transitive = false;

			File configFile = new File("./gdb.config");
					    
			RestletServer server = new RestletServer();
			
			server.run (port, configFile, transitive, cors);
	 }
}
