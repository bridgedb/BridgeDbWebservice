package org.bridgedb.webservicetesting.BridgeDbWebservice;

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

public class XrefExists extends RestletResource {

	Xref xref;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			String id = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ID));
			String dsName = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_SYSTEM));
			DataSource dataSource = parseDataSource(dsName);
			if (dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
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
			IDMapper mapper = getIDMappers();
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("exists",mapper.xrefExists(xref));
				return new StringRepresentation(jsonObject.toString());
			}

			else {
				return new StringRepresentation("" + mapper.xrefExists(xref));

			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
