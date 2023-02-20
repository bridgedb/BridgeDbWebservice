package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperStack;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class Properties extends RestletResource{
    
    @Get("json")
	public Representation get(Variant variant) {
    	if (!supportedOrganism(urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)))) {
			String error = UNSUPPORTED_ORGANISM_TEMPLATE.replaceAll("%%ORGANISM%%", (String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM));
			StringRepresentation sr = new StringRepresentation(error);
			sr.setMediaType(MediaType.TEXT_HTML);
			return sr;
    	}
    	
		if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
			try
			{
		        JSONObject jsonObject = new JSONObject();
		        IDMapperStack stack = getIDMappers();
			    for(int i = 0; i < stack.getSize(); ++i) 
			    {
			    	IDMapper mapper = stack.getIDMapperAt(i);
			    	for (String key : mapper.getCapabilities().getKeys())
			    	{
			    		jsonObject.put(key, mapper.getCapabilities().getProperty(key));
			    	}
			    }
			    
			    return new StringRepresentation(jsonObject.toString());
			} 
			catch( Exception e ) 
			{
			    e.printStackTrace();
			    setStatus( Status.SERVER_ERROR_INTERNAL );
			    return new StringRepresentation(e.getMessage());
			}
		}
	else {
		StringBuilder result = new StringBuilder();
        IDMapperStack stack = getIDMappers();
	    for(int i = 0; i < stack.getSize(); ++i) 
	    {
	    	IDMapper mapper = stack.getIDMapperAt(i);
	    	for (String key : mapper.getCapabilities().getKeys())
	    	{
	    		result.append( key );
	    		result.append( "\t" );
	    		result.append( mapper.getCapabilities().getProperty(key) );
	    		result.append( "\n" );
	    	}
	    }
	    return new StringRepresentation(result.toString());
	}
	}
    
}
