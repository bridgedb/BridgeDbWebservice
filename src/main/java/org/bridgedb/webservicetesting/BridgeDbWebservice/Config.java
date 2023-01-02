package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Properties;

import org.bridgedb.BridgeDb;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Config extends ServerResource {
	
	
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		//props.load(BridgeDb.class.getResourceAsStream("BridgeDb.properties"));
		props.load(BridgeDb.class.getResourceAsStream("version.props"));

	}

	@Get("json")
	public Representation get(Variant variant) {
		try {
			Properties props = new Properties();
			props.load(BridgeDb.class.getResourceAsStream("BridgeDb.properties"));

			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("java.version", System.getProperty("java.version"));
		        jsonObject.put("bridgedb.version", props.getProperty("bridgedb.version"));
		        jsonObject.put("bridgedb.revision", props.getProperty("REVISION"));
		        return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				result.append("java.version\t" + System.getProperty("java.version") + "\n");
				result.append("bridgedb.version\t" + props.getProperty("bridgedb.version") + "\n");
				result.append("bridgedb.revision\t" + props.getProperty("REVISION") + "\n");
				return new StringRepresentation(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
