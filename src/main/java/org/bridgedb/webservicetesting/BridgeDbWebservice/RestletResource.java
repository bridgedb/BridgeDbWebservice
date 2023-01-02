package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.GdbProvider;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RestletResource extends ServerResource {
//public class RestletResource extends BaseResource {
	

  /*  @Override
    protected void doInit() throws ResourceException {
        System.out.println("Initting IDMapperResource");
    }
    */
	private IDMapperStack mappers;
	private String orgName;
	
	protected DataSource parseDataSource(String dsName) {
		if(dsName == null) return null;
		DataSource ds = null;
		//Try parsing by full name
		if(DataSource.getFullNames().contains(dsName)) {
			ds = DataSource.getExistingByFullName(dsName);
		} else { //If not possible, use system code
			ds = DataSource.getExistingBySystemCode(dsName);
		}
		return ds;
	}
	@Override
	protected void doInit() throws ResourceException {
		try {
		
		orgName = urlDecode(
				(String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)
				);
		String requestedID = urlDecode(
				(String) getRequest().getAttributes().get(RestletService.PAR_ID)
		);
		
		// Check for new HMDB identifier in request
		if(requestedID != null) {
			if (requestedID.startsWith("HMDB") && requestedID.length()==11) {
				String newId = requestedID.replace("0000", "00");
				Map<String, Object> newIdAttributes = new HashMap<>();
				newIdAttributes = getRequest().getAttributes();
				// Put newId as value for key id in order to replace the wrong HMDB id
				newIdAttributes.put("id", newId);
				// Set attributes of the request to be the new map of attributes with the correct HMDB id
				getRequest().setAttributes(newIdAttributes);
			}
		}
		
		initIDMappers();
		} catch(UnsupportedEncodingException e) {
			throw new ResourceException(e);
		}
	}
	 
	 protected String urlDecode(String string) throws UnsupportedEncodingException {
		 	System.out.println("decoding url: "+string);
			return string == null ? null : URLDecoder.decode(string, "UTF-8");
		}
		
		private void initIDMappers() {
			//System.out.println(orgName);
			Organism org = Organism.fromLatinName(orgName);
			if(org == null) { //Fallback on code
				org = Organism.fromCode(orgName);
			}
			if(org == null) { //Fallback on shortname
				org = Organism.fromShortName(orgName);
			}
			if(org == null) {
				throw new IllegalArgumentException("Unknown organism: " + orgName + "<p><font size='+1'><i>Double check the spelling. We are expecting an entry like: Human</i></font></p>");
			}
			System.out.println(org);
			mappers = getGdbProvider().getStack(org);
			if (mappers.getSize() == 0)
			{
				throw new IllegalArgumentException("No database found for: " + orgName +"<p><font size='+1'><i>Verify that the database is supported and properly referenced in gdb.config.</i></font></p>");
			}
		}
		protected IDMapperStack getIDMappers() {
			return mappers;
		}
		
		private GdbProvider getGdbProvider() {
			return ((RestletService)getApplication()).getGdbProvider();
		}
	 
}
