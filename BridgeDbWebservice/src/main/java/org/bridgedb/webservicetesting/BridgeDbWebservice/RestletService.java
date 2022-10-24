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
import org.restlet.routing.Extractor;
import org.restlet.routing.Filter;
import org.restlet.routing.Redirector;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;

public class RestletService extends Application{
	
	//public static final String CONF_GDBS = "gdb.config";
	public static final String CONF_GDBS = "C:/Users/Helena/Documents/Projects/BridgeDb/Webservice/gdb.config";
	public static final String PAR_ORGANISM = "organism";
	public static final String PAR_ID = "id";
	public static final String PAR_SYSTEM = "system";
	public static final String PAR_QUERY = "query";
	public static final String PAR_DATASOURCES = "datasources";

	public static final String PAR_TARGET_SYSTEM = "dataSource";
	public static final String PAR_TARGET_ATTR_NAME = "attrName";
	public static final String PAR_TARGET_LIMIT = "limit";
	
	public static final String PAR_SOURCE_SYSTEM = "src";
	public static final String PAR_DEST_SYSTEM = "dest";

	public final File configFile;
	private boolean transitive;
	
	public static final String URL_PROPERTIES = "/{" + PAR_ORGANISM + "}/properties";
	public static final String URL_ATTRIBUTES = "/{" + PAR_ORGANISM + "}/attributes/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";
	public static final String URL_DATASOURCES = "/" + PAR_DATASOURCES;
	public static final String URL_ATTRIBUTE_SET = "/{" + PAR_ORGANISM + "}/attributeSet";
	public static final String URL_ATTRIBUTE_SEARCH_WITH_LIMIT_ATTR_NAME =  "/{" + PAR_ORGANISM + "}/attributeSearch/{" + PAR_QUERY + "}/{"+PAR_TARGET_LIMIT + "}/{"+PAR_TARGET_ATTR_NAME + "}";
	public static final String URL_ATTRIBUTE_SEARCH_WITH_LIMIT = "/{" + PAR_ORGANISM + "}/attributeSearch/{" + PAR_QUERY + "}/{"+PAR_TARGET_LIMIT + "}";
	public static final String URL_ATTRIBUTE_SEARCH = "/{" + PAR_ORGANISM + "}/attributeSearch/{" + PAR_QUERY + "}";
	public static final String URL_SUPPORTED_SOURCE_DATASOURCES = "/{" + PAR_ORGANISM + "}/sourceDataSources";
	public static final String URL_SUPPORTED_TARGET_DATASOURCES = "/{" + PAR_ORGANISM + "}/targetDataSources";
	public static final String URL_IS_FREE_SEARCH_SUPPORTED = "/{" + PAR_ORGANISM + "}/isFreeSearchSupported";
	public static final String URL_IS_MAPPING_SUPPORTED = "/{" + PAR_ORGANISM + "}/isMappingSupported/{" + PAR_SOURCE_SYSTEM + "}/{" + PAR_DEST_SYSTEM + "}";
	public static final String URL_CONFIG = "/config";
	public static final String URL_CONTENTS = "/contents";
	public static final String URL_NO_MATCH = "/{" + PAR_ORGANISM + "}";
	public static final String URL_XREFS = "/{" + PAR_ORGANISM + "}/xrefs/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";
	public static final String URL_SEARCH = "/{" + PAR_ORGANISM + "}/search/{" + PAR_QUERY + "}";
	public static final String URL_XREF_EXISTS = "/{" + PAR_ORGANISM + "}/xrefExists/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";
	public static final String URL_HOME = "/";
	public static final String URL_BACK_PAGE_TEXT = "/{" + PAR_ORGANISM + "}/backPageText/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";

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
	
    @Override
    public Restlet createInboundRoot() {
        System.out.println("Creating the root");
        Router router = new Router(getContext());
        Redirector redirector = new Redirector(getContext(), URL_ATTRIBUTE_SEARCH, Redirector.MODE_CLIENT_TEMPORARY);
        Extractor extractor = new Extractor(getContext(), redirector); 
        Extractor extractorAttributeName= new Extractor(getContext()); 
        TemplateRoute route = router.attach (URL_PROPERTIES, Properties.class );

        router.attach(URL_DATASOURCES, DataSources.class);
        
		/* AttributeMapper methods */
        router.attach(URL_ATTRIBUTE_SET, AttributeSet.class);

		//Route attrSearchRoute = router.attach( URL_ATTRIBUTE_SEARCH, AttributeSearch.class );
	    //extractor.extractFromEntity(PAR_TARGET_LIMIT,PAR_TARGET_LIMIT, true);
	   // extractor.extractFromEntity(PAR_TARGET_ATTR_NAME,PAR_TARGET_ATTR_NAME, true);
        
        Route attrSearchRoute = router.attach(URL_ATTRIBUTE_SEARCH, AttributeSearch.class);
        router.attach(URL_ATTRIBUTE_SEARCH_WITH_LIMIT, AttributeSearch.class);
        router.attach(URL_ATTRIBUTE_SEARCH_WITH_LIMIT_ATTR_NAME, AttributeSearch.class);
       // extractor.extractFromQuery(PAR_QUERY, PAR_QUERY, true);
	   // router.attach("/{" + PAR_ORGANISM + "}/attributeSearch/",AttributeSearch.class);
        //router.attach("/{" + PAR_ORGANISM + "}/attributeSearch/",extractor);
        //extractor.setNext(AttributeSearch.class);
        //router.attach("/attributeSearch", extractor);
		//extractorAttributeName.extractFromEntity( PAR_TARGET_ATTR_NAME, PAR_TARGET_ATTR_NAME, true );
		Route attributesRoute = router.attach(URL_ATTRIBUTES, Attributes.class );
		//router.attach("/{" + PAR_ORGANISM + "}/attributeSearch/",extractorAttributeName);
		//extractorAttributeName.setNext(Attributes.class);
		router.attach(URL_SUPPORTED_SOURCE_DATASOURCES, SupportedSourceDataSources.class );
		router.attach(URL_SUPPORTED_TARGET_DATASOURCES, SupportedTargetDataSources.class );
		router.attach(URL_IS_FREE_SEARCH_SUPPORTED, IsFreeSearchSupported.class );
		router.attach(URL_IS_MAPPING_SUPPORTED, IsMappingSupported.class );
		
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
		
        extractor.setNext(router);

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
