package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.File;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

public class RestletServer {
	
	private Component component;
	
	public void run(int port, File configFile, boolean transitive)
	{
		component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new RestletService(configFile, transitive));		
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
			boolean transitive = false;
			//File configFile = null;
			File configFile = new File("C:/Users/Helena/Documents/Projects/BridgeDb/Webservice/gdb.config");
					    
			RestletServer server = new RestletServer();
			
			server.run (port, configFile, transitive);
			}
}
