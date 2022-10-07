package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;


/**
 * Hello world!
 *
 */
public class App extends Application{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
	    Server server = new Server(Protocol.HTTP, 8080, RestletResource.class);
	    try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/helloWorld",RestletResource.class );
        return router;
    }
    
}
