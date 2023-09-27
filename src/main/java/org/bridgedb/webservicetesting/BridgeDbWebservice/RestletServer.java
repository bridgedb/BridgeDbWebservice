package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.File;
import java.util.Properties;

import org.bridgedb.BridgeDb;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestletServer {
	
	private Component component;

	public void run(int port, File configFile, boolean transitive, boolean cors)
	{
		run(port, configFile, transitive, cors, null);
	}

	public void run(int port, File configFile, boolean transitive, boolean cors, String serverURL)
	{
		component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new RestletService(configFile, transitive, cors, serverURL));
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
    	if (args.length > 0 && "-v".equals(args[0])) {
			Properties props = new Properties();
			props.load(BridgeDb.class.getClassLoader().getResourceAsStream("version.props"));
			props.load(RestletServer.class.getClassLoader().getResourceAsStream("webservice.props"));
    		System.out.println("java.version\t" + System.getProperty("java.version"));
    		System.out.println("bridgedb.version\t" + props.getProperty("bridgedb.version"));
    		System.out.println("webservice.version\t" + props.getProperty("webservice.version"));
    		System.exit(0);
    	} else {
    		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
    		int port = 8080; // default port
    		boolean cors = true;
    		String serverURL = null;
    		if(args.length > 0) port = Integer. parseInt(args[0]);
    		if(args.length > 1) cors = !("false".equals(args[1]));
    		if(args.length > 2) serverURL = args[2];
    		boolean transitive = false;

    		File configFile = new File("./gdb.config");

    		RestletServer server = new RestletServer();

    		server.run (port, configFile, transitive, cors, serverURL);
    	}
    }
}
