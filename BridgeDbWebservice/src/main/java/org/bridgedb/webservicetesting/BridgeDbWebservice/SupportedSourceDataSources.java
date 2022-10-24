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

public class SupportedSourceDataSources extends RestletResource {

	@Get("json")
	public Representation get(Variant variant) {
		try {
			IDMapper mapper = getIDMappers();
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
		        JSONObject jsonObject = new JSONObject();
		        int i=1;
				for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources()) {
					jsonObject.put("DataSource " + i + " :", ds.getFullName());
					i++;
				}
				return new StringRepresentation(jsonObject.toString());

			} else {
				StringBuilder result = new StringBuilder();
				for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources()) {
					result.append(ds.getFullName());
					result.append("\n");
				}
				return new StringRepresentation(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
