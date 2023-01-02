package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class Attributes extends RestletResource {
	
	Xref xref;
	String attrType;
	
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_ID));
			String dsName = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_SYSTEM));
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
			
			attrType = (String)getRequest().getAttributes().get(RestletService.PAR_TARGET_ATTR_NAME);
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}
	@Get("json")
	public Representation getAttributes(Variant variant) {
		try {
			if(attrType != null) {
				return getAttributesWithType(variant);
			} else {
				return getAttributesWithoutType(variant);
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
	
	private Representation getAttributesWithType(Variant variant) throws IDMapperException {
		if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
			IDMapperStack mapper = getIDMappers();
			JSONObject jsonObject = new JSONObject();
			Set<String> values = mapper.getAttributes(xref, attrType);
			for(String v : values) {
				jsonObject.put(attrType,v);
			}
			return new StringRepresentation(jsonObject.toString());
		}
		else {
			IDMapperStack mapper = getIDMappers();
			Set<String> values = mapper.getAttributes(xref, attrType);
			StringBuilder str = new StringBuilder();
			for(String v : values) {
				str.append(v);
				str.append("\n");
			}
			return new StringRepresentation(str.toString());
		}
	}
	
	private Representation getAttributesWithoutType(Variant variant) throws IDMapperException {
		IDMapperStack mapper = getIDMappers();
		Map<String, Set<String>> values = mapper.getAttributes(xref);
		if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
			JSONObject jsonObject = new JSONObject();
			for(String attr : values.keySet()) {
				for(String v : values.get(attr)) {
					jsonObject.put(attr, v);
				}
			}
			return new StringRepresentation(jsonObject.toString());
		}
	else {
		StringBuilder str = new StringBuilder();
		for(String attr : values.keySet()) {
			for(String v : values.get(attr)) {
				str.append(attr);
				str.append("\t");
				str.append(v);
				str.append("\n");
			}
		}
		return new StringRepresentation(str.toString());
	}
	}
	
}
