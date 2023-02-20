package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class Xrefs extends RestletResource{
	
	Xref xref;
	DataSource targetDs;
	
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
		    System.out.println( "Xrefs.doInit start" );
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_ID));
			String dsName = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_SYSTEM));
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
			
			//Optional parameters
			String targetDsName = (String)getRequest().getAttributes().get(RestletService.PAR_TARGET_SYSTEM);
			if(targetDsName != null) {
				targetDs = parseDataSource(urlDecode(targetDsName));
			}
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}
	
	@Get("json")
	public Representation get(Variant variant) {
    	if (!supportedOrganism(urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)))) {
			String error = UNSUPPORTED_ORGANISM_TEMPLATE.replaceAll("%%ORGANISM%%", (String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM));
			StringRepresentation sr = new StringRepresentation(error);
			sr.setMediaType(MediaType.TEXT_HTML);
			return sr;
    	}

    	System.out.println( "Xrefs.getXrefs() start" );
		try {
			//The result set
			IDMapper mapper = getIDMappers();
			Set<Xref> xrefs;
			if (targetDs == null)
				xrefs = mapper.mapID(xref);
			else
				xrefs = mapper.mapID(xref, targetDs);
			if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
		        JSONObject jsonObject = new JSONObject();
				for(Xref x : xrefs) {
					jsonObject.put(x.getBioregistryIdentifier(), x.getDataSource().getFullName());
				}
				return new StringRepresentation(jsonObject.toString());
			}
			else {
			StringBuilder result = new StringBuilder();
			for(Xref x : xrefs) {
				result.append(x.getBioregistryIdentifier());
				result.append("\t");
				result.append(x.getDataSource().getFullName());
				result.append("\n");
			}
			return new StringRepresentation(result.toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}



}
