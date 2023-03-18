package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.GdbProvider;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class RestletResource extends ServerResource {
//public class RestletResource extends BaseResource {
	

  /*  @Override
    protected void doInit() throws ResourceException {
        System.out.println("Initting IDMapperResource");
    }
    */
	private IDMapperStack mappers;
	
	protected static String UNSUPPORTED_ORGANISM_TEMPLATE = "<html>\n"
			+ "<head>\n"
			+ "   <title>Status page</title>\n"
			+ "</head>\n"
			+ "<body style=\"font-family: sans-serif;\">\n"
			+ "<h3>Unknown organism: %%ORGANISM%%<p><font size='+1'><i>Double check the spelling. We are expecting an entry like: Human</i></font></p></h3><p>You can get technical details <a href=\"http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1\">here</a>.<br>\n"
			+ "Please continue your visit at our <a href=\"/\">home page</a>.\n"
			+ "</p>\n"
			+ "</body>\n"
			+ "</html>\n";

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
		
		String orgName = urlDecode(
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
		
		initIDMappers(orgName);
		} catch(IllegalArgumentException e) {
			if (e.getMessage().contains("Unknown organism")) return; // ignore for now
		}
	}
	 
	 protected String urlDecode(String string) {
		 // System.out.println("decoding url: "+string);
		 if (string == null) return null;
		 try {
			string = URLDecoder.decode(string, "UTF-8");
		 } catch (UnsupportedEncodingException e) {}
		 return string;
	 }

	 protected Organism findOrganism(String orgName) {
		 Organism org = Organism.fromLatinName(orgName);
		 if (org == null) { org = Organism.fromCode(orgName); } //Fallback on code
		 if (org == null) { org = Organism.fromShortName(orgName); } //Fallback on shortname
		 return org;
	 }

	 protected boolean supportedOrganism(String orgName) {
		 return findOrganism(orgName) != null;
	 }

	 private void initIDMappers(String orgName) {
		 Organism org = findOrganism(orgName);
		 if(org == null) {
			 throw new IllegalArgumentException("Unknown organism: " + orgName + "<p><font size='+1'><i>Double check the spelling. We are expecting an entry like: Human</i></font></p>");
		 }
		 // System.out.println(org);
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
