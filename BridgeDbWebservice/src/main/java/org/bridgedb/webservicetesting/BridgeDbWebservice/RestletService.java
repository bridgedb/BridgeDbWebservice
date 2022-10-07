package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.File;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.GdbProvider;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;

public class RestletService extends Application{
	
	//public static final String CONF_GDBS = "gdb.config";
	public static final String CONF_GDBS = "C:/Users/Helena/Documents/Projects/BridgeDb/Webservice/gdb.config";
	public static final String PAR_ORGANISM = "organism";
	public static final String PAR_ID = "id";
	public final File configFile;
	private boolean transitive;
	
	public static final String URL_PROPERTIES = "/{" + PAR_ORGANISM + "}/properties";
	
	private GdbProvider gdbProvider;
	
	public RestletService(File aConfigFile, boolean transitive)
	{
		this.transitive = transitive;
		if (aConfigFile == null)
		{
			this.configFile = new File (CONF_GDBS);
		}
		else
		{
			this.configFile = aConfigFile;
		}

		if (!configFile.exists())
		{
			System.err.println ("Could not find config file " + configFile);
			System.err.println ("Please copy org.bridgedb.server/gdb.config.template and adjust it to your needs");
			System.exit(1);
		}
	}
	
	public synchronized void start() throws Exception {
		super.start();
		DataSourceTxt.init();
		connectGdbs();
	}
	
	public Restlet createRoot() {
		Router router = new Router(getContext());
		router.attach (URL_PROPERTIES, Properties.class );
		return router;
	}
	
    @Override
    public Restlet createInboundRoot() {
        System.out.println("Creating the root");
        Router router = new Router(getContext());

        TemplateRoute route = router.attach("/{organism}/properties", Properties.class);
        TemplateRoute route2 = router.attach("/test", Properties.class);
        //route.getTemplate().setMatchingMode(Template.MODE_STARTS_WITH);
        //route2.getTemplate().setMatchingMode(Template.MODE_STARTS_WITH);
        
        Filter preferencesFilter = new Filter(getContext()) {
            protected int beforeHandle(Request request, Response response) {
            	System.out.println("MIME: " + request.getClientInfo().getAcceptedMediaTypes());
                if (request.getClientInfo().getAcceptedMediaTypes().isEmpty()) {
                    request.getClientInfo().accept(MediaType.TEXT_PLAIN);
                } else if (request.getClientInfo().getAcceptedMediaTypes().size() == 1) {
                	if (request.getClientInfo().getAcceptedMediaTypes().get(0).getMetadata().equals(MediaType.ALL)) {
                        request.getClientInfo().accept(MediaType.TEXT_PLAIN);
                    } else if (request.getClientInfo().getAcceptedMediaTypes().get(0).getMetadata().equals(MediaType.APPLICATION_ALL_JSON)) {
                        request.getClientInfo().accept(MediaType.APPLICATION_JSON);
                    }
                }
                return super.beforeHandle(request, response);
            }            
        };
        preferencesFilter.setNext(router);

        System.out.println("Returning the root");
        return router;
    }
    
	private void connectGdbs() throws IDMapperException, IOException, ClassNotFoundException 
	{
		String[] gdbconf = getContext().getParameters().getValuesArray(CONF_GDBS);
		File gdbFile = configFile;
		if(gdbconf.length > 0) {
			gdbFile = new File(gdbconf[0]);
		}
		gdbProvider = GdbProvider.fromConfigFile(gdbFile, transitive);
	}
	
	public GdbProvider getGdbProvider() {
		return gdbProvider;
	}

}
