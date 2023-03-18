package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class SupportedTargetDataSources extends RestletResource {

	@Get("json")
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
				List<String> resultSet = new ArrayList<>();
				for (DataSource ds : mapper.getCapabilities().getSupportedTgtDataSources()) {
					resultSet.add(ds.getFullName());
				}
				jsonObject.put("supportedTargetDatasources", resultSet);
				return new StringRepresentation(jsonObject.toString());

			} else {
				StringBuilder result = new StringBuilder();
				for (DataSource ds : mapper.getCapabilities().getSupportedTgtDataSources()) {
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
