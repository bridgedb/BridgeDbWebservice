package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class Bioregistry extends RestletResource {
	
	Xref xref;
	DataSource targetDs;

	protected DataSource parsePrefix(String prefix) {
		if(prefix == null) return null;
		return DataSource.getByCompactIdentifierPrefix(prefix);
	}

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
		    System.out.println( "Bioregistry.doInit start" );
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_ID));
			System.out.println("ID: " + id);
			String bioregistryPrefix = urlDecode((String)getRequest().getAttributes().get(RestletService.PAR_BIOREG_PREFIX));
			System.out.println("prefix: " + bioregistryPrefix);
			DataSource dataSource = parsePrefix(bioregistryPrefix);
			System.out.println("DS: " + dataSource);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown Bioregistry prefix: " + bioregistryPrefix);
			}
			xref = new Xref(id, dataSource);
			System.out.println("Xref: " + xref);
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}
	
	@Get
	public Representation get(Variant variant) {
    	if (!supportedOrganism(urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)))) {
			String error = UNSUPPORTED_ORGANISM_TEMPLATE.replaceAll("%%ORGANISM%%", (String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM));
			StringRepresentation sr = new StringRepresentation(error);
			sr.setMediaType(MediaType.TEXT_HTML);
			return sr;
    	}

    	String targetSystemCode = null;
    	Form form = getRequest().getResourceRef().getQueryAsForm();
        for (Parameter parameter : form) {
    		if ("dataSource".equals(parameter.getName())) {
    			targetSystemCode = parameter.getValue();
    		}
    	}
    	
//    	System.out.println( "Xrefs.getXrefs() start" );
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
					if (targetSystemCode == null ||
						targetSystemCode.equals(x.getDataSource().getSystemCode())) {
						jsonObject.put(x.getBioregistryIdentifier(), x.getDataSource().getFullName());
					}
				}
				return new StringRepresentation(jsonObject.toString());
			}
			else {
			    StringBuilder result = new StringBuilder();
			    for(Xref x : xrefs) {
			        if (targetSystemCode == null ||
			        	targetSystemCode.equals(x.getDataSource().getSystemCode())) {
			            result.append(x.getId());
			            result.append("\t");
			            result.append(x.getDataSource().getFullName());
			            result.append("\n");
			        }
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
