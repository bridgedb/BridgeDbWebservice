package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class IsMappingSupported extends RestletResource {
	DataSource srcDs;
	DataSource destDs;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			// Required parameters
			String dsName = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_SOURCE_SYSTEM));
			srcDs = parseDataSource(dsName);
			if (srcDs == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			dsName = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_DEST_SYSTEM));
			destDs = parseDataSource(dsName);
			if (destDs == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}

		} catch (Exception e) {
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
		try {
			IDMapper m = getIDMappers();
			boolean supported = m.getCapabilities().isMappingSupported(srcDs, destDs);
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("supported: ", "" + supported);
				return new StringRepresentation(jsonObject.toString());
			} else {
				return new StringRepresentation("" + supported);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
