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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Properties extends RestletResource{
	
    /*
    @Get("json")
    public String helloJson() {
    	String json = "{ \"name\": \"test\", \"java\": true }";
	    JSONObject convertedObject = new Gson().fromJson(json, JSONObject.class);
	    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
	    Gson gson = builder.create();
	    String resultString = gson.toJson(convertedObject);
	    System.out.println(resultString);
	    return resultString;
    }
	
    @Get("text/plain")
    public Representation testGetDefault() {
        return new StringRepresentation("test default : - ok");

    }*/
    
	//@Get("application/json")
	@Override
	public Representation get(Variant variant) {
		if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
			System.out.println("generating JSON");
			try
			{
		        StringBuilder result = new StringBuilder();
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
		return new StringRepresentation("test default : - ok");
	}
	}
    
}
